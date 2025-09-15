package baskakov.dev.service;

import baskakov.dev.configure.AccountProperties;
import baskakov.dev.model.Account;
import baskakov.dev.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final Map<Integer, Account> accountMap;
    private final AccountProperties accountProperties;
    private int idCounter;

    public AccountService(AccountProperties accountProperties) {
        this.accountMap = new HashMap<>();
        this.accountProperties = accountProperties;
        this.idCounter = 0;
    }

    public Account createAccount(User user) {
        idCounter++;
        Account account = new Account(idCounter, user.getId(), accountProperties.getDefaultAmount());
        accountMap.put(account.getId(), account);
        return account;
    }

    public Optional<Account> getAccountById(int id) {
        return Optional.ofNullable(accountMap.get(id));
    }

    public List<Account> getAllAccountsForUser(int userId) {
        return accountMap.values()
                .stream()
                .filter(acc -> acc.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public void depositToAccount(int accountId, int amountToDeposit) {
        Account account = getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(accountId)));

        if (amountToDeposit <= 0) {
            throw new IllegalArgumentException("Невозможно внести 0 или отрицательную сумму: amountToDeposit = %s"
                    . formatted(amountToDeposit));
        }

        account.setMoneyAmount(account.getMoneyAmount() + amountToDeposit);
    }

    public void withdrawFromAccount(int accountId, int amountToWithdraw) {
        Account account = getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(accountId)));

        if (amountToWithdraw <= 0) {
            throw new IllegalArgumentException("Невозможно снять 0 или отрицательную сумму: amountToWithdraw = %s"
                    . formatted(amountToWithdraw));
        }

        if (account.getMoneyAmount() < amountToWithdraw) {
            throw new IllegalArgumentException("Нельзя снять сумму %s с аккаунта %s, так как на аккаунте %s"
            . formatted(amountToWithdraw, accountId, account.getMoneyAmount()));
        }

        account.setMoneyAmount(account.getMoneyAmount() - amountToWithdraw);
    }

    public Account closeAccountById(int accountId) {
        Account accountToClose = getAccountById(accountId).
                orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(accountId)));

        List<Account> accountList = getAllAccountsForUser(accountId);
        if (accountList.size() == 1) {
            throw new IllegalArgumentException("Невозможно удалить единственный аккаунт");
        }

        Account accountToDeposipAfterClose = accountList.stream()
                .filter(acc -> acc.getId() != accountId)
                .findFirst()
                .orElseThrow();

        accountToDeposipAfterClose.setMoneyAmount(accountToDeposipAfterClose.getMoneyAmount() + accountToClose.getMoneyAmount());
        accountMap.remove(accountId);
        return accountToClose;
    }

    public void transferMoney(int fromAccountId, int toAccountId, int amountToTransfer) {
        Account accountFrom = getAccountById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(fromAccountId)));

        Account accountTo = getAccountById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет с id $s не найден!".formatted(toAccountId)));

        if (amountToTransfer <= 0) {
            throw new IllegalArgumentException("Невозможно перевести 0 или отрицательную сумму: amountToTransfer = %s"
                    . formatted(amountToTransfer));
        }

        if (accountFrom.getMoneyAmount() < amountToTransfer) {
            throw new IllegalArgumentException("Нельзя снять сумму %s с аккаунта %s, так как на аккаунте %s"
                    . formatted(amountToTransfer, accountFrom.getId(), accountFrom.getMoneyAmount()));
        }

        int totalAmount = accountTo.getUserId() != accountFrom.getUserId() ?
                (int) (amountToTransfer * (1 - accountProperties.getDefaultTransferComission())) :
                amountToTransfer;

        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amountToTransfer);
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + totalAmount);
    }
}
