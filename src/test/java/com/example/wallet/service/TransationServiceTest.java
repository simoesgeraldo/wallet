package com.example.wallet.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.wallet.dto.MessageDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;
import com.example.wallet.dto.transaction.TransactionDto;
import com.example.wallet.dto.transaction.TransactionTransferDto;
import com.example.wallet.exceptionhandler.WalletDataIntegrityViolationException;
import com.example.wallet.mapper.WalletMapper;
import com.example.wallet.model.Transaction;
import com.example.wallet.model.TransactionType;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.serviceImpl.TransationServiceImpl;

public class TransationServiceTest {
	 @Mock
	    private TransactionRepository transactionRepo;

	    private TransationServiceImpl service;

	    @BeforeEach
	    void setup() {
	        MockitoAnnotations.openMocks(this);
	        service = new TransationServiceImpl(transactionRepo);
	    }

	    @Test
	    void testDeposit() {
	        WalletDto wallet = new WalletDto(1L, "User", null, null);
	        TransactionDto model = new TransactionDto(1L, wallet, BigDecimal.TEN, null, null, "10", "23");

	        MessageDto result = service.deposit( model);

	        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
	        verify(transactionRepo).save(txCaptor.capture());

	        Transaction savedTx = txCaptor.getValue();
	        assertEquals(TransactionType.DEPOSIT, savedTx.getType());
	        assertEquals(10, savedTx.getAmount().intValue());

	        assertEquals("Deposit made successfully.", result.getMessage());
	    }

	    @Test
	    void testWithdraw() {
	        WalletDto wallet = new WalletDto(1L, "User", null, null);
	        TransactionDto withdraw = new TransactionDto(1L, wallet, BigDecimal.TEN, null, null, "Withdraw", "op456");

	        MessageDto result = service.withdraw(withdraw);

	        verify(transactionRepo).save(any(Transaction.class));
	        assertEquals("Withdrawal successful", result.getMessage());
	    }

	    @Test
	    void testTransfer() {
	    	WalletDto fromWallet = new WalletDto(34L, "User", new BigDecimal("100"), null);
	    	WalletDto toWallet = new WalletDto(98L, "User2", new BigDecimal("10"), null);
	    	WalletOperationDto model = new WalletOperationDto(1L, BigDecimal.TEN, "10", "op789");
	    	TransactionTransferDto transferDto = new TransactionTransferDto(fromWallet, toWallet, model);
	        MessageDto result = service.transfer(transferDto);

	        verify(transactionRepo, times(2)).save(any(Transaction.class));
	        assertEquals("Transfer completed successfully", result.getMessage());
	    }

	    @Test
	    void testTransactionHistory() {
	    	
	    	WalletDto toWallet = new WalletDto(2L, "User2", null, null);
	        when(transactionRepo.findByWalletIdAndTransactionDateBetween(anyLong(), any(), any()))
	            .thenReturn(List.of(new Transaction(1L, WalletMapper.toEntiy(toWallet), BigDecimal.TEN, LocalDateTime.now(), TransactionType.DEPOSIT, "Description", "op")));
	        List<TransactionDto> history = service.transactionHistory(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now());

	        assertEquals(1, history.size());
	    }

	    @Test
	    void testFindByIdempotencyKey_WhenExists_ShouldThrow() {
	        when(transactionRepo.findByIdempotencyKey("dup-key"))
	            .thenReturn(List.of(new Transaction()));

	        assertThrows(WalletDataIntegrityViolationException.class, () -> {
	            service.findByIdempotencyKey("dup-key");
	        });
	    }

	    @Test
	    void testFindByIdempotencyKey_WhenNotExists_ShouldNotThrow() {
	        when(transactionRepo.findByIdempotencyKey("unique-key"))
	            .thenReturn(Collections.emptyList());

	        assertDoesNotThrow(() -> {
	            service.findByIdempotencyKey("unique-key");
	        });
	    }
}
