package net.fabricmc.fabric.api.transfer.v1.storage.base

import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * A [Storage] wrapping multiple slotted storages.
 * Same as [CombinedStorage], but for [SlottedStorage]s.
 *
 * @param <T> The type of the stored resources.
 * @param <S> The class of every part. `? extends Storage<T>` can be used if the parts are of different types.
 *
 * **Experimental feature**, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
open class CombinedSlottedStorage<T, S : SlottedStorage<T>>(parts: List<S>) : CombinedStorage<T, S>(parts), SlottedStorage<T> {
    override val slotCount: Int
        get() = parts.sumOf { it.slotCount }
    
    override fun getSlot(slot: Int): SingleSlotStorage<T> {
        var updatedSlot = slot
        for (part in parts) {
            if (updatedSlot < part.slotCount) {
                return part.getSlot(updatedSlot)
            }
            updatedSlot -= part.slotCount
        }
        throw IndexOutOfBoundsException("Slot $slot is out of bounds. This storage has size $slotCount")
    }
    
    override fun toString(): String = parts.joinToString(", ", "CombinedSlottedStorage[", "]")
    override fun iterator(): Iterator<SingleSlotStorage<T>> {
        return parts.asSequence().flatMap { it.iterator().asSequence() }.iterator()
    }
}
