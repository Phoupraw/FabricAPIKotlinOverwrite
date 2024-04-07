@file:Suppress("INAPPLICABLE_JVM_NAME")

package net.fabricmc.fabric.api.transfer.v1.storage

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface StorageView<T> {
    fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long
    @get:JvmName("isResourceBlank")
    val resourceBlank: Boolean
    val resource: T
    val amount: Long
    val capacity: Long
    val underlyingView: StorageView<T> get() = this
}
