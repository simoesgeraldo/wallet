package com.example.wallet.exceptionhandler;

public class WalletDataIntegrityViolationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
	public WalletDataIntegrityViolationException(String owner) {
        super("This request has already been processed " + owner  );
    }
}
