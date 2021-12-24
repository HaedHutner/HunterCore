package dev.haedhutner.core.economy;

import dev.haedhutner.core.HunterCore;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public final class Economy {

    public static boolean isPresent() {
        return HunterCore.getEconomyService().isPresent();
    }

    public static Optional<TransferResult> transferCurrency(UUID source, UUID destination, Currency currency, BigDecimal amount, Cause cause) {
        return getAccount(source).flatMap(sourceAccount -> {
            return getAccount(destination).map(destinationAccount -> {
                return transfer(sourceAccount, destinationAccount, currency, amount, cause);
            });
        });
    }

    public static Optional<TransferResult> transferCurrency(UUID source, String destination, Currency currency, BigDecimal amount, Cause cause) {
        return getAccount(source).flatMap(sourceAccount -> {
            return getAccount(destination).map(destinationAccount -> {
                return transfer(sourceAccount, destinationAccount, currency, amount, cause);
            });
        });
    }

    public static Optional<TransferResult> transferCurrency(String source, UUID destination, Currency currency, BigDecimal amount, Cause cause) {
        return getAccount(source).flatMap(sourceAccount -> {
            return getAccount(destination).map(destinationAccount -> {
                return transfer(sourceAccount, destinationAccount, currency, amount, cause);
            });
        });
    }

    public static Optional<TransferResult> transferCurrency(String source, String destination, Currency currency, BigDecimal amount, Cause cause) {
        return getAccount(source).flatMap(sourceAccount -> {
            return getAccount(destination).map(destinationAccount -> {
                return transfer(sourceAccount, destinationAccount, currency, amount, cause);
            });
        });
    }

    private static TransferResult transfer(Account source, Account destination, Currency currency, BigDecimal amount, Cause cause) {
        return source.transfer(
                destination,
                currency,
                amount,
                cause
        );
    }

    public static Optional<Account> getAccount(String account) {
        return HunterCore.getEconomyService().flatMap(econ -> econ.getOrCreateAccount(account));
    }

    public static Optional<UniqueAccount> getAccount(UUID account) {
        return HunterCore.getEconomyService().flatMap(econ -> econ.getOrCreateAccount(account));
    }

    public static void addCurrency(UUID account, Currency currency, BigDecimal amount, Cause cause) {
        HunterCore.getEconomyService().ifPresent(econ -> {
            econ.getOrCreateAccount(account).ifPresent(uniqueAccount -> {
                uniqueAccount.deposit(currency, amount, cause);
            });
        });
    }

    public static void removeCurrency(UUID account, Currency currency, BigDecimal amount, Cause cause) {
        HunterCore.getEconomyService().ifPresent(econ -> {
            econ.getOrCreateAccount(account).ifPresent(uniqueAccount -> {
                uniqueAccount.withdraw(currency, amount, cause);
            });
        });
    }

}
