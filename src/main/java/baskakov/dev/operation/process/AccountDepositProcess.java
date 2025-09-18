package baskakov.dev.operation.process;

import baskakov.dev.operation.ProcessorOperation;
import baskakov.dev.operation.TypeOperation;
import baskakov.dev.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountDepositProcess implements ProcessorOperation {
    private final Scanner scanner;
    private final AccountService accountService;

    public AccountDepositProcess(Scanner scanner,
                                 AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void process() {
        System.out.println("Введите идентификатор счета для пополнения:");
        Long accountId = Long.parseLong(scanner.nextLine());
        System.out.println("Введите сумму для пополнения:");
        int amount = Integer.parseInt(scanner.nextLine());
        accountService.depositToAccount(accountId, amount);
        System.out.println("Счет %s успешно пополнен на %s"
                .formatted(accountId, amount));
    }

    @Override
    public TypeOperation getTypeOperation() {
        return TypeOperation.ACCOUNT_DEPOSIT;
    }
}
