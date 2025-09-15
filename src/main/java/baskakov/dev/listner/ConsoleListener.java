package baskakov.dev.listner;

import baskakov.dev.operation.ProcessorOperation;
import baskakov.dev.operation.TypeOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class ConsoleListener {
    private final Scanner scanner;
    private final Map<TypeOperation, ProcessorOperation> mapOperations;

    public ConsoleListener(Scanner scanner,
                           List<ProcessorOperation> listOperations) {
        this.scanner = scanner;
        this.mapOperations = listOperations.stream().collect(Collectors.toMap(
                ProcessorOperation::getTypeOperation,
                processorOperation -> processorOperation
        ));
    }

    public void start() {
        System.out.println("Запущен поток ожидающий данные на ввод");
    }

    public void stop() {
        System.out.println("Остановлен поток ожидающий данные на ввод");
    }

    public void listen() {
        while (!Thread.currentThread().isInterrupted()) {
            var operation = listenOperation();
            if (operation == null) {
                return;
            }
            processOperation(operation);
        }
    }

    private TypeOperation listenOperation() {
        System.out.println("Выберите одну из операций:");
        printOperations();
        System.out.println();
        while (!Thread.currentThread().isInterrupted()) {
            String operation = scanner.nextLine();
            try {
                return TypeOperation.valueOf(operation);
            } catch (IllegalArgumentException e) {
                System.out.println("Не найдена указанная команда");
            }
        }
        return null;
    }

    private void printOperations() {
        mapOperations.keySet().forEach(System.out::println);
    }

    private void processOperation(TypeOperation operation) {
        try {
            var process = mapOperations.get(operation);
            process.process();
        } catch (Exception e) {
            System.out.printf("При вызове метода %s, произошла ошибка %s\n", operation, e.getMessage());
        }
    }
}
