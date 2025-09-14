package baskakov.dev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
public class AccountProperties {
    private final int defaultAmount;
    private final double defaultTransferCommision;

    public AccountProperties(
            @Value("${account.default-amount}") int defaultAmount,
            @Value("${account.transfer-commission}") double defaultTransferCommision) {
        this.defaultAmount = defaultAmount;
        this.defaultTransferCommision = defaultTransferCommision;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public double getDefaultTransferCommision() {
        return defaultTransferCommision;
    }
}
