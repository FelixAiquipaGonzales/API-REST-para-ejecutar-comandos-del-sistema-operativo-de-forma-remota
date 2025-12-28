package app.sencico.edu.pe.gematica.service;

import app.sencico.edu.pe.gematica.dto.CommandRequest;
import app.sencico.edu.pe.gematica.dto.CommandResponse;
import app.sencico.edu.pe.gematica.exception.CommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CommandExecutorService {
    
    private static final Logger log = LoggerFactory.getLogger(CommandExecutorService.class);
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private final CommandTranslator commandTranslator;
    
    // Constructor para inyección de dependencias
    public CommandExecutorService(CommandTranslator commandTranslator) {
        this.commandTranslator = commandTranslator;
    }
    
    public CommandResponse executeCommand(CommandRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Detectar sistema operativo
            String operatingSystem = detectOperatingSystem(request.getOperatingSystem());
            
            // Traducir comando según el SO actual
            CommandTranslator.CommandTranslation translation = commandTranslator.translateCommand(
                request.getCommand(), 
                request.getArguments(), 
                operatingSystem
            );
            
            // Construir el comando según el SO con el comando traducido
            List<String> command = buildCommand(translation.getCommand(), translation.getArguments(), operatingSystem);
            
            // Configurar ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            
            // Establecer directorio de trabajo si se especifica
            if (request.getWorkingDirectory() != null && !request.getWorkingDirectory().isEmpty()) {
                File workDir = new File(request.getWorkingDirectory());
                if (!workDir.exists() || !workDir.isDirectory()) {
                    throw new CommandExecutionException("El directorio de trabajo no existe: " + request.getWorkingDirectory());
                }
                processBuilder.directory(workDir);
            }
            
            // Combinar error y output streams
            processBuilder.redirectErrorStream(false);
            
            log.info("Ejecutando comando: {} en SO: {}", command, operatingSystem);
            
            // Ejecutar el comando
            Process process = processBuilder.start();
            
            // Leer la salida
            String output = readStream(new BufferedReader(new InputStreamReader(process.getInputStream())));
            String errorOutput = readStream(new BufferedReader(new InputStreamReader(process.getErrorStream())));
            
            // Esperar a que termine con timeout
            boolean finished = process.waitFor(request.getTimeout(), TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                throw new CommandExecutionException("El comando excedió el tiempo de espera de " + request.getTimeout() + " segundos");
            }
            
            int exitCode = process.exitValue();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Construir respuesta
            CommandResponse response = new CommandResponse();
            response.setStatus(exitCode == 0 ? "SUCCESS" : "ERROR");
            response.setExitCode(exitCode);
            response.setOutput(output);
            response.setErrorOutput(errorOutput);
            response.setExecutedCommand(String.join(" ", command));
            response.setOperatingSystem(System.getProperty("os.name") + " " + System.getProperty("os.version"));
            response.setExecutionTime(executionTime);
            response.setExecutedAt(LocalDateTime.now());
            response.setWorkingDirectory(processBuilder.directory() != null ? 
                    processBuilder.directory().getAbsolutePath() : 
                    System.getProperty("user.dir"));
            response.setMessage(exitCode == 0 ? "Comando ejecutado exitosamente" : "El comando terminó con código de error " + exitCode);
            return response;
                    
        } catch (CommandExecutionException e) {
            log.error("Error al ejecutar comando: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al ejecutar comando", e);
            long executionTime = System.currentTimeMillis() - startTime;
            
            CommandResponse errorResponse = new CommandResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setExitCode(-1);
            errorResponse.setExecutedCommand(request.getCommand());
            errorResponse.setOperatingSystem(System.getProperty("os.name") + " " + System.getProperty("os.version"));
            errorResponse.setExecutionTime(executionTime);
            errorResponse.setExecutedAt(LocalDateTime.now());
            errorResponse.setMessage("Error al ejecutar comando: " + e.getMessage());
            errorResponse.setWorkingDirectory(request.getWorkingDirectory());
            return errorResponse;
        }
    }
    
    private String detectOperatingSystem(String requestedOS) {
        if (requestedOS == null || "AUTO".equalsIgnoreCase(requestedOS)) {
            if (OS_NAME.contains("win")) {
                return "WINDOWS";
            } else if (OS_NAME.contains("mac")) {
                return "MAC";
            } else if (OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.contains("aix")) {
                return "LINUX";
            }
        }
        return requestedOS;
    }
    
    private List<String> buildCommand(String command, String arguments, String operatingSystem) {
        List<String> commandList = new ArrayList<>();
        
        switch (operatingSystem.toUpperCase()) {
            case "WINDOWS":
                commandList.add("cmd");
                commandList.add("/c");
                break;
            case "LINUX":
            case "MAC":
                commandList.add("/bin/sh");
                commandList.add("-c");
                break;
            default:
                // Intentar detectar automáticamente
                if (OS_NAME.contains("win")) {
                    commandList.add("cmd");
                    commandList.add("/c");
                } else {
                    commandList.add("/bin/sh");
                    commandList.add("-c");
                }
        }
        
        // Agregar comando y argumentos
        if (arguments != null && !arguments.isEmpty()) {
            commandList.add(command + " " + arguments);
        } else {
            commandList.add(command);
        }
        
        return commandList;
    }
    
    private String readStream(BufferedReader reader) throws IOException {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }
        return output.toString();
    }
    
    public List<String> getAvailableCommands() {
        List<String> commands = new ArrayList<>();
        
        if (OS_NAME.contains("win")) {
            // Comandos comunes de Windows
            commands.add("ping google.com -n 4 | Hace 4 pings a google.com");
            commands.add("dir /b | Lista archivos y directorios (formato simple)");
            commands.add("ipconfig /all | Muestra configuración completa de red");
            commands.add("netstat -an | Muestra todas las conexiones y puertos");
            commands.add("tasklist /v | Lista procesos con información detallada");
            commands.add("systeminfo | Información completa del sistema");
            commands.add("echo Hola Mundo | Muestra texto en pantalla");
            commands.add("type archivo.txt | Muestra contenido de archivo");
            commands.add("hostname | Muestra nombre del equipo");
            commands.add("whoami | Muestra usuario actual");
            commands.add("date /t | Muestra fecha actual");
            commands.add("time /t | Muestra hora actual");
            commands.add("cls | Limpia la pantalla");
        } else {
            // Comandos comunes de Unix/Linux/Mac
            commands.add("ping google.com -c 4 | Hace 4 pings a google.com");
            commands.add("ls -la | Lista archivos detallada incluyendo ocultos");
            commands.add("pwd | Muestra directorio actual");
            commands.add("ifconfig -a | Muestra toda la configuración de red");
            commands.add("netstat -an | Muestra todas las conexiones y puertos");
            commands.add("ps aux | Lista todos los procesos del sistema");
            commands.add("uname -a | Información completa del sistema");
            commands.add("echo 'Hola Mundo' | Muestra texto en pantalla");
            commands.add("cat archivo.txt | Muestra contenido de archivo");
            commands.add("hostname | Muestra nombre del equipo");
            commands.add("whoami | Muestra usuario actual");
            commands.add("date | Muestra fecha y hora actual");
            commands.add("clear | Limpia la pantalla");
        }
        
        // Agregar nota sobre traducción automática
        commands.add("---");
        commands.add("NOTA: Los comandos se traducen automáticamente entre sistemas operativos");
        commands.add("Ejemplo: 'ping -c 4' en Linux se convierte a 'ping -n 4' en Windows");
        
        return commands;
    }
}
