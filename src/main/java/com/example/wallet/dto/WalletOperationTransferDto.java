package com.example.wallet.dto;

import java.util.Objects;

import com.example.wallet.exceptionhandler.WalletNotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnore;

public record WalletOperationTransferDto ( @JsonIgnore Long origin, @JsonIgnore Long destination, WalletOperationDto walletOperation) {
	
	
	public WalletOperationTransferDto withOriginAndDestination(Long origin, Long destination) {
		if(Objects.isNull(origin) || Objects.isNull(destination)) {
			throw new WalletNotFoundException();
		}
		 return new WalletOperationTransferDto(origin, destination, walletOperation());
	}
	
	
	
	
}
