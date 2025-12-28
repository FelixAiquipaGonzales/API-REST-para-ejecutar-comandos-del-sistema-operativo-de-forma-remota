package app.sencico.edu.pe.gematica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de solicitud para ejecutar comandos del sistema")
public class CommandRequest {
    
    @NotBlank(message = "El comando no puede estar vacío")
    @Schema(
        description = "Comando a ejecutar en el sistema",
        example = "dir",
        required = true
    )
    private String command;
    
    @Schema(
        description = "Argumentos del comando (opcional)",
        example = "/a /b"
    )
    private String arguments;
    
    @Schema(
        description = "Directorio de trabajo (opcional). Si no se especifica, se usa el directorio actual",
        example = "C:/temp"
    )
    private String workingDirectory;
    
    @Schema(
        description = "Tiempo máximo de espera en segundos (por defecto 30)",
        example = "60"
    )
    private Integer timeout = 30;
    
    @Schema(
        description = "Sistema operativo específico (opcional). Si no se especifica, detecta automáticamente. Valores: WINDOWS, LINUX, MAC",
        example = "WINDOWS"
    )
    @Pattern(regexp = "^(WINDOWS|LINUX|MAC|AUTO)$", message = "Sistema operativo debe ser WINDOWS, LINUX, MAC o AUTO")
    private String operatingSystem = "AUTO";

    // Constructor sin parámetros
	public CommandRequest() {
		this.timeout = 30;
		this.operatingSystem = "AUTO";
	}

	// Constructor con todos los parámetros
	public CommandRequest(String command, String arguments, String workingDirectory, Integer timeout, String operatingSystem) {
		this.command = command;
		this.arguments = arguments;
		this.workingDirectory = workingDirectory;
		this.timeout = timeout;
		this.operatingSystem = operatingSystem;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

}
