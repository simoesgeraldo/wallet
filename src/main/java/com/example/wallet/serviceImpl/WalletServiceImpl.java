package com.example.wallet.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.wallet.dto.MessageDto;
import com.example.wallet.dto.TransactionHistoryDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;
import com.example.wallet.dto.WalletOperationTransferDto;
import com.example.wallet.dto.transaction.TransactionTransferDto;
import com.example.wallet.exceptionhandler.InsufficientBalanceException;
import com.example.wallet.exceptionhandler.WalletDataIntegrityViolationException;
import com.example.wallet.exceptionhandler.WalletNotFoundException;
import com.example.wallet.mapper.TransactionMapper;
import com.example.wallet.mapper.WalletMapper;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.service.TransationService;
import com.example.wallet.service.WalletService;

import jakarta.transaction.Transactional;

@Service
public class WalletServiceImpl implements WalletService{

	private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);
	private final WalletRepository walletRepo;
	private final TransationService transationService;
	
	public WalletServiceImpl(WalletRepository walletRepo,  TransationService transationService) {
		this.walletRepo = walletRepo;
		this.transationService = transationService;
	}
	
	@Transactional
	public WalletDto createWallet(WalletDto walletDto) {
		try {
			log.info("Created a new wallet");
			Wallet wallet = new Wallet();
			wallet.setOwner(walletDto.getOwner());
			walletRepo.save(wallet);
			return WalletMapper.toDto(wallet);
		} catch (Exception e) {
			throw new WalletDataIntegrityViolationException(walletDto.getOwner());
			
		}
	}

	public Optional<WalletDto> getWallet(Long id) {
		log.info("Retrieve the current balance of a user's wallet");
		return Optional.of(walletRepo.findById(id).map(WalletMapper::toDto).orElseThrow(() -> new WalletNotFoundException(id)));
	}
	@Transactional
	public MessageDto deposit(WalletOperationDto model) {
		log.info("Depositing value into the wallet");
		
		checkIfTheNumberIsNegative(model.amount());
		transationService.findByIdempotencyKey(model.operationCode());
		
		Wallet wallet = walletRepo.findById(model.walletId()).orElseThrow(() -> new WalletNotFoundException(model.walletId()));
		wallet.setBalance(wallet.getBalance().add(model.amount()));
		walletRepo.save(wallet);
		return transationService.deposit(TransactionMapper.toDto(wallet, model));
	}
	
	@Transactional
	public MessageDto withdraw(WalletOperationDto model) {
		log.info("Starting the withdrawal of funds");
		checkIfTheNumberIsNegative(model.amount());
		transationService.findByIdempotencyKey(model.operationCode());
		
		WalletDto wallet = walletRepo.findById(model.walletId()).map(WalletMapper::toDto)
				.orElseThrow(() -> new WalletNotFoundException(model.walletId()));
		if (wallet.getBalance().compareTo(model.amount()) < 0) {
			throw new InsufficientBalanceException();
		}
		wallet.setBalance(wallet.getBalance().subtract(model.amount()));
		walletRepo.save(WalletMapper.toEntiy(wallet));
		return transationService.withdraw(TransactionMapper.toDto(wallet, model));
	}

		@Transactional
	    public MessageDto transfer(WalletOperationTransferDto model) {
	    	
	    	log.info("Starting the transfer of values");
	    	checkIfTheNumberIsNegative(model.walletOperation().amount());
			transationService.findByIdempotencyKey(model.walletOperation().operationCode());
			List<Long> originAndDestination = List.of(model.origin(), model.destination());
			List<WalletDto> wallets = walletRepo.findByIdIn(originAndDestination).stream().map(WalletMapper::toDto).toList();
			
			if (wallets.isEmpty() || wallets.size()!=2) {
				throw new WalletNotFoundException();
			}
			
			Map<Long, WalletDto> walletMap = wallets.stream()
				    .collect(Collectors.toMap(WalletDto::getId, Function.identity()));
			
	    	WalletDto from = walletMap.get(model.origin());
			if (from.getBalance().compareTo(model.walletOperation().amount()) < 0) {
				throw new InsufficientBalanceException();
			}
	    	
	    	WalletDto to = walletMap.get(model.destination());

	        from.setBalance(from.getBalance().subtract(model.walletOperation().amount()));
	        to.setBalance(to.getBalance().add(model.walletOperation().amount()));

	        walletRepo.save(WalletMapper.toEntiy(from));
	        walletRepo.save(WalletMapper.toEntiy(to));
	        return transationService.transfer(new TransactionTransferDto(from, to, model.walletOperation()));

	    }
		
		@Override
		public void deleteAll() {
			transationService.deleteAll();
			walletRepo.deleteAll();
		}

	    public TransactionHistoryDto transactionHistory(Long walletId, LocalDateTime startDate, LocalDateTime endDate) {
	    	log.info("Consulting the wallet history");
	        return new  TransactionHistoryDto(transationService.transactionHistory(walletId, startDate, endDate ));
	    }
	    
	    private static void checkIfTheNumberIsNegative(BigDecimal amount) {
	    	if (amount.compareTo(BigDecimal.ZERO) < 0) {
			    throw new InsufficientBalanceException ("The deposit to be deposited is negative or zero");
			}
	    }

		
	    
	    
	    
	    
}
