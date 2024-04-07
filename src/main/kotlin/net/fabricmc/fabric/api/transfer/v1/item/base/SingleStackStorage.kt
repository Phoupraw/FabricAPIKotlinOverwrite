@file:Suppress("INAPPLICABLE_JVM_NAME")

package net.fabricmc.fabric.api.transfer.v1.item.base

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import kotlin.math.min

@ApiStatus.Experimental
abstract class SingleStackStorage : SnapshotParticipant<ItemStack>(), SingleSlotStorage<ItemVariant> {
    protected abstract var stack: ItemStack
    protected open fun canInsert(resource: ItemVariant): Boolean = true
    protected open fun canExtract(resource: ItemVariant): Boolean = true
    protected open fun getCapacity(resource: ItemVariant): Int = resource.item.maxCount
    override val resourceBlank: Boolean @JvmName("isResourceBlank") get() = stack.isEmpty
    override val resource: ItemVariant get() = TODO()
    override val amount: Long get() = stack.count.toLong()
    override val capacity: Long get() = getCapacity(resource).toLong()
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)

        var currentStack = stack

        if ((resource.matches(currentStack) || currentStack.isEmpty) && canInsert(resource)) {
            val insertedAmount = min(maxAmount.toDouble(), (getCapacity(resource) - currentStack.count).toDouble()).toInt()

            if (insertedAmount > 0) {
                updateSnapshots(transaction)
                currentStack = stack

                if (currentStack.isEmpty) {
                    currentStack = resource.toStack(insertedAmount)
                } else {
                    currentStack.increment(insertedAmount)
                }

                stack = currentStack

                return insertedAmount.toLong()
            }
        }

        return 0
    }

    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)

        var currentStack = stack

        if (resource.matches(currentStack) && canExtract(resource)) {
            val extracted = min(currentStack.count.toDouble(), maxAmount.toDouble()).toInt()

            if (extracted > 0) {
                this.updateSnapshots(transaction)
                currentStack = stack
                currentStack.decrement(extracted)
                stack = currentStack

                return extracted.toLong()
            }
        }

        return 0
    }

    override fun createSnapshot(): ItemStack {
        val original = stack
        stack = original.copy()
        return original
    }

    override fun readSnapshot(snapshot: ItemStack) {
        stack = snapshot
    }

    override fun toString(): String = "SingleStackStorage[$stack]"
}
