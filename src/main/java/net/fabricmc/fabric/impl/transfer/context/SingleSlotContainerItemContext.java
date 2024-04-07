package net.fabricmc.fabric.impl.transfer.context;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SingleSlotContainerItemContext implements ContainerItemContext {
    private final SingleSlotStorage<ItemVariant> slot;
    public SingleSlotContainerItemContext(SingleSlotStorage<ItemVariant> slot) {
        this.slot = Objects.requireNonNull(slot);
    }
    @Override
    public @NotNull SingleSlotStorage<ItemVariant> getMainSlot() {
        return slot;
    }
    @Override
    public long insertOverflow(@NotNull ItemVariant itemVariant, long maxAmount, @NotNull TransactionContext transactionContext) {
        return 0;
    }
    @Override
    public @NotNull List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
        return Collections.emptyList();
    }
    @Override
    public String toString() {
        return "SingleSlotContainerItemContext[%d %s %s]"
          .formatted(slot.getAmount(), slot.getResource(), slot);
    }
}
