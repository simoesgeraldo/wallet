package com.example.wallet.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.wallet.dto.MessageDto;
import com.example.wallet.dto.transaction.TransactionDto;
import com.example.wallet.dto.transaction.TransactionTransferDto;
import com.example.wallet.exceptionhandler.WalletDataIntegrityViolationException;
import com.example.wallet.mapper.TransactionMapper;
import com.example.wallet.model.Transaction;
import com.example.wallet.model.TransactionType;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.service.TransationService;

@Service
public class TransationServiceImpl implements TransationService {

	private static final Logger log = LoggerFactory.getLogger(TransationServiceImpl.class);
	private final TransactionRepository transactionRepo;
	
	public TransationServiceImpl(TransactionRepository transactionRepo) {
		super();
		this.transactionRepo = transactionRepo;
	}

	@Override
	public MessageDto deposit(TransactionDto transacion) {
		log.info("Recording the deposit transaction");
		TransactionDto dto = transacion.withType(TransactionType.DEPOSIT);
		Transaction tx = TransactionMapper.toEntiy(dto);
		transactionRepo.save(tx);
		log.info("Wallet {} deposit amount {} with operator code {}", transacion.wallet().getId(), transacion.amount(), transacion.operationCode());
		return new MessageDto(transacion.wallet().getId(), transacion.wallet().getOwner(), transacion.amount(), "Deposit made successfully.");
	}

	@Override
	public MessageDto withdraw(TransactionDto transacion) {
		log.info("Withdrawing funds from the wallet");
		TransactionDto dto = transacion.withType(TransactionType.WITHDRAW);
		Transaction tx = TransactionMapper.toEntiy(dto);
		transactionRepo.save(tx);
		log.info("Wallet {} withdraw amount {} with operator code {}", transacion.wallet().getId() , transacion.amount(), transacion.operationCode());
		return new MessageDto(transacion.wallet().getId(), transacion.wallet().getOwner(), transacion.amount(), "Withdrawal successful");
	}

	@Override
	public MessageDto transfer(TransactionTransferDto transaction) {
		log.info("Transfer of funds");
		TransactionDto outTxdto = new TransactionDto(null,transaction.fromWallet(), transaction.walletOperation().amount(), LocalDateTime.now(),TransactionType.TRANSFER_OUT, "Transfer from wallet "+ 
		transaction.fromWallet().getId(), transaction.walletOperation().operationCode());
		Transaction outTx = TransactionMapper.toEntiy(outTxdto);
		transactionRepo.save(outTx);
		log.info("Wallet {} transfer Amount {} OperatorCode {}", transaction.fromWallet().getId(), transaction.walletOperation().amount(), transaction.walletOperation().operationCode());
		TransactionDto inTxdto = new TransactionDto(null,transaction.toWallet(), transaction.walletOperation().amount(), LocalDateTime.now(),TransactionType.TRANSFER_IN, "Transfer to wallet " + transaction.toWallet().getId(), transaction.walletOperation().operationCode());
		Transaction inTx = TransactionMapper.toEntiy(inTxdto);
		transactionRepo.save(inTx);
		log.info("Wallet {} sent an amount to wallet {} in the amount of {} with operator code {}", transaction.fromWallet().getId(), transaction.toWallet().getId() , transaction.walletOperation().amount(), transaction.walletOperation().operationCode() );
		return new MessageDto(transaction.toWallet().getId(), transaction.toWallet().getOwner(), transaction.walletOperation().amount(), "Transfer completed successfully");
		
	}
	
	@Override
	public List<TransactionDto> transactionHistory(Long walletId, LocalDateTime startDate, LocalDateTime endDate) {
		log.info("Viewing account history");
        return transactionRepo.findByWalletIdAndTransactionDateBetween(walletId, startDate, endDate).stream().map(TransactionMapper::toDto).toList();
    }

	@Override
	public void findByIdempotencyKey(String idempotencyKey) {
		List<Transaction> transaction = transactionRepo.findByIdempotencyKey(idempotencyKey);
		if(!transaction.isEmpty()) {
			throw new WalletDataIntegrityViolationException( idempotencyKey);
		}
	}

	@Override
	public void deleteAll() {
		transactionRepo.deleteAll();
	}

}
