package app.sencico.edu.pe.gematica.exception;

import app.sencico.edu.pe.gematica.dto.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<CommandResponse> handleCommandExecutionException(CommandExecutionException ex) {
        log.error("Error de ejecución de comando: {}", ex.getMessage());
        
        CommandResponse response = new CommandResponse();
        response.setStatus("ERROR");
        response.setExitCode(-1);
        response.setMessage(ex.getMessage());
        response.setExecutedAt(LocalDateTime.now());
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", "ERROR");
        errors.put("message", "Error de validación");
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        errors.put("errors", fieldErrors);
        errors.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommandResponse> handleGeneralException(Exception ex) {
        log.error("Error general no manejado", ex);
        
        CommandResponse response = new CommandResponse();
        response.setStatus("ERROR");
        response.setExitCode(-1);
        response.setMessage("Error interno del servidor: " + ex.getMessage());
        response.setExecutedAt(LocalDateTime.now());
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

