package com.example.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.wallet.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
	
	List<Wallet> findByIdIn(List<Long> ids);

}
