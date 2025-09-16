package baskakov.dev.service;

import baskakov.dev.model.Account;
import baskakov.dev.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final Map<Integer, User> userMap;
    private final Set<String> takenLogins;
    private final AccountService accountService;
    private int idCounter;

    public UserService(AccountService accountService) {
        this.userMap = new HashMap<>();
        this.takenLogins = new HashSet<>();
        this.accountService = accountService;
        this.idCounter = 0;
    }

    public User createUser(String userLogin) {
        if (takenLogins.contains(userLogin)) {
            throw new IllegalArgumentException("Пользователь с логином $s уже существует!".formatted(userLogin));
        }
        takenLogins.add(userLogin);
        idCounter++;

        User newUser = new User(idCounter, userLogin, new ArrayList<>());
        Account newAccount = accountService.createAccount(newUser);
        newUser.getAccountList().add(newAccount);
        userMap.put(newUser.getId(), newUser);
        return newUser;
    }

    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(userMap.get(id));
    }

    public List<User> getAllUsers() {
        return userMap.values().stream().toList();
    }
}
