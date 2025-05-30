package com.example.wallet.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.wallet.dto.MessageDto;
import com.example.wallet.dto.transaction.TransactionDto;
import com.example.wallet.dto.transaction.TransactionTransferDto;

public interface TransationService {

	MessageDto deposit(TransactionDto transacion);
	MessageDto withdraw(TransactionDto transacion);
	MessageDto transfer(TransactionTransferDto transectionTransfer );
	List<TransactionDto> transactionHistory(Long walletId, LocalDateTime startDate, LocalDateTime endDate);
	void findByIdempotencyKey(String idempotencyKey);
	void deleteAll();
}
