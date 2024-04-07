package net.fabricmc.fabric.api.transfer.v1.context

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface ContainerItemContext {
    fun <A> find(lookup: ItemApiLookup<A, ContainerItemContext>): A? {
        return if (itemVariant.blank) null else lookup.find(itemVariant.toStack(), this)
    }

    val itemVariant: ItemVariant get() = mainSlot.resource
    val amount: Long
        get() {
            check(!itemVariant.blank) { "Amount may not be queried when the current item variant is blank." }
            return mainSlot.amount
        }

    fun insert(itemVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        val mainInserted = mainSlot.insert(itemVariant, maxAmount, transaction)
        val overflowInserted = insertOverflow(itemVariant, maxAmount - mainInserted, transaction)
        return mainInserted + overflowInserted
    }

    fun extract(itemVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        return mainSlot.extract(itemVariant, maxAmount, transaction)
    }

    fun exchange(newVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(newVariant, maxAmount)
        transaction.openNested().use { nested ->
            val extracted = extract(itemVariant, maxAmount, nested)
            if (insert(newVariant, extracted, nested) == extracted) {
                nested.commit()
                return extracted
            }
        }
        return 0
    }

    val mainSlot: SingleSlotStorage<ItemVariant>
    fun insertOverflow(itemVariant: ItemVariant, maxAmount: Long, transactionContext: TransactionContext): Long
    val additionalSlots: List<SingleSlotStorage<ItemVariant>>
}
