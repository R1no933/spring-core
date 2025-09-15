package baskakov.dev.operation.process;

import baskakov.dev.model.User;
import baskakov.dev.operation.ProcessorOperation;
import baskakov.dev.operation.TypeOperation;
import baskakov.dev.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class UserCreateProcess implements ProcessorOperation {
    private final Scanner scanner;
    private final UserService userService;

    public UserCreateProcess(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }

    @Override
    public void process() {
        System.out.println("Введите имя пользователя для создания нового пользователя:");
        String username = scanner.next();
        User user = userService.createUser(username);
        System.out.println("Пользователь %s успешно создан".formatted(user.toString()));
    }

    @Override
    public TypeOperation getTypeOperation() {
        return TypeOperation.USER_CREATE;
    }
}
