package com.example.wallet.exceptionhandler;

public class WalletNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public WalletNotFoundException(Long id) {
        super("Wallet with ID " + id + " not found.");
    }
	public WalletNotFoundException() {
        super("We were unable to process your request to verify data");
    }

}
