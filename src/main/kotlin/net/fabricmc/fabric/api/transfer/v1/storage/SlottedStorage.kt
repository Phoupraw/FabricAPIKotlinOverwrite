package net.fabricmc.fabric.api.transfer.v1.storage

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.impl.transfer.TransferApiImpl
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface SlottedStorage<T> : Storage<T> {
    val slotCount: Int
    fun getSlot(slot: Int): SingleSlotStorage<T>
    val slots: List<SingleSlotStorage<T>> get() = TransferApiImpl.makeListView(this)
    override fun iterator(): Iterator<SingleSlotStorage<T>>
    @Suppress("UNCHECKED_CAST")
    override fun nonEmptyIterator(): Iterator<SingleSlotStorage<T>> = super.nonEmptyIterator() as Iterator<SingleSlotStorage<T>>
    @Suppress("UNCHECKED_CAST")
    override fun nonEmptyViews(): Iterable<SingleSlotStorage<T>> = super.nonEmptyViews() as Iterable<SingleSlotStorage<T>>
}
