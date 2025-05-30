package com.example.wallet.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.wallet.dto.MessageDto;
import com.example.wallet.dto.TransactionHistoryDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;
import com.example.wallet.dto.WalletOperationTransferDto;

public interface WalletService {
	 WalletDto createWallet(WalletDto wallet) ;
	 MessageDto deposit(WalletOperationDto model);
	 MessageDto withdraw(WalletOperationDto model);
	 MessageDto transfer(WalletOperationTransferDto model);
	 TransactionHistoryDto transactionHistory(Long walletId, LocalDateTime startDate, LocalDateTime endDate);
	 Optional<WalletDto> getWallet(Long id) ;
	 
	 void deleteAll();
}
