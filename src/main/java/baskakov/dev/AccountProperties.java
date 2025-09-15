package baskakov.dev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountProperties {
    private final int defaultAmount;
    private final double defaultTransferComission;

    public AccountProperties(
            @Value("${account.default-amount}") int defaultAmount,
            @Value("${account.transfer-commission}") double defaultTransferCommission) {
        this.defaultAmount = defaultAmount;
        this.defaultTransferComission = defaultTransferCommission;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public double getDefaultTransferComission() {
        return defaultTransferComission;
    }
}
