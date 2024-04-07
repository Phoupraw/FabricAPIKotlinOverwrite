package net.fabricmc.fabric.api.transfer.v1.storage;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Preconditions that can be used when working with storages.
 *
 * <p>In particular, {@link #notNegative} or {@link #notBlankNotNegative} can be used by implementations of
 * {@link Storage#insert} and {@link Storage#extract} to fail-fast if the arguments are invalid.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public final class StoragePreconditions {
    /**
     * Ensure that the passed transfer variant is not blank.
     *
     * @throws IllegalArgumentException If the variant is blank.
     */
    public static void notBlank(@NotNull TransferVariant<?> variant) {
        if (variant.isBlank()) {
            throw new IllegalArgumentException("Transfer variant may not be blank.");
        }
    }
    /**
     * Ensure that the passed amount is not negative. That is, it must be {@code >= 0}.
     *
     * @throws IllegalArgumentException If the amount is negative.
     */
    @Contract(pure = true)
    public static void notNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount may not be negative, but it is: " + amount);
        }
    }
    /**
     * Check both for a not blank transfer variant and a not negative amount.
     */
    public static void notBlankNotNegative(TransferVariant<?> variant, long amount) {
        notBlank(variant);
        notNegative(amount);
    }
    private StoragePreconditions() {
    }
}
