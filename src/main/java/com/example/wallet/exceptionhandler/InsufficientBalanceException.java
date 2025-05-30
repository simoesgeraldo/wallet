package com.example.wallet.exceptionhandler;

public class InsufficientBalanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public InsufficientBalanceException() {
        super("Insufficient balance to carry out the operation.");
    }
	
	public InsufficientBalanceException(String msg) {
        super(msg);
        
    }

}
