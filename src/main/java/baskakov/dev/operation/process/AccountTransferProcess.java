package baskakov.dev.operation.process;

import baskakov.dev.operation.ProcessorOperation;
import baskakov.dev.operation.TypeOperation;
import baskakov.dev.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountTransferProcess implements ProcessorOperation {
    private final Scanner scanner;
    private final AccountService accountService;

    public AccountTransferProcess(Scanner scanner,
                                  AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void process() {
        System.out.println("Введите идентификатор счета откуда хотите перевести средства:");
        int fromAccId = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите идентификатор счета куда хотите перевести средства:");
        int toAccId = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите сумму перевода:");
        int amount = Integer.parseInt(scanner.nextLine());
        accountService.transferMoney(fromAccId, toAccId, amount);
        System.out.println("Со счета %s на счет %s успешно переведено %s"
                .formatted(fromAccId, toAccId, amount));
    }

    @Override
    public TypeOperation getTypeOperation() {
        return TypeOperation.ACCOUNT_TRANSFER;
    }
}
