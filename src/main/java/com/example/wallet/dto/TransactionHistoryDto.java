package com.example.wallet.dto;

import java.util.List;

import com.example.wallet.dto.transaction.TransactionDto;

public class TransactionHistoryDto {
	
	private WalletDto wallet;
	private List<TransactionDto> transactios;
	
	public TransactionHistoryDto(List<TransactionDto> transactionDtos) {
		
		this.wallet = transactionDtos.stream()
			    .map(TransactionDto::wallet)
			    .findFirst()
			    .orElse(null);
		this.transactios = transactionDtos;
		
	}

	public WalletDto getWallet() {
		return wallet;
	}

	public void setWallet(WalletDto wallet) {
		this.wallet = wallet;
	}

	public List<TransactionDto> getTransactios() {
		return transactios;
	}

	public void setTransactios(List<TransactionDto> transactios) {
		this.transactios = transactios;
	}
	
	
	
	

}
