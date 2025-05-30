package com.example.wallet.controller;
import com.example.wallet.dto.*;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWallet() {
        WalletDto wallet = new WalletDto();
        when(walletService.createWallet(wallet)).thenReturn(wallet);

        ResponseEntity<WalletDto> response = walletController.createWallet(wallet);

        assertEquals(wallet, response.getBody());
        verify(walletService, times(1)).createWallet(wallet);
    }

    @Test
    void testGetBalance() {
        WalletDto wallet = new WalletDto();
        when(walletService.getWallet(1L)).thenReturn(Optional.of(wallet));

        ResponseEntity<WalletDto> response = walletController.getBalance(1L);

        assertEquals(wallet, response.getBody());
        verify(walletService).getWallet(1L);
    }

    @Test
    void testDeposit() {
    	WalletOperationDto model = new WalletOperationDto(1L, BigDecimal.TEN, "10", "23");
        MessageDto message = new MessageDto(null, null, null, "Deposit successful");
        when(walletService.deposit(model)).thenReturn(message);

        ResponseEntity<MessageDto> response = walletController.deposit(1L,model);

        assertEquals(message, response.getBody());
        verify(walletService).deposit(model);
    }

    @Test
    void testWithdraw() {
    	WalletOperationDto model = new WalletOperationDto(1L, BigDecimal.TEN, "10", "23");
        MessageDto message = new MessageDto(null, null, null, "Withdraw successful");
        when(walletService.withdraw(model)).thenReturn(message);

        ResponseEntity<MessageDto> response = walletController.withdraw(1L,model);

        assertEquals(message, response.getBody());
        verify(walletService).withdraw(model);
    }

    @Test
    void testTransfer() {
    	WalletOperationDto model = new WalletOperationDto(1L, BigDecimal.TEN, "10", "23");
    	WalletOperationTransferDto transferDto = new WalletOperationTransferDto(4L, 10L, model);
        MessageDto message = new MessageDto(null, null, null, "Transfer successful");
        when(walletService.transfer(transferDto)).thenReturn(message);

        ResponseEntity<MessageDto> response = walletController.transfer(4L, 10L, transferDto);

        assertEquals(message, response.getBody());
        verify(walletService).transfer(transferDto);
    }

    @Test
    void testGetTransactions() {
        Long walletId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        TransactionHistoryDto history = new TransactionHistoryDto(List.of());

        when(walletService.transactionHistory(walletId, start, end)).thenReturn(history);

        ResponseEntity<TransactionHistoryDto> response = walletController.getTransactions(walletId, startDate, endDate);

        assertEquals(history, response.getBody());
        verify(walletService).transactionHistory(walletId, start, end); 
    }
}

