package net.fabricmc.fabric.api.transfer.v1.item

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.util.Hand
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface PlayerInventoryStorage : InventoryStorage {
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long
    fun offerOrDrop(variant: ItemVariant, amount: Long, transaction: TransactionContext) = drop(variant, amount - offer(variant, amount, transaction), transaction)
    fun offer(variant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long
    fun drop(variant: ItemVariant, amount: Long, throwRandomly: Boolean, retainOwnership: Boolean, transaction: TransactionContext)
    fun drop(variant: ItemVariant, amount: Long, retainOwnership: Boolean, transaction: TransactionContext) = drop(variant, amount, false, retainOwnership, transaction)
    fun drop(variant: ItemVariant, amount: Long, transaction: TransactionContext) = drop(variant, amount, false, transaction)
    fun getHandSlot(hand: Hand): SingleSlotStorage<ItemVariant>
}
