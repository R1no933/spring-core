package baskakov.dev.listner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ConsoleListnerStarter {
    private final ConsoleListener consoleListener;
    private Thread threadListener;

    public ConsoleListnerStarter(ConsoleListener consoleListener) {
        this.consoleListener = consoleListener;
    }

    @PostConstruct
    public void postConstruct() {
        this.threadListener = new Thread(() -> {
            consoleListener.start();
            consoleListener.listen();
        });
        threadListener.start();
    }

    @PreDestroy
    public void preDestroy() {
        threadListener.interrupt();
        consoleListener.stop();
    }
}
