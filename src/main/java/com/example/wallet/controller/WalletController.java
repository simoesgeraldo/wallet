package com.example.wallet.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wallet.dto.MessageDto;
import com.example.wallet.dto.TransactionHistoryDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;
import com.example.wallet.dto.WalletOperationTransferDto;
import com.example.wallet.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallet")
@Tag(description = "Wallet", name = "Wallet")
public class WalletController {

	private final WalletService walletService;

	public WalletController(WalletService walletService) {
		this.walletService = walletService;
	}

	@PostMapping
	@Operation(summary = "Create Wallet", description = "Allow the creation of wallets for users")
	public ResponseEntity<WalletDto> createWallet(@RequestBody @Valid WalletDto wallet) {
		return ResponseEntity.ok(walletService.createWallet(wallet));
	}

	@GetMapping("/{walleteId}/balance")
	@Operation(summary = "Retrieve Balance", description = "Retrieve the current balance of a user's wallet")
	public ResponseEntity<WalletDto> getBalance(@PathVariable Long walleteId) {
		return ResponseEntity.ok(walletService.getWallet(walleteId).get());
	}

	@PostMapping("/{walleteId}/deposit")
	@Operation(summary = "Deposit Funds", description = "Enable users to deposit money into their wallets")
	public ResponseEntity<MessageDto> deposit(@PathVariable Long walleteId, @RequestBody @Valid WalletOperationDto model) {
		model = model.withWalletId(walleteId);
		return ResponseEntity.ok(walletService.deposit(model));
	}
	
	@GetMapping("/{walleteId}/transactions")
	@Operation(summary = "Retrieve Historical Balance", description = "Retrieve the balance of a user's wallet at a specific\n"
			+ "point in the past")
	public ResponseEntity<TransactionHistoryDto> getTransactions(
			@PathVariable Long walleteId,
		    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
			) {
		LocalDateTime start = startDate.atStartOfDay(); 
	    LocalDateTime end = endDate.atTime(LocalTime.MAX);
		return ResponseEntity.ok(walletService.transactionHistory(walleteId, start, end));
	}
	
	@PostMapping("/{walleteId}/withdraw")
	@Operation(summary = "Withdraw Funds", description = "Enable users to withdraw money from their wallets")
    public ResponseEntity<MessageDto> withdraw(@PathVariable Long walleteId, @RequestBody @Valid WalletOperationDto walletOperation ) {
		walletOperation = walletOperation.withWalletId(walleteId);
        return ResponseEntity.ok(walletService.withdraw(walletOperation));
    }

    @PostMapping("/{origin}/{destination}/transfer")
    @Operation(summary = "Transfer Funds", description = "Facilitate the transfer of money between user wallets")
    public ResponseEntity<MessageDto> transfer(@PathVariable Long origin, @PathVariable Long destination, @RequestBody @Valid WalletOperationTransferDto amounToTransfer) {
    	amounToTransfer = amounToTransfer.withOriginAndDestination(origin, destination);
        return ResponseEntity.ok(walletService.transfer(amounToTransfer));
    }
   
}
