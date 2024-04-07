package net.fabricmc.fabric.api.transfer.v1.storage.base

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import org.jetbrains.annotations.ApiStatus

/**
 * A [Storage] that supports extraction, and not insertion.
 *
 *
 * **Experimental feature**, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
interface ExtractionOnlyStorage<T> : Storage<T> {
    override fun supportsInsertion(): Boolean = false
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
}
