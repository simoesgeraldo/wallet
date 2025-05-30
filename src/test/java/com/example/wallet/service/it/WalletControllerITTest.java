package com.example.wallet.service.it;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.wallet.dto.WalletDto;
import com.example.wallet.dto.WalletOperationDto;
import com.example.wallet.dto.transaction.TransactionDto;
import com.example.wallet.dto.transaction.TransactionTransferDto;
import com.example.wallet.service.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WalletControllerITTest {

	 @LocalServerPort
	  private Integer port;
	 JsonMapper jsonMapper;  

	  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
	    "postgres:16-alpine"
	  );

	  @BeforeAll
	  static void beforeAll() {
	    postgres.start();
	    
	  }

	  @AfterAll
	  static void afterAll() {
	    postgres.stop();
	  }

	  @DynamicPropertySource
	  static void configureProperties(DynamicPropertyRegistry registry) {
	    registry.add("spring.datasource.url", postgres::getJdbcUrl);
	    registry.add("spring.datasource.username", postgres::getUsername);
	    registry.add("spring.datasource.password", postgres::getPassword);
	  }

	  @Autowired
	  WalletService walletService;
	  
	  @BeforeEach
	   void setUp() {
	    RestAssured.baseURI = "http://localhost:" + port;
	    walletService.deleteAll();
	  }
	  
    @Test
    @Order(1)
    void testCreateWallet() throws JsonProcessingException {
    	 WalletDto wallet = new WalletDto();
    	 wallet.setOwner("Calton");

    	 given()
         .contentType(ContentType.JSON)
         .body(wallet)
     .when()
         .post("/wallet")
     .then()
         .statusCode(200)
         .body("id", equalTo(1));
    	       
    }
    
    void testCreateWalletError() throws JsonProcessingException {
   	 WalletDto wallet = new WalletDto();
   	wallet.setId(0L);
   	wallet.setOwner("Calton");

   	 given()
        .contentType(ContentType.JSON)
        .body(wallet)
    .when()
        .post("/wallet")
    .then()
        .statusCode(200)
        .body("id", equalTo(1));
   	       
   }
    

    @Test
    @Order(2)
    void shouldDepositSuccessfully() {
        WalletDto wallet = new WalletDto(null, "Calton", null, null);
        wallet = walletService.createWallet(wallet);
        TransactionDto deposit = new TransactionDto(null, wallet, BigDecimal.TEN, null, null, "Para pagamento de agua", "AD");


        given()
            .contentType(ContentType.JSON)
            .body(deposit)
        .when()
            .post("/wallet/"+wallet.getId()+"/deposit")
        .then()
            .statusCode(200)
            .body("message", equalTo("Deposit made successfully."));
    }
    
    
    @Test
    @Order(3)
    void shouldFailDepositWithInvalidAmount() {
    	WalletDto wallet = new WalletDto(null, "Calton", null, null);
    	wallet = walletService.createWallet(wallet);
    	TransactionDto deposit = new TransactionDto(null, wallet,new BigDecimal("-10.5"), null, null, "AQ", "S");
        
        given()
            .contentType(ContentType.JSON)
            .body(deposit)
        .when()
            .post("/wallet/"+wallet.getId()+"/deposit")
        .then()
            .statusCode(400)
            .body("amount", equalTo("The value must be positive"));
        
    }
    
    @Test
    void shouldWithdrawSuccessfully() {
    	WalletDto wallet = new WalletDto(null, "Calton", null, null);
    	wallet = walletService.createWallet(wallet);
    	WalletOperationDto walletDeposit = new WalletOperationDto(wallet.getId(), BigDecimal.TEN, "code12&3", "23");
    	walletService.deposit(walletDeposit);
    	WalletOperationDto walletWithdraw = new WalletOperationDto(wallet.getId(), BigDecimal.ONE, "code12l", "23");

        given()
            .contentType(ContentType.JSON)
            .body(walletWithdraw)
        .when()
            .post("/wallet/"+wallet.getId()+"/withdraw")
        .then()
            .statusCode(200)
            .body("message", equalTo("Withdrawal successful"));
    }

    @Test
    void shouldFailWithdrawWithInsufficientFunds() {
    	WalletDto wallet = new WalletDto(null, "Calton", null, null);
    	wallet = walletService.createWallet(wallet);
    	WalletOperationDto walletDeposit = new WalletOperationDto(wallet.getId(), new BigDecimal("11"), "code124", "23");
    	walletService.deposit(walletDeposit);
    	WalletOperationDto walletWithdraw = new WalletOperationDto(wallet.getId(), new BigDecimal("12"), "codeGHH", "Compra");

        given()
            .contentType(ContentType.JSON)
            .body(walletWithdraw)
        .when()
            .post("/wallet/"+wallet.getId()+"/withdraw")
        .then()
            .statusCode(400)
            .body("message", equalTo("Insufficient balance to carry out the operation."));
    	
    }

    
    @Test
    void shouldGetBalanceSuccessfully() {
    	WalletDto wallet = new WalletDto(null, "Calton", null, null);
    	wallet = walletService.createWallet(wallet);
    	WalletOperationDto walletDeposit = new WalletOperationDto(wallet.getId(), new BigDecimal("120.00"), "code12&3", "23");
    	walletService.deposit(walletDeposit);

        given()
            .when()
            .get("/wallet/" + wallet.getId() + "/balance")
            .then()
            .statusCode(200)
            .body("balance", equalTo(120.0f));
    }

    @Test
    void shouldReturnNotFoundWhenGettingBalanceOfInvalidId() {
        given()
            .when()
            .get("/wallet/99999/balance")
            .then()
            .statusCode(404);
    }
    
    @Test
    void shouldTransferSuccessfully() {
        
    	WalletDto wallet = new WalletDto(null, "Cheldon", null, null);
    	wallet = walletService.createWallet(wallet);
    	WalletDto wallet2 = new WalletDto(null, "Cris", null, null);
    	wallet2 = walletService.createWallet(wallet2);
    	
    	WalletOperationDto walletDeposit = new WalletOperationDto(wallet.getId(), new BigDecimal("110"), "OPC", "deposito em conta");
    	walletService.deposit(walletDeposit);
    	WalletOperationDto transfer = new WalletOperationDto(wallet.getId(), new BigDecimal("110"), "OPCA", "Transferencia entre contas");
    	TransactionTransferDto transactionTransferDto = new TransactionTransferDto(wallet, wallet2, transfer);
    	

        given()
            .contentType(ContentType.JSON)
            .body(transactionTransferDto)
        .when()
            .post("/wallet/"+wallet.getId()+"/"+wallet2.getId()+"/transfer")
        .then()
            .statusCode(200)
            .body("message", equalTo("Transfer completed successfully"));
    }

    @Test
    void shouldFailTransferWithInsufficientFunds() {
    	WalletDto wallet = new WalletDto(null, "Cheldon", null, null);
    	wallet = walletService.createWallet(wallet);
    	WalletDto wallet2 = new WalletDto(null, "Cris", null, null);
    	wallet2 = walletService.createWallet(wallet2);
    	
    	WalletOperationDto walletDeposit = new WalletOperationDto(wallet.getId(), new BigDecimal("10"), "OPC", "deposito em conta");
    	walletService.deposit(walletDeposit);
    	WalletOperationDto transfer = new WalletOperationDto(wallet.getId(), new BigDecimal("11"), "OPCA", "Transferencia entre contas");
    	TransactionTransferDto transactionTransferDto = new TransactionTransferDto(wallet, wallet2, transfer);


    	 given()
         .contentType(ContentType.JSON)
         .body(transactionTransferDto)
     .when()
         .post("/wallet/"+wallet.getId()+"/"+wallet2.getId()+"/transfer")
     .then()
         .statusCode(400)
         .body("message", equalTo("Insufficient balance to carry out the operation."));
    }

    @Test
    void shouldReturnTransactionHistory() {
    	WalletDto wallet = new WalletDto(null, "Cheldon", null, null);
    	wallet = walletService.createWallet(wallet);
    	WalletDto wallet2 = new WalletDto(null, "Cris", null, null);
    	wallet2 = walletService.createWallet(wallet2);
    	
    	WalletOperationDto walletDeposit = new WalletOperationDto(wallet.getId(), new BigDecimal("110"), "OPC", "deposito em conta");
    	walletService.deposit(walletDeposit);

        given()
            .queryParam("startDate", "2025-05-01")
            .queryParam("endDate", "2025-05-30")
        .when()
            .get("/wallet/"+wallet.getId()+"/transactions")
        .then()
            .statusCode(200);
    }
 
}

