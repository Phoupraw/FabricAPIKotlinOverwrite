package net.fabricmc.fabric.api.transfer.v1.storage.base

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * A [Storage] wrapping multiple storages.
 *
 *
 * The storages passed to [the constructor][CombinedStorage.CombinedStorage] will be iterated in order.
 *
 * @param <T> The type of the stored resources.
 * @param <S> The class of every part. `? extends Storage<T>` can be used if the parts are of different types.
 *
 * **Experimental feature**, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
open class CombinedStorage<T, S : Storage<T>>(@JvmField var parts: List<S>) : Storage<T> {
    override fun supportsInsertion(): Boolean = parts.all { it.supportsInsertion() }
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notNegative(maxAmount)
        var amount = 0L
        for (part in parts) {
            amount += part.insert(resource, maxAmount - amount, transaction)
            if (amount == maxAmount) break
        }
        return amount
    }
    
    override fun supportsExtraction(): Boolean = parts.all { it.supportsExtraction() }
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notNegative(maxAmount)
        var amount = 0L
        for (part in parts) {
            amount += part.extract(resource, maxAmount - amount, transaction)
            if (amount == maxAmount) break
        }
        return amount
    }
    
    override fun iterator(): Iterator<StorageView<T>> = CombinedIterator()
    override fun toString(): String = parts.joinToString(", ", "CombinedStorage[", "]")
    /**
     * The combined iterator for multiple storages.
     */
    private inner class CombinedIterator : Iterator<StorageView<T>> {
        val partIterator = parts.iterator()
        // Always holds the next StorageView<T>, except during next() while the iterator is being advanced.
        var currentPartIterator: Iterator<StorageView<T>>? = null
        
        init {
            advanceCurrentPartIterator()
        }
        
        override fun hasNext(): Boolean = currentPartIterator!!.hasNext()
        override fun next(): StorageView<T> {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val returned = currentPartIterator!!.next()
            
            // Advance the current part iterator
            if (!currentPartIterator!!.hasNext()) {
                advanceCurrentPartIterator()
            }
            return returned
        }
        
        private fun advanceCurrentPartIterator() {
            while (partIterator.hasNext()) {
                currentPartIterator = partIterator.next().iterator()
                if ((currentPartIterator ?: return).hasNext()) {
                    break
                }
            }
        }
    }
}
