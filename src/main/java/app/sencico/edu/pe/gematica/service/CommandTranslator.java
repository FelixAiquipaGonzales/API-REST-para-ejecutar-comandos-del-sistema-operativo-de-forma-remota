package app.sencico.edu.pe.gematica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para traducir comandos entre diferentes sistemas operativos
 * Maneja las diferencias de sintaxis entre Windows, Linux y Mac
 */
@Component
public class CommandTranslator {
    
    private static final Logger log = LoggerFactory.getLogger(CommandTranslator.class);
    
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    
    /**
     * Traduce el comando y argumentos al formato correcto según el SO actual
     */
    public CommandTranslation translateCommand(String command, String arguments, String targetOS) {
        String actualOS = detectActualOS(targetOS);
        
        // Si no hay argumentos, retornar comando sin cambios
        if (arguments == null || arguments.trim().isEmpty()) {
            return new CommandTranslation(command, arguments);
        }
        
        // Traducir según el comando
        switch (command.toLowerCase()) {
            case "ping":
                return translatePingCommand(command, arguments, actualOS);
            case "ls":
            case "dir":
                return translateListCommand(command, arguments, actualOS);
            case "ps":
            case "tasklist":
                return translateProcessCommand(command, arguments, actualOS);
            case "ifconfig":
            case "ipconfig":
                return translateNetworkCommand(command, arguments, actualOS);
            case "grep":
            case "findstr":
                return translateSearchCommand(command, arguments, actualOS);
            case "cat":
            case "type":
                return translateReadFileCommand(command, arguments, actualOS);
            case "rm":
            case "del":
                return translateDeleteCommand(command, arguments, actualOS);
            case "cp":
            case "copy":
                return translateCopyCommand(command, arguments, actualOS);
            case "mv":
            case "move":
                return translateMoveCommand(command, arguments, actualOS);
            case "clear":
            case "cls":
                return translateClearCommand(command, arguments, actualOS);
            default:
                return new CommandTranslation(command, arguments);
        }
    }
    
    /**
     * Traduce comando ping entre sistemas operativos
     */
    private CommandTranslation translatePingCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            // Convertir sintaxis Unix a Windows
            arguments = arguments.replaceAll("-c\\s+(\\d+)", "-n $1");  // -c 4 -> -n 4
            arguments = arguments.replaceAll("-i\\s+(\\d+)", "-w $1000"); // -i 1 -> -w 1000 (segundos a milisegundos)
            arguments = arguments.replaceAll("-s\\s+(\\d+)", "-l $1");   // -s size -> -l size
            
