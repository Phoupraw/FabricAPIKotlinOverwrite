package net.fabricmc.fabric.impl.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link InventoryStorage}.
 * Note on thread-safety: we assume that Inventory's are inherently single-threaded, and no attempt is made at synchronization.
 * However, the access to implementations can happen on multiple threads concurrently, which is why we use a thread-safe wrapper map.
 */
@ApiStatus.NonExtendable
public class InventoryStorageImpl /*extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>>*/ /*implements InventoryStorage*/ {
    //private static final Map<Inventory, InventoryStorageImpl> WRAPPERS = new MapMaker().weakValues().makeMap();
    public static InventoryStorage of(Inventory inventory, @Nullable Direction direction) {
        throw new IllegalStateException();
        //InventoryStorageImpl storage = WRAPPERS.computeIfAbsent(inventory, inv -> {
        //	if (inv instanceof PlayerInventory playerInventory) {
        //		return new PlayerInventoryStorageImpl(playerInventory);
        //	} else {
        //		return new InventoryStorageImpl(inv);
        //	}
        //});
        //storage.resizeSlotList();
        //return storage.getSidedWrapper(direction);
    }
    //final Inventory inventory;
    ///**
    // * This {@code backingList} is the real list of wrappers.
    // * The {@code parts} in the superclass is the public-facing unmodifiable sublist with exactly the right amount of slots.
    // */
    //final List<InventorySlotWrapper> backingList;
    ///**
    // * This participant ensures that markDirty is only called once for the entire inventory.
    // */
    //final MarkDirtyParticipant markDirtyParticipant = new MarkDirtyParticipant();
    InventoryStorageImpl(Inventory inventory) {
        //super(Collections.emptyList());
        //this.inventory = inventory;
        //this.backingList = new ArrayList<>();
    }
    //@Override
    //public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
    //	return super.iterator();
    //}
    //@Override
    //public List<SingleSlotStorage<ItemVariant>> getSlots() {
    //	return parts;
    //}
    
    ///**
    // * Resize slot list to match the current size of the inventory.
    // */
    //private void resizeSlotList() {
    //	int inventorySize = inventory.size();
    //
    //	// If the public-facing list must change...
    //	if (inventorySize != parts.size()) {
    //		// Ensure we have enough wrappers in the backing list.
    //		while (backingList.size() < inventorySize) {
    //			backingList.add(new InventorySlotWrapper(this, backingList.size()));
    //		}
    //
    //		// Update the public-facing list.
    //		parts = Collections.unmodifiableList(backingList.subList(0, inventorySize));
    //	}
    //}
    
    //private InventoryStorage getSidedWrapper(@Nullable Direction direction) {
    //	if (inventory instanceof SidedInventory && direction != null) {
    //		return new SidedInventoryStorageImpl(this, direction);
    //	} else {
    //		return this;
    //	}
    //}
    
    //@Override
    //public String toString() {
    //	return "InventoryStorage[" + DebugMessages.forInventory(inventory) + "]";
    //}
    
    // Boolean is used to prevent allocation. Null values are not allowed by SnapshotParticipant.
    //class MarkDirtyParticipant extends SnapshotParticipant<Boolean> {
    //	@Override
    //	protected Boolean createSnapshot() {
    //		return Boolean.TRUE;
    //	}
    //
    //	@Override
    //	protected void readSnapshot(Boolean snapshot) {
    //	}
    //
    //	@Override
    //	protected void onFinalCommit() {
    //		inventory.markDirty();
    //	}
    //}
}
