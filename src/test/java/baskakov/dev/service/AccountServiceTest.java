package baskakov.dev.service;

import baskakov.dev.configure.AccountProperties;
import baskakov.dev.model.Account;
import baskakov.dev.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountProperties accountProperties;
    @InjectMocks
    AccountService accountService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createAccount_ShouldCreateAccountWithDefaultAmount() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());

        //Act
        Account resultAccount = accountService.createAccount(user);

        //Assert
        assertNotNull(resultAccount);
        assertEquals(1, resultAccount.getId());
        assertEquals(user.getId(), resultAccount.getUserId());
        assertEquals(1000, resultAccount.getMoneyAmount());
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenExists() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account expectedAccount = accountService.createAccount(user);

        //Act
        Optional<Account> resultAccount = accountService.getAccountById(expectedAccount.getId());

        //Assert
        assertTrue(resultAccount.isPresent());
        assertEquals(expectedAccount,  resultAccount.get());
    }

    @Test
    void getAccountById_ShouldReturnEmpty_WhenDoesNotExist() {
        Optional<Account> resultAccount = accountService.getAccountById(999);
        assertTrue(resultAccount.isEmpty());
    }

    @Test
    void getAllAccountsForUser_ShouldReturnAllUserAccounts() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account acc1 = accountService.createAccount(user);
        Account acc2 = accountService.createAccount(user);

        //Act
        List<Account> userAccounts = accountService.getAllAccountsForUser(user.getId());

        //Assert
        assertEquals(2, userAccounts.size());
        assertTrue(userAccounts.contains(acc1));
        assertTrue(userAccounts.contains(acc2));
    }

    @Test
    void depositAccount_ShouldIncreasedAccountBalance() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account userAccount = accountService.createAccount(user);
        int initAmount = userAccount.getMoneyAmount();
        int depositAmount = 250;

        //Act
        accountService.depositToAccount(userAccount.getId(), depositAmount);

        //Assert
        Optional<Account> resultAccount = accountService.getAccountById(userAccount.getId());
        assertTrue(resultAccount.isPresent());
        assertEquals(initAmount + depositAmount, resultAccount.get().getMoneyAmount());
    }

    @Test
    void depositAccount_ShouldThrowException_WhenNegativeAmount() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account userAccount = accountService.createAccount(user);

        //Act
        assertThrows(IllegalArgumentException.class, () -> accountService.depositToAccount(userAccount.getId(), -100));
    }

    @Test
    void withdrawAccount_ShouldDecreaseAccountBalance() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account userAccount = accountService.createAccount(user);
        int initAmount = userAccount.getMoneyAmount();
        int withdrawAmount = 250;

        //Act
        accountService.withdrawFromAccount(userAccount.getId(), withdrawAmount);

        //Assert
        Optional<Account> resultAccount = accountService.getAccountById(userAccount.getId());
        assertTrue(resultAccount.isPresent());
        assertEquals(initAmount - withdrawAmount, resultAccount.get().getMoneyAmount());
    }

    @Test
    void withdrawAccount_ShouldThrowException_WhenInsufficientFunds() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account userAccount = accountService.createAccount(user);

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.withdrawFromAccount(userAccount.getId(), 2500));
    }

    @Test
    void closeAccount_ShouldCloseAccountAndTransferMoney() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account userAccount1 = accountService.createAccount(user);
        Account userAccount2 = accountService.createAccount(user);
        int init1Amount = userAccount1.getMoneyAmount();
        int init2Amount =  userAccount2.getMoneyAmount();

        //Act
        Account closedAccount = accountService.closeAccountById(userAccount1.getId());

        //Assert
        assertEquals(userAccount1, closedAccount);
        Optional<Account> resultAccount = accountService.getAccountById(userAccount2.getId());
        assertTrue(resultAccount.isPresent());
        assertEquals(init1Amount + init2Amount, resultAccount.get().getMoneyAmount());
        assertTrue(accountService.getAccountById(userAccount1.getId()).isEmpty());
    }

    @Test
    void closeAccount_ShouldThrowException_WhenOneAccount() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account userAccount1 = accountService.createAccount(user);


        //Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.closeAccountById(userAccount1.getId()));
    }

    @Test
    void transferMoney_ShouldTransferBetweenAccountForOneUser() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user = new User(1, "testUser", List.of());
        Account fromAccount = accountService.createAccount(user);
        Account toAccount = accountService.createAccount(user);
        int transferAmount = 500;
        int fromInitAmount = fromAccount.getMoneyAmount();
        int toInitAmount = toAccount.getMoneyAmount();

        //Act
        accountService.transferMoney(fromAccount.getId(), toAccount.getId(), transferAmount);

        //Assert
        Optional<Account> updatedFrom = accountService.getAccountById(fromAccount.getId());
        Optional<Account> updatedTo = accountService.getAccountById(toAccount.getId());

        assertTrue(updatedFrom.isPresent());
        assertTrue(updatedTo.isPresent());
        assertEquals(fromInitAmount - transferAmount, updatedFrom.get().getMoneyAmount());
        assertEquals(toInitAmount + transferAmount, updatedTo.get().getMoneyAmount());
    }

    @Test
    void transferMoney_ShouldTransferBetweenAccountForTwoUsersWithComissions() {
        //Arrange
        when(accountProperties.getDefaultAmount()).thenReturn(1000);
        User user1 = new User(1, "testUser1", List.of());
        User  user2 = new User(2, "testUser2", List.of());
        Account fromAccount = accountService.createAccount(user1);
        Account toAccount = accountService.createAccount(user2);
        int transferAmount = 500;
        int fromInitAmount = fromAccount.getMoneyAmount();
        int toInitAmount = toAccount.getMoneyAmount();
        when(accountProperties.getDefaultTransferComission()).thenReturn(0.05);

        //Act
        accountService.transferMoney(fromAccount.getId(), toAccount.getId(), transferAmount);

        //Assert
        Optional<Account> updatedFrom = accountService.getAccountById(fromAccount.getId());
        Optional<Account> updatedTo = accountService.getAccountById(toAccount.getId());

        assertTrue(updatedFrom.isPresent());
        assertTrue(updatedTo.isPresent());
        assertEquals(fromInitAmount - transferAmount, updatedFrom.get().getMoneyAmount());
        int expectedAmount = (int) (transferAmount * (1 - 0.05));
        assertEquals(toInitAmount + expectedAmount, updatedTo.get().getMoneyAmount());
        verify(accountProperties).getDefaultTransferComission();
    }
}