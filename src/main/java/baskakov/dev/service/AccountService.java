package baskakov.dev.service;

import baskakov.dev.configure.AccountProperties;
import baskakov.dev.model.Account;
import baskakov.dev.model.User;
import baskakov.dev.utli.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AccountProperties accountProperties;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;


    public AccountService(
            AccountProperties accountProperties,
            SessionFactory sessionFactory,
            TransactionHelper transactionHelper) {
        this.accountProperties = accountProperties;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }

    public Account createAccount(User user) {
        return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            Account account = new Account(null, user, accountProperties.getDefaultAmount());
            session.persist(account);
            return account;
        });
    }

    public void depositToAccount(Long accountId, int amountToDeposit) {
        if (amountToDeposit <= 0) {
            throw new IllegalArgumentException("Невозможно внести 0 или отрицательную сумму: amountToDeposit = %s"
                    .formatted(amountToDeposit));
        }

        transactionHelper.executeInTransaction(() -> {
            Account account = getAccountById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(accountId)));

            account.setMoneyAmount(account.getMoneyAmount() + amountToDeposit);
            return 0;
        });
    }

    public void withdrawFromAccount(Long accountId, int amountToWithdraw) {
        if (amountToWithdraw <= 0) {
            throw new IllegalArgumentException("Невозможно снять 0 или отрицательную сумму: amountToWithdraw = %s"
                    .formatted(amountToWithdraw));
        }

        transactionHelper.executeInTransaction(() -> {
            Account account = getAccountById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(accountId)));

            if (account.getMoneyAmount() < amountToWithdraw) {
                throw new IllegalArgumentException("Нельзя снять сумму %s с аккаунта %s, так как на аккаунте %s"
                        .formatted(amountToWithdraw, accountId, account.getMoneyAmount()));
            }
            account.setMoneyAmount(account.getMoneyAmount() - amountToWithdraw);
            return 0;
        });
    }

    public Account closeAccountById(Long accountId) {
        return transactionHelper.executeInTransaction(() -> {
            Account accountToClose = getAccountById(accountId).
                    orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(accountId)));

            var accountList = accountToClose.getOwner().getAccountList();

            if (accountList.size() == 1) {
                throw new IllegalArgumentException("Невозможно удалить единственный аккаунт");
            }

            Account accountToDepositAfterClose = accountList.stream()
                    .filter(acc -> !acc.getId().equals(accountId))
                    .findFirst()
                    .orElseThrow();

            accountToDepositAfterClose.setMoneyAmount(accountToDepositAfterClose.getMoneyAmount() + accountToClose.getMoneyAmount());

            sessionFactory.getCurrentSession().remove(accountToClose);
            return accountToClose;
        });
    }

    public void transferMoney(Long fromAccountId, Long toAccountId, int amountToTransfer) {
        if (amountToTransfer <= 0) {
            throw new IllegalArgumentException("Невозможно перевести 0 или отрицательную сумму: amountToTransfer = %s"
                    .formatted(amountToTransfer));
        }

        transactionHelper.executeInTransaction(() -> {
            Account accountFrom = getAccountById(fromAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(fromAccountId)));

            Account accountTo = getAccountById(toAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(toAccountId)));


            if (accountFrom.getMoneyAmount() < amountToTransfer) {
                throw new IllegalArgumentException("Нельзя снять сумму %s с аккаунта %s, так как на аккаунте %s"
                        .formatted(amountToTransfer, accountFrom.getId(), accountFrom.getMoneyAmount()));
            }

            int totalAmount = !accountTo.getOwner().getId().equals(accountFrom.getOwner().getId()) ?
                    (int) (amountToTransfer * (1 - accountProperties.getDefaultTransferComission())) :
                    amountToTransfer;

            accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amountToTransfer);
            accountTo.setMoneyAmount(accountTo.getMoneyAmount() + totalAmount);

            return 0;
        });
    }

    private Optional<Account> getAccountById(Long id) {
            var account = sessionFactory.getCurrentSession().find(Account.class, id);
            return Optional.of(account);
    }
}
