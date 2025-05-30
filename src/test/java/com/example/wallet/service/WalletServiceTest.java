package com.example.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.wallet.dto.MessageDto;
import com.example.wallet.dto.TransactionHistoryDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;
import com.example.wallet.dto.WalletOperationTransferDto;
import com.example.wallet.exceptionhandler.InsufficientBalanceException;
import com.example.wallet.exceptionhandler.WalletNotFoundException;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.serviceImpl.WalletServiceImpl;

class WalletServiceTest {

	@Mock
	private WalletRepository walletRepo;

	@Mock
	private TransationService transationService;

	@InjectMocks
	private WalletServiceImpl walletService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createWallet_success() {
		WalletDto dto = new WalletDto(null, "User", BigDecimal.ZERO, null);
		Wallet wallet = new Wallet();
		wallet.setId(1L);
		wallet.setOwner("User");
		wallet.setBalance(BigDecimal.ZERO);

		when(walletRepo.save(any(Wallet.class))).thenReturn(wallet);

		WalletDto result = walletService.createWallet(dto);

		assertEquals("User", result.getOwner());
		assertEquals(BigDecimal.ZERO, result.getBalance());
		verify(walletRepo).save(any(Wallet.class));
	}

	@Test
	void getWallet_found() {
		Wallet wallet = new Wallet(1L, "owner", BigDecimal.TEN, null);
		when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));

		Optional<WalletDto> result = walletService.getWallet(1L);

		assertTrue(result.isPresent());
		assertEquals("owner", result.get().getOwner());
	}

	@Test
	void getWallet_notFound() {
		when(walletRepo.findById(99L)).thenReturn(Optional.empty());

		assertThrows(WalletNotFoundException.class, () -> walletService.getWallet(99L));
	}

	@Test
	void deposit_success() {

		Wallet wallet = new Wallet(1L, "owner", BigDecimal.ZERO, null);
		Wallet updatedWallet = new Wallet(1L, "owner", BigDecimal.TEN, null);
		WalletOperationDto dto = new WalletOperationDto(1L, BigDecimal.TEN, "code123", "23");

		when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
		when(walletRepo.save(any(Wallet.class))).thenReturn(updatedWallet);
		when(transationService.deposit(any())).thenReturn(new MessageDto(1L, "owner", null, "Success"));

		MessageDto result = walletService.deposit(dto);

		assertEquals("Success", result.getMessage());
		verify(transationService).findByIdempotencyKey("code123");
	}

	@Test
	void withdraw_insufficientBalance() {
		WalletOperationDto model = new WalletOperationDto(1L, BigDecimal.TEN, "1B0", "23");
		Wallet wallet = new Wallet(1L, "owner", BigDecimal.valueOf(5), null);

		when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));

		assertThrows(InsufficientBalanceException.class, () -> walletService.withdraw(model));
	}

	@Test
	void transfer_success() {
		WalletOperationDto dto = new WalletOperationDto(1L, BigDecimal.TEN, "codeT", "23");
		WalletOperationTransferDto model = new WalletOperationTransferDto(1L, 2L, dto);

		Wallet origin = new Wallet(1L, "Origin", BigDecimal.valueOf(50), null);
		Wallet destination = new Wallet(2L, "Destination", BigDecimal.valueOf(20), null);

		when(walletRepo.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(origin, destination));
		when(walletRepo.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(transationService.transfer(any()))
				.thenReturn(new MessageDto(null, destination.getOwner(), null, "Transfer successful"));

		MessageDto result = walletService.transfer(model);

		assertEquals("Transfer successful", result.getMessage());
		verify(transationService).findByIdempotencyKey("codeT");
	}

	@Test
	void transactionHistory_success() {
		Long walletId = 1L;
		LocalDateTime start = LocalDateTime.now().minusDays(5);
		LocalDateTime end = LocalDateTime.now();

		when(transationService.transactionHistory(walletId, start, end)).thenReturn(List.of());

		TransactionHistoryDto history = walletService.transactionHistory(walletId, start, end);

		assertNotNull(history);
		assertTrue(history.getTransactios().isEmpty());
	}

}
