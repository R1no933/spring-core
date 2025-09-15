package baskakov.dev.operation.process;

import baskakov.dev.operation.ProcessorOperation;
import baskakov.dev.operation.TypeOperation;
import baskakov.dev.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountWithdrawProcess implements ProcessorOperation {
    private final Scanner scanner;
    private final AccountService accountService;

    public AccountWithdrawProcess(Scanner scanner,
                                  AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void process() {
        System.out.println("Введите идентификатор счета для снятия средств:");
        int accountId = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите сумму для снятия:");
        int amount = Integer.parseInt(scanner.nextLine());
        accountService.withdrawFromAccount(accountId, amount);
        System.out.println("Со счета %s успешно снято %s"
                .formatted(accountId, amount));
    }

    @Override
    public TypeOperation getTypeOperation() {
        return TypeOperation.ACCOUNT_WITHDRAW;
    }
}
