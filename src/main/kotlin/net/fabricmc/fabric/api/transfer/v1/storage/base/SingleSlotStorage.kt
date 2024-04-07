package net.fabricmc.fabric.api.transfer.v1.storage.base

import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.impl.transfer.TransferApiImpl
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface SingleSlotStorage<T> : SlottedStorage<T>, StorageView<T> {
    override fun iterator(): Iterator<SingleSlotStorage<T>> = TransferApiImpl.singletonIterator(this)
    override val slotCount: Int get() = 1
    override fun getSlot(slot: Int): SingleSlotStorage<T> {
        if (slot != 0) throw IndexOutOfBoundsException("Slot $slot does not exist in a single-slot storage.")
        return this
    }
}
