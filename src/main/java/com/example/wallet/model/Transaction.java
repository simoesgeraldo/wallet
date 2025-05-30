package com.example.wallet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet_id", nullable = false)
	private Wallet wallet;
	private BigDecimal amount;
	@Column(name = "transactiondate")
	private LocalDateTime transactionDate;
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	@Column(length = 100)
	private String description;
	@Column(name="idempotencykey", length = 15, nullable = false)
	private String idempotencyKey;
	
	public Transaction() {
	}
	public Transaction(Long id, Wallet wallet, BigDecimal amount, LocalDateTime transactionDate, TransactionType type,
			String description, String idempotencyKey) {
		super();
		this.id = id;
		this.wallet = wallet;
		this.amount = amount;
		this.transactionDate = transactionDate;
		this.type = type;
		this.description = description;
		this.idempotencyKey = idempotencyKey;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Wallet getWallet() {
		return wallet;
	}
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}
	@Override
	public int hashCode() {
		return Objects.hash(amount, description, id, idempotencyKey, transactionDate, type, wallet);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return Objects.equals(amount, other.amount) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(idempotencyKey, other.idempotencyKey)
				&& Objects.equals(transactionDate, other.transactionDate) && type == other.type
				&& Objects.equals(wallet, other.wallet);
	}
	
	
	
}