            log.debug("Traducido ping para Windows: {}", arguments);
        } else {
            // Convertir sintaxis Windows a Unix
            arguments = arguments.replaceAll("-n\\s+(\\d+)", "-c $1");  // -n 4 -> -c 4
            arguments = arguments.replaceAll("-w\\s+(\\d+)", "-W $1");  // -w timeout -> -W timeout
            arguments = arguments.replaceAll("-l\\s+(\\d+)", "-s $1");  // -l size -> -s size
            arguments = arguments.replaceAll("-t", "-t");               // continuo es igual
            
            log.debug("Traducido ping para Unix/Linux: {}", arguments);
        }
        
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando de listado de archivos
     */
    private CommandTranslation translateListCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "dir";
            // Traducir opciones comunes de ls a dir
            arguments = arguments.replaceAll("-la?", "/a");  // -l o -la -> /a
            arguments = arguments.replaceAll("-R", "/s");    // recursivo
            arguments = arguments.replaceAll("-h", "");       // no hay equivalente directo
        } else {
            command = "ls";
            // Traducir opciones comunes de dir a ls
            arguments = arguments.replaceAll("/a", "-la");
            arguments = arguments.replaceAll("/s", "-R");
            arguments = arguments.replaceAll("/b", "-1");     // bare format -> one per line
        }
        
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando de listado de procesos
     */
    private CommandTranslation translateProcessCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "tasklist";
            // Traducir opciones de ps
            if (arguments.contains("-e") || arguments.contains("aux")) {
                arguments = "/v";  // verbose
            }
        } else {
            command = "ps";
            // Traducir opciones de tasklist
            if (arguments.contains("/v")) {
                arguments = "aux";
            }
        }
        
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando de configuración de red
     */
    private CommandTranslation translateNetworkCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "ipconfig";
            if (arguments.contains("-a")) {
                arguments = "/all";
            }
        } else {
            command = "ifconfig";
            if (arguments.contains("/all")) {
                arguments = "-a";
            }
        }
        
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando de búsqueda en texto
     */
    private CommandTranslation translateSearchCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "findstr";
            // Traducir opciones de grep
            arguments = arguments.replaceAll("-i", "/i");  // case insensitive
            arguments = arguments.replaceAll("-r", "/s");  // recursive
            arguments = arguments.replaceAll("-n", "/n");  // line numbers
        } else {
            command = "grep";
            // Traducir opciones de findstr
            arguments = arguments.replaceAll("/i", "-i");
            arguments = arguments.replaceAll("/s", "-r");
            arguments = arguments.replaceAll("/n", "-n");
        }
        
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando para leer archivos
     */
    private CommandTranslation translateReadFileCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "type";
        } else {
            command = "cat";
        }
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando para eliminar archivos
     */
    private CommandTranslation translateDeleteCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "del";
            arguments = arguments.replaceAll("-r", "/s");  // recursive
            arguments = arguments.replaceAll("-f", "/f");  // force
        } else {
            command = "rm";
            arguments = arguments.replaceAll("/s", "-r");
            arguments = arguments.replaceAll("/f", "-f");
        }
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando para copiar archivos
     */
    private CommandTranslation translateCopyCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "copy";
            arguments = arguments.replaceAll("-r", "/s");  // recursive
        } else {
            command = "cp";
            arguments = arguments.replaceAll("/s", "-r");
        }
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando para mover archivos
     */
    private CommandTranslation translateMoveCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            command = "move";
        } else {
            command = "mv";
        }
        return new CommandTranslation(command, arguments);
    }
    
    /**
     * Traduce comando para limpiar pantalla
     */
    private CommandTranslation translateClearCommand(String command, String arguments, String os) {
        if (isWindows(os)) {
            return new CommandTranslation("cls", "");
        } else {
            return new CommandTranslation("clear", "");
        }
    }
    
    /**
     * Detecta el sistema operativo actual
     */
    private String detectActualOS(String requestedOS) {
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
    
    /**
     * Verifica si el SO es Windows
     */
    private boolean isWindows(String os) {
        return "WINDOWS".equalsIgnoreCase(os) || os.toLowerCase().contains("win");
    }
    
    /**
     * Clase para encapsular la traducción de comando
     */
    public static class CommandTranslation {
        private final String command;
        private final String arguments;
        
        public CommandTranslation(String command, String arguments) {
            this.command = command;
            this.arguments = arguments;
        }
        
        public String getCommand() {
            return command;
        }
        
        public String getArguments() {
            return arguments;
        }
    }
    
    /**
     * Mapa de equivalencias de comandos entre sistemas
     */
    public Map<String, String> getCommandEquivalencies() {
        Map<String, String> equivalencies = new HashMap<>();
        
        // Windows -> Unix/Linux
        equivalencies.put("dir", "ls");
        equivalencies.put("type", "cat");
        equivalencies.put("del", "rm");
        equivalencies.put("copy", "cp");
        equivalencies.put("move", "mv");
        equivalencies.put("cls", "clear");
        equivalencies.put("ipconfig", "ifconfig");
        equivalencies.put("tasklist", "ps");
        equivalencies.put("findstr", "grep");
        
        // Unix/Linux -> Windows
        equivalencies.put("ls", "dir");
        equivalencies.put("cat", "type");
        equivalencies.put("rm", "del");
        equivalencies.put("cp", "copy");
        equivalencies.put("mv", "move");
        equivalencies.put("clear", "cls");
        equivalencies.put("ifconfig", "ipconfig");
        equivalencies.put("ps", "tasklist");
        equivalencies.put("grep", "findstr");
        
        return equivalencies;
    }
}

