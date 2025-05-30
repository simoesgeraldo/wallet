package com.example.wallet.dto.transaction;

import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;

public record TransactionTransferDto(WalletDto fromWallet, WalletDto toWallet, WalletOperationDto walletOperation) {
}
