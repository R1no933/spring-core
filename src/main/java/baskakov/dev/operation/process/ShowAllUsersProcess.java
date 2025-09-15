package baskakov.dev.operation.process;

import baskakov.dev.model.User;
import baskakov.dev.operation.ProcessorOperation;
import baskakov.dev.operation.TypeOperation;
import baskakov.dev.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShowAllUsersProcess implements ProcessorOperation {
    private final UserService userService;

    public ShowAllUsersProcess(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст!\n");
            return;
        }
        System.out.println("Список всех пользователей:");
        users.forEach(System.out::println);
        System.out.println();
    }

    @Override
    public TypeOperation getTypeOperation() {
        return TypeOperation.SHOW_ALL_USERS;
    }
}
