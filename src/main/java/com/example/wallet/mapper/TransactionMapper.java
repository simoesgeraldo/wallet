package com.example.wallet.mapper;

import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;
import com.example.wallet.dto.transaction.TransactionDto;
import com.example.wallet.model.Transaction;
import com.example.wallet.model.Wallet;

public class TransactionMapper {
	
	public static TransactionDto toDto(Transaction transaction){
		if (transaction == null) return null;
		return new TransactionDto(transaction.getId(),  WalletMapper.toDto(transaction.getWallet()), transaction.getAmount(), transaction.getTransactionDate(), transaction.getType(), 
				transaction.getDescription(), transaction.getIdempotencyKey());
	}
	
	public static Transaction toEntiy(TransactionDto transactionDto){
		if (transactionDto == null) return null;
		Transaction transaction = new Transaction();
		transaction.setId(transactionDto.id());
		transaction.setWallet(WalletMapper.toEntiy(transactionDto.wallet()));
		transaction.setAmount(transactionDto.amount());
		transaction.setTransactionDate(transactionDto.transactionDate());
		transaction.setType(transactionDto.type()); 
		transaction.setDescription(transactionDto.description());
		transaction.setIdempotencyKey(transactionDto.operationCode());
		return transaction;
	}
	
	public static TransactionDto toDto(Wallet wallet, WalletOperationDto walletOperationDto) {
        return toDto(WalletMapper.toDto(wallet), walletOperationDto);
    }

    public static TransactionDto toDto(WalletDto walletDto, WalletOperationDto walletOperationDto) {
        return buildTransactionDto(walletDto, walletOperationDto);
    }

    private static TransactionDto buildTransactionDto(WalletDto walletDto, WalletOperationDto walletOperationDto) {
        return new TransactionDto(
                null, 
                walletDto,
                walletOperationDto.amount(),
                null,
                null, 
                walletOperationDto.description(),
                walletOperationDto.operationCode()
        );
    }
	

}
