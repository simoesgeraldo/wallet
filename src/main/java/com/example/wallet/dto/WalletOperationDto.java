package com.example.wallet.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WalletOperationDto (
	
	@JsonIgnore
	Long walletId,
	
	@NotNull(message = "The value is mandatory")
    @Positive(message = "The value must be positive")
	BigDecimal amount,
	
	@NotBlank(message = "Operation code cannot be not blank")
	@NotEmpty(message = "Operation code cannot be not empty")
	String operationCode,
	
	String description
	) {

	public WalletOperationDto withWalletId(Long walletId) {
		return new WalletOperationDto(walletId, amount(), operationCode(), description());
	}
	
	
	
	
	
}
