package baskakov.dev.service;

import baskakov.dev.model.Account;
import baskakov.dev.model.User;
import baskakov.dev.utli.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final AccountService accountService;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;

    public UserService(
            AccountService accountService,
            SessionFactory sessionFactory,
            TransactionHelper transactionHelper) {
        this.accountService = accountService;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }

    public User createUser(String login) {
        return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            var user = session.createQuery("FROM User WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResultOrNull();

            if (user != null) {
                throw new IllegalArgumentException("Пользователь %s уже существует"
                        .formatted(login));
            }

            User newUser =  new User(null, login, new ArrayList<>());
            session.persist(newUser);

            accountService.createAccount(newUser);

            return newUser;
        });

    }

    public Optional<User> findUserById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            var user = session.find(User.class, id);
            return Optional.of(user);
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.accountList", User.class)
                    .list();
        }
    }
}
