package net.fabricmc.fabric.api.transfer.v1.item

import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.Direction
import org.jetbrains.annotations.ApiStatus


@ApiStatus.Experimental
//@ApiStatus.NonExtendable
interface InventoryStorage : SlottedStorage<ItemVariant> {
    /**
     * Retrieve an unmodifiable list of the wrappers for the slots in this inventory.
     * Each wrapper corresponds to a single slot in the inventory.
     */
    override val slots: List<SingleSlotStorage<ItemVariant>>
    override val slotCount: Int get() = slots.size
    override fun getSlot(slot: Int): SingleSlotStorage<ItemVariant> = slots[slot]
    override fun iterator(): Iterator<SingleSlotStorage<ItemVariant>>
    //companion object {
    //    @JvmStatic
    //    fun of(inventory: Inventory, direction: Direction?): InventoryStorage {
    //        //Objects.requireNonNull(inventory, "Null inventory is not supported.");
    //        //return InventoryStorageImpl.of(inventory, direction);
    //    }
    //}
}
