package com.example.wallet.dto;

import java.math.BigDecimal;

public class MessageDto {

	private Long id;
	private String owner;
	private BigDecimal amount ;
	private String message;
	public MessageDto(Long id ,String owner, BigDecimal amount, String message) {
		super();
		this.id = id;
		this.owner = owner;
		this.amount = amount;
		this.message = message;
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
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
