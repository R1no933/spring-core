package baskakov.dev.service;


import baskakov.dev.model.Account;
import baskakov.dev.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private AccountService accountService;
    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldCreateUserWithAccount_WhenLoginUnique() {
        //Arrange
        String uniqLogin = "testLogin";
        Account mockAccount = new Account(1, 1, 1000);
        when(accountService.createAccount(any(User.class))).thenReturn(mockAccount);

        //Act
        User result = userService.createUser(uniqLogin);

        //Assert
        assertNotNull(result);
        assertEquals(uniqLogin, result.getLogin());
        assertEquals(1, result.getId());
        assertEquals(1, result.getAccountList().size());
        assertEquals(mockAccount, result.getAccountList().get(0));
        verify(accountService).createAccount(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenLoginIsAlreadyExists() {
        //Arrange
        String existingUser = "existingUser";
        when(accountService.createAccount(any(User.class)))
                .thenReturn(new Account(1, 1, 1000));
        userService.createUser(existingUser); // first create success

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(existingUser));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        //Arrange
        String userName = "testUser";
        Account mockAccount = new Account(1, 1, 1000);
        User expectedUser = userService.createUser(userName);

        //Act
        Optional<User> currentUser = userService.getUserById(expectedUser.getId());

        //Assert
        assertTrue(currentUser.isPresent());
        assertEquals(expectedUser, currentUser.get());

    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> currentUser = userService.getUserById(999);
        assertTrue(currentUser.isEmpty());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers_WhenUserExists() {
        //Arrange
        when(accountService.createAccount(any(User.class)))
                .thenReturn(new Account(1, 1, 1000))
                .thenReturn(new Account(2, 2, 1000))
                .thenReturn(new Account(3, 3, 1000));

        User user1  = userService.createUser("testUser1");
        User user2 = userService.createUser("testUser2");
        User user3 = userService.createUser("testUser3");

        //Act
        List<User> resultList = userService.getAllUsers();

        //Assert
        assertEquals(resultList.size(), 3);
        assertTrue(resultList.contains(user1));
        assertTrue(resultList.contains(user2));
        assertTrue(resultList.contains(user3));
    }
}