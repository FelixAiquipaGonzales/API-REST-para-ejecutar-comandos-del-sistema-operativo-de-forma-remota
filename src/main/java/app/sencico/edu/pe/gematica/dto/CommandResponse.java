package app.sencico.edu.pe.gematica.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Objeto de respuesta con el resultado de la ejecución del comando")
public class CommandResponse {
    
    @Schema(description = "Estado de la ejecución", example = "SUCCESS")
    private String status;
    
    @Schema(description = "Código de salida del comando", example = "0")
    private Integer exitCode;
    
    @Schema(description = "Salida estándar del comando")
    private String output;
    
    @Schema(description = "Salida de error del comando (si existe)")
    private String errorOutput;
    
    @Schema(description = "Comando ejecutado", example = "dir /a")
    private String executedCommand;
    
    @Schema(description = "Sistema operativo donde se ejecutó", example = "Windows 10")
    private String operatingSystem;
    
    @Schema(description = "Tiempo de ejecución en milisegundos", example = "1250")
    private Long executionTime;
    
    @Schema(description = "Fecha y hora de ejecución")
    private LocalDateTime executedAt;
    
    @Schema(description = "Mensaje adicional o de error")
    private String message;
    
    @Schema(description = "Directorio de trabajo usado")
    private String workingDirectory;
    
    // Constructor sin parámetros
    public CommandResponse() {
    }
    
    // Constructor con todos los parámetros
    public CommandResponse(String status, Integer exitCode, String output, String errorOutput,
                          String executedCommand, String operatingSystem, Long executionTime,
                          LocalDateTime executedAt, String message, String workingDirectory) {
        this.status = status;
        this.exitCode = exitCode;
        this.output = output;
        this.errorOutput = errorOutput;
        this.executedCommand = executedCommand;
        this.operatingSystem = operatingSystem;
        this.executionTime = executionTime;
        this.executedAt = executedAt;
        this.message = message;
        this.workingDirectory = workingDirectory;
    }
    
    // Getters y Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getExitCode() {
        return exitCode;
    }
    
    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }
    
    public String getOutput() {
        return output;
    }
    
    public void setOutput(String output) {
        this.output = output;
    }
    
    public String getErrorOutput() {
        return errorOutput;
    }
    
    public void setErrorOutput(String errorOutput) {
        this.errorOutput = errorOutput;
    }
    
    public String getExecutedCommand() {
        return executedCommand;
    }
    
    public void setExecutedCommand(String executedCommand) {
        this.executedCommand = executedCommand;
    }
    
    public String getOperatingSystem() {
        return operatingSystem;
    }
    
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
    
    public Long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }
    
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
    
    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getWorkingDirectory() {
        return workingDirectory;
    }
    
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}

