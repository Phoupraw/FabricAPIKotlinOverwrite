package net.fabricmc.fabric.impl.transfer.context;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.List;

public class PlayerContainerItemContext implements ContainerItemContext {
    private final PlayerInventoryStorage playerWrapper;
    private final SingleSlotStorage<ItemVariant> slot;
    public PlayerContainerItemContext(PlayerEntity player, Hand hand) {
        this.playerWrapper = PlayerInventoryStorage.of(player);
        this.slot = playerWrapper.getHandSlot(hand);
    }
    public PlayerContainerItemContext(PlayerEntity player, SingleSlotStorage<ItemVariant> slot) {
        this.playerWrapper = PlayerInventoryStorage.of(player);
        this.slot = slot;
    }
    @Override
    public SingleSlotStorage<ItemVariant> getMainSlot() {
        return slot;
    }
    @Override
    public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
        playerWrapper.offerOrDrop(itemVariant, maxAmount, transactionContext);
        return maxAmount;
    }
    @Override
    public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
        return playerWrapper.getSlots();
    }
    @Override
    public String toString() {
        return "PlayerContainerItemContext[%d %s %s/%s]"
          .formatted(slot.getAmount(), slot.getResource(), playerWrapper, slot);
    }
}
