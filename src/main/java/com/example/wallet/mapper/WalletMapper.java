package com.example.wallet.mapper;

import com.example.wallet.dto.WalletDto;
import com.example.wallet.model.Wallet;

public class WalletMapper {

	public static WalletDto toDto(Wallet wallet){
		return new WalletDto(wallet.getId(), wallet.getOwner(), wallet.getBalance(), wallet.getVersion());
	}
	
	public static Wallet toEntiy(WalletDto wallet){
		return new Wallet(wallet.getId(), wallet.getOwner(), wallet.getBalance(), wallet.getVersion());
	}

}
