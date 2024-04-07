package net.fabricmc.fabric.api.transfer.v1.storage

import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.util.crash.CrashException
import net.minecraft.util.crash.CrashReport
import net.minecraft.util.math.MathHelper
import org.jetbrains.annotations.ApiStatus
import java.util.*
import java.util.function.Predicate

@ApiStatus.Experimental
object StorageUtil {
    
    @JvmStatic
    fun <T> move(from: Storage<T>?, to: Storage<T>?, filter: Predicate<T>, maxAmount: Long, transaction: TransactionContext?): Long {
        Objects.requireNonNull(filter, "Filter may not be null")
        if (from == null || to == null) return 0
        var totalMoved: Long = 0
        try {
            Transaction.openNested(transaction).use { iterationTransaction ->
                for (view in from.nonEmptyViews()) {
                    val resource: T = view.resource
                    if (!filter.test(resource)) continue
                    
                    // check how much can be extracted
                    val maxExtracted = simulateExtract(view, resource, maxAmount - totalMoved, iterationTransaction)
                    iterationTransaction.openNested().use { transferTransaction ->
                        // check how much can be inserted
                        val accepted = to.insert(resource, maxExtracted, transferTransaction)
                        
                        // extract it, or rollback if the amounts don't match
                        if (view.extract(resource, accepted, transferTransaction) == accepted) {
                            totalMoved += accepted
                            transferTransaction.commit()
                        }
                    }
                    if (maxAmount == totalMoved) {
                        // early return if nothing can be moved anymore
                        iterationTransaction.commit()
                        return totalMoved
                    }
                }
                iterationTransaction.commit()
            }
        } catch (e: Exception) {
            val report = CrashReport.create(e, "Moving resources between storages")
            report.addElement("Move details")
              .add("Input storage") { from.toString() }
              .add("Output storage") { to.toString() }
              .add("Filter") { filter.toString() }
              .add("Max amount", maxAmount)
              .add("Transaction", transaction)
            throw CrashException(report)
        }
        return totalMoved
    }
    
    fun <T> simulateInsert(storage: Storage<T>, resource: T, maxAmount: Long, transaction: TransactionContext?): Long {
        Transaction.openNested(transaction).use { simulateTransaction -> return storage.insert(resource, maxAmount, simulateTransaction) }
    }
    
    @JvmStatic
    fun <T> simulateExtract(storage: Storage<T>, resource: T, maxAmount: Long, transaction: TransactionContext?): Long {
        Transaction.openNested(transaction).use { simulateTransaction -> return storage.extract(resource, maxAmount, simulateTransaction) }
    }
    
    fun <T> simulateExtract(storageView: StorageView<T>, resource: T, maxAmount: Long, transaction: TransactionContext?): Long {
        Transaction.openNested(transaction).use { simulateTransaction -> return storageView.extract(resource, maxAmount, simulateTransaction) }
    }
    
    // Object & is used to have a different erasure than the other overloads.
    fun <T, S> simulateExtract(storage: S, resource: T, maxAmount: Long, transaction: TransactionContext?): Long where S : Any, S : Storage<T>, S : StorageView<T> {
        Transaction.openNested(transaction).use { simulateTransaction -> return storage.extract(resource, maxAmount, simulateTransaction) }
    }
    
    fun <T> extractAny(storage: Storage<T>?, maxAmount: Long, transaction: TransactionContext): ResourceAmount<T>? {
        StoragePreconditions.notNegative(maxAmount)
        if (storage == null) return null
        try {
            for (view in storage.nonEmptyViews()) {
                val resource: T = view.resource
                val amount = view.extract(resource, maxAmount, transaction)
                if (amount > 0) return ResourceAmount(resource, amount)
            }
        } catch (e: Exception) {
            val report = CrashReport.create(e, "Extracting resources from storage")
            report.addElement("Extraction details")
              .add("Storage") { storage.toString() }
              .add("Max amount", maxAmount)
              .add("Transaction", transaction)
            throw CrashException(report)
        }
        return null
    }
    
