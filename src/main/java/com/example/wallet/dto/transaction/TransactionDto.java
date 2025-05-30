package com.example.wallet.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.wallet.dto.WalletDto;
import com.example.wallet.model.TransactionType;

public record TransactionDto(Long id, WalletDto wallet, BigDecimal amount, LocalDateTime transactionDate, TransactionType type,
		String description, String operationCode ) {
	
	public TransactionDto withType(TransactionType type) {
		return new TransactionDto( id(), wallet(), amount(), LocalDateTime.now(), type,
				 description(), operationCode() );
	}
}
