package com.example.wallet.exceptionhandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalControllerExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

	   @ExceptionHandler(WalletNotFoundException.class)
	    public ResponseEntity<?> handleWalletNotFound(WalletNotFoundException ex) {
	        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	    }

	    @ExceptionHandler(InsufficientBalanceException.class)
	    public ResponseEntity<?> handleInsufficientBalance(InsufficientBalanceException ex) {
	        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(IllegalArgumentException.class)
	    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
	        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	    
	    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	    public ResponseEntity<String> handleBookNotFound(ObjectOptimisticLockingFailureException ex) {
	    	logger.error(ex.getMessage());
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
	        Map<String, String> erros = new HashMap<>();

	        ex.getBindingResult().getAllErrors().forEach(erro -> {
	            String campo = ((FieldError) erro).getField();
	            String mensagem = erro.getDefaultMessage();
	            erros.put(campo, mensagem);
	        });

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
	    }
	    @ExceptionHandler(WalletDataIntegrityViolationException.class)
	    public ResponseEntity<?> handleConstraintViolation(WalletDataIntegrityViolationException ex) {
	    	 return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
	    }
	    
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<?> handleGenericException(Exception ex) {
	    	logger.error(""+ex);
	        return buildResponse("Erro interno do servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
	        Map<String, Object> body = new HashMap<>();
	        body.put("timestamp", LocalDateTime.now());
	        body.put("status", status.value());
	        body.put("error", status.getReasonPhrase());
	        body.put("message", message);
	        
	        logger.error(message);
	        
	        return new ResponseEntity<>(body, status);
	    }
   
    
    
}
