package com.example.wallet.dto;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class WalletDto {

	private Long id;
	@Pattern(regexp = "[a-zA-Z]+", message = "The name must contain only letters")
	@NotEmpty(message = "The owner must not be empty")
	private String owner;
	private BigDecimal balance = BigDecimal.ZERO;
	@JsonIgnore
	private Long version =0L;

	public WalletDto(Long id, String owner, BigDecimal balance, Long version) {
		super();
		this.id = id;
		this.owner = Objects.requireNonNull(owner);
		this.balance = balance;
		this.version = version;
	}

	public WalletDto() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	


}
