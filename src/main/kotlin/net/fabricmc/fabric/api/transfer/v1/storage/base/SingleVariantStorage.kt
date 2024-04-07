@file:Suppress("UNUSED_PARAMETER", "DEPRECATION")

package net.fabricmc.fabric.api.transfer.v1.storage.base

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.nbt.NbtCompound
import org.jetbrains.annotations.ApiStatus
import kotlin.math.min

/**
 * A storage that can store a single transfer variant at any given time.
 * Implementors should at least override [.getCapacity],
 * and probably [.onFinalCommit] as well for `markDirty()` and similar calls.
 *
 *
 * [.canInsert] and [.canExtract] can be used for more precise control over which variants may be inserted or extracted.
 * If one of these two functions is overridden to always return false, implementors may also wish to override
 * [.supportsInsertion] and/or [.supportsExtraction].
 *
 *
 * **Experimental feature**, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 *
 * @see net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage SingleFluidStorage for fluid variants.
 *
 * @see net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage SingleItemStorage for item variants.
 */
@ApiStatus.Experimental
abstract class SingleVariantStorage<T : TransferVariant<*>> : SnapshotParticipant<ResourceAmount<T>>(), SingleSlotStorage<T> {
    override var resource = blankVariant
    override var amount: Long = 0
    protected abstract val blankVariant: T
    /**
     * Return the maximum capacity of this storage for the passed transfer variant.
     * If the passed variant is blank, an estimate should be returned.
     */
    protected abstract fun getCapacity(variant: T): Long
    /**
     * @return `true` if the passed non-blank variant can be inserted, `false` otherwise.
     */
    protected fun canInsert(variant: T): Boolean {
        return true
    }
    /**
     * @return `true` if the passed non-blank variant can be extracted, `false` otherwise.
     */
    protected fun canExtract(variant: T): Boolean {
        return true
    }
    /**
     * Simple implementation of writing to NBT. Other formats are allowed, this is just a convenient suggestion.
     */
    // Reading from NBT is not provided because it would need to call the static FluidVariant/ItemVariant.fromNbt
    fun writeNbt(nbt: NbtCompound) {
        nbt.put("variant", resource.toNbt())
        nbt.putLong("amount", amount)
    }
    
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        if ((resource == this.resource || this.resource.blank) && canInsert(resource)) {
            val insertedAmount = min(maxAmount, getCapacity(resource) - amount)
            if (insertedAmount > 0) {
                updateSnapshots(transaction)
                if (this.resource.blank) {
                    this.resource = resource
                    amount = insertedAmount
                } else {
                    amount += insertedAmount
                }
                return insertedAmount
            }
        }
        return 0
    }
    
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        if (resource == this.resource && canExtract(resource)) {
            val extractedAmount = min(maxAmount, amount)
            if (extractedAmount > 0) {
                updateSnapshots(transaction)
                amount -= extractedAmount
                if (amount == 0L) {
                    this.resource = blankVariant
                }
                return extractedAmount
            }
        }
        return 0
    }
    
    override val resourceBlank: Boolean
        get() = resource.blank
    override val capacity: Long
        get() = getCapacity(resource)
    
    override fun createSnapshot(): ResourceAmount<T> {
        return ResourceAmount(resource, amount)
    }
    
    override fun readSnapshot(snapshot: ResourceAmount<T>) {
        resource = snapshot.resource
        amount = snapshot.amount
    }
    
    override fun toString(): String {
        return "SingleVariantStorage[%d %s]".formatted(amount, resource)
    }
}
