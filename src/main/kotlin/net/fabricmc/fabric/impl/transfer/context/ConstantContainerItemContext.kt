package net.fabricmc.fabric.impl.transfer.context

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

class ConstantContainerItemContext(initialVariant: ItemVariant, initialAmount: Long) : ContainerItemContext {
    private val backingSlot: SingleVariantStorage<ItemVariant> = object : SingleVariantStorage<ItemVariant>() {
        override val blankVariant: ItemVariant
            get() = TODO()
        
        override fun getCapacity(variant: ItemVariant): Long = Long.MAX_VALUE
        override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long = 0
        override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long = maxAmount
    }
    
    init {
        backingSlot.resource = initialVariant
        backingSlot.amount = initialAmount
    }
    
    override val mainSlot: SingleSlotStorage<ItemVariant>
        get() = backingSlot
    
    override fun insertOverflow(itemVariant: ItemVariant, maxAmount: Long, transactionContext: TransactionContext): Long = maxAmount
    override val additionalSlots: List<SingleSlotStorage<ItemVariant>>
        get() = emptyList()
    
    override fun toString(): String = TODO()
}
