package app.sencico.edu.pe.gematica.controller;

import app.sencico.edu.pe.gematica.dto.CommandRequest;
import app.sencico.edu.pe.gematica.dto.CommandResponse;
import app.sencico.edu.pe.gematica.service.CommandExecutorService;
import app.sencico.edu.pe.gematica.exception.CommandExecutionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/command")
@Tag(name = "Command Executor", description = "API para ejecutar comandos del sistema operativo")
@CrossOrigin(origins = "*")
public class CommandController {
    
    private static final Logger log = LoggerFactory.getLogger(CommandController.class);
    private final CommandExecutorService commandExecutorService;
    
    // Constructor para inyección de dependencias
    public CommandController(CommandExecutorService commandExecutorService) {
        this.commandExecutorService = commandExecutorService;
    }
    
    @PostMapping("/execute")
    @Operation(
        summary = "Ejecutar comando del sistema",
        description = "Ejecuta un comando del sistema operativo y devuelve el resultado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comando ejecutado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CommandResponse> executeCommand(
            @Valid @RequestBody CommandRequest request) {
        
        log.info("Recibida solicitud para ejecutar comando: {}", request.getCommand());
        
        try {
            CommandResponse response = commandExecutorService.executeCommand(request);
            return ResponseEntity.ok(response);
        } catch (CommandExecutionException e) {
            log.error("Error al ejecutar comando: {}", e.getMessage());
            
            CommandResponse errorResponse = new CommandResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setExitCode(-1);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setExecutedCommand(request.getCommand());
                    
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            log.error("Error inesperado al ejecutar comando", e);
            
            CommandResponse errorResponse = new CommandResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setExitCode(-1);
            errorResponse.setMessage("Error inesperado: " + e.getMessage());
            errorResponse.setExecutedCommand(request.getCommand());
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/info")
    @Operation(
        summary = "Obtener información del sistema",
        description = "Devuelve información sobre el sistema operativo actual"
    )
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("os.name", System.getProperty("os.name"));
        info.put("os.version", System.getProperty("os.version"));
        info.put("os.arch", System.getProperty("os.arch"));
        info.put("java.version", System.getProperty("java.version"));
        info.put("user.dir", System.getProperty("user.dir"));
        info.put("user.home", System.getProperty("user.home"));
        info.put("java.home", System.getProperty("java.home"));
        
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/available-commands")
    @Operation(
        summary = "Listar comandos disponibles",
        description = "Devuelve una lista de comandos comunes disponibles para el sistema operativo actual"
    )
    public ResponseEntity<List<String>> getAvailableCommands() {
        List<String> commands = commandExecutorService.getAvailableCommands();
        return ResponseEntity.ok(commands);
    }
    
    @GetMapping("/health")
    @Operation(
        summary = "Verificar estado del servicio",
        description = "Endpoint para verificar que el servicio está funcionando"
    )
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Command Executor Service");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(health);
    }
    
    @PostMapping("/execute/simple")
    @Operation(
        summary = "Ejecutar comando simple",
        description = "Ejecuta un comando simple especificado como parámetro de consulta"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comando ejecutado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Comando inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CommandResponse> executeSimpleCommand(
            @Parameter(description = "Comando a ejecutar", required = true)
            @RequestParam String command,
            @Parameter(description = "Argumentos del comando")
            @RequestParam(required = false) String arguments,
            @Parameter(description = "Tiempo máximo de espera en segundos")
            @RequestParam(required = false, defaultValue = "30") Integer timeout) {
        
        log.info("Ejecutando comando simple: {}", command);
        
        CommandRequest request = new CommandRequest();
        request.setCommand(command);
        request.setArguments(arguments);
        request.setTimeout(timeout);
        
        try {
            CommandResponse response = commandExecutorService.executeCommand(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al ejecutar comando simple", e);
            
            CommandResponse errorResponse = new CommandResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setExitCode(-1);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setExecutedCommand(command);
                    
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
