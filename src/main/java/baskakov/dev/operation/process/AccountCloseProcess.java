package baskakov.dev.operation.process;

import baskakov.dev.model.Account;
import baskakov.dev.model.User;
import baskakov.dev.operation.ProcessorOperation;
import baskakov.dev.operation.TypeOperation;
import baskakov.dev.service.AccountService;
import baskakov.dev.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountCloseProcess implements ProcessorOperation {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    public AccountCloseProcess(Scanner scanner,
                               AccountService accountService,
                               UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void process() {
        System.out.println("Введите идентификатор счета, который хотите закрыть:");
        int accountId = Integer.parseInt(scanner.nextLine());
        Account accountToClose = accountService.closeAccountById(accountId);
        User user = userService.getUserById(accountToClose.getUserId()).orElseThrow(() ->
                new IllegalArgumentException("Пользователь %s не найден"
                        .formatted(accountToClose.getUserId())));
        user.getAccountList().remove(accountToClose);
        System.out.println("Счет успешно закрыт");
    }

    @Override
    public TypeOperation getTypeOperation() {
        return TypeOperation.ACCOUNT_CLOSE;
    }
}
