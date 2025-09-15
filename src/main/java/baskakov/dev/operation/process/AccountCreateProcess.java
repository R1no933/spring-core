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
public class AccountCreateProcess implements ProcessorOperation {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    public AccountCreateProcess(Scanner scanner,
                                AccountService accountService,
                                UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void process() {
        System.out.println("Введите идентификатор пользователя, для которого хотите создать счет");
        try {
            int userId = Integer.parseInt(scanner.nextLine());
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с идентификатором %s не найден"
                            .formatted(userId)));
            Account account = accountService.createAccount(user);
            user.getAccountList().add(account);
            System.out.println("Новый счет успешно создан для пользователя %s"
                    .formatted(user.getLogin()));
        } catch (IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Override
    public TypeOperation getTypeOperation() {
        return TypeOperation.ACCOUNT_CREATE;
    }
}