    fun <T> insertStacking(slots: List<SingleSlotStorage<T>>, resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notNegative(maxAmount)
        var amount: Long = 0
        try {
            for (slot in slots) {
                if (!slot.resourceBlank) {
                    amount += slot.insert(resource, maxAmount - amount, transaction)
                    if (amount == maxAmount) return amount
                }
            }
            for (slot in slots) {
                amount += slot.insert(resource, maxAmount - amount, transaction)
                if (amount == maxAmount) return amount
            }
        } catch (e: Exception) {
            val report = CrashReport.create(e, "Inserting resources into slots")
            report.addElement("Slotted insertion details")
              .add("Slots") { Objects.toString(slots, null) }
              .add("Resource") { Objects.toString(resource, null) }
              .add("Max amount", maxAmount)
              .add("Transaction", transaction)
            throw CrashException(report)
        }
        return amount
    }
    
    fun <T> tryInsertStacking(storage: Storage<T>?, resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notNegative(maxAmount)
        return try {
            if (storage is SlottedStorage<T>) {
                insertStacking(storage.slots, resource, maxAmount, transaction)
            } else storage?.insert(resource, maxAmount, transaction) ?: 0
        } catch (e: Exception) {
            val report = CrashReport.create(e, "Inserting resources into a storage")
            report.addElement("Insertion details")
              .add("Storage") { Objects.toString(storage, null) }
              .add("Resource") { Objects.toString(resource, null) }
              .add("Max amount", maxAmount)
              .add("Transaction", transaction)
            throw CrashException(report)
        }
    }
    
    fun <T> findStoredResource(storage: Storage<T>?): T? {
        return findStoredResource(storage) { true }
    }
    
    fun <T> findStoredResource(storage: Storage<T>?, filter: Predicate<T>): T? {
        Objects.requireNonNull(filter, "Filter may not be null")
        if (storage == null) return null
        for (view in storage.nonEmptyViews()) {
            if (filter.test(view.resource)) {
                return view.resource
            }
        }
        return null
    }
    
    @JvmStatic
    fun <T> findExtractableResource(storage: Storage<T>, transaction: TransactionContext?): T? {
        return findExtractableResource(storage, { true }, transaction)
    }
    
    fun <T> findExtractableResource(storage: Storage<T>?, filter: Predicate<T>, transaction: TransactionContext?): T? {
        Objects.requireNonNull(filter, "Filter may not be null")
        if (storage == null) return null
        Transaction.openNested(transaction).use { nested ->
            for (view in storage.nonEmptyViews()) {
                // Extract below could change the resource, so we have to query it before extracting.
                val resource: T = view.resource
                if (filter.test(resource) && view.extract(resource, Long.MAX_VALUE, nested) > 0) {
                    // Will abort the extraction.
                    return resource
                }
            }
        }
        return null
    }
    
    fun <T> findExtractableContent(storage: Storage<T>, transaction: TransactionContext?): ResourceAmount<T>? {
        return findExtractableContent(storage, { true }, transaction)
    }
    
    fun <T> findExtractableContent(storage: Storage<T>, filter: Predicate<T>, transaction: TransactionContext?): ResourceAmount<T>? {
        val extractableResource = findExtractableResource(storage, filter, transaction)
        if (extractableResource != null) {
            val extractableAmount = simulateExtract(storage, extractableResource, Long.MAX_VALUE, transaction)
            if (extractableAmount > 0) {
                return ResourceAmount(extractableResource, extractableAmount)
            }
        }
        return null
    }
    
    fun <T> calculateComparatorOutput(storage: Storage<T>?): Int {
        if (storage == null) return 0
        var fillPercentage = 0.0
        var viewCount = 0
        var hasNonEmptyView = false
        for (view in storage) {
            viewCount++
            if (view.amount > 0) {
                fillPercentage += view.amount.toDouble() / view.capacity
                hasNonEmptyView = true
            }
        }
        return MathHelper.floor(fillPercentage / viewCount * 14) + if (hasNonEmptyView) 1 else 0
    }
}
