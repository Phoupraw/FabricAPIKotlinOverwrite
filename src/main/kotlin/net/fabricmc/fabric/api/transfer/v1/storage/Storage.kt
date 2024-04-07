package net.fabricmc.fabric.api.transfer.v1.storage

import com.google.common.collect.Iterators
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.impl.transfer.TransferApiImpl
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface Storage<T> : Iterable<StorageView<T>> {
    fun supportsInsertion(): Boolean = true
    fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long
    fun supportsExtraction(): Boolean = true
    fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long
    override fun iterator(): Iterator<StorageView<T>>
    fun nonEmptyIterator(): Iterator<StorageView<T>> = Iterators.filter(iterator()) { it.amount > 0 && !it.resourceBlank }
    fun nonEmptyViews(): Iterable<StorageView<T>> = Iterable { nonEmptyIterator() }
    val version: Long
        get() {
            check(!Transaction.isOpen()) { "getVersion() may not be called during a transaction." }
            return TransferApiImpl.version.getAndIncrement()
        }
    
    //companion object {
    //    @JvmStatic
    //    @Suppress("UNCHECKED_CAST")
    //    fun <T> empty(): Storage<T> = TransferApiImpl.EMPTY_STORAGE as Storage<T>
    //    @JvmStatic
    //    @Suppress("UNCHECKED_CAST")
    //    fun <T> asClass(): Class<Storage<T>> = Storage::class.java as Class<Storage<T>>
    //}
}
