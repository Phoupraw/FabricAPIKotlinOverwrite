package net.fabricmc.fabric.api.transfer.v1.storage.base

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * A [Storage] that supports insertion, and not extraction. By default, it doesn't have any storage view either.
 *
 *
 * **Experimental feature**, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
fun interface InsertionOnlyStorage<T> : Storage<T> {
    override fun supportsExtraction(): Boolean = false
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
    override fun iterator(): Iterator<StorageView<T>> = Collections.emptyIterator()
}
