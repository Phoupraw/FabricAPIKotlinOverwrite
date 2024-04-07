@file:Suppress("OVERLOADS_INTERFACE")

package net.fabricmc.fabric.api.transfer.v1.item

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
@ApiStatus.NonExtendable
interface ItemVariant : TransferVariant<Item> {
    fun matches(stack: ItemStack): Boolean = isOf(stack.item) && nbtMatches(stack.nbt)
    val item: Item
        get() = `object`
    
    fun toStack() = toStack(1)
    fun toStack(count: Int): ItemStack {
        if (blank) return ItemStack.EMPTY
        val stack = ItemStack(item, count)
        stack.nbt = copyNbt()
        return stack
    }
    
//    companion object /*Blank : ItemVariant*/ {
//        @JvmStatic
//        fun blank(): ItemVariant = of(Items.AIR)
//        @JvmStatic
//        fun of(stack: ItemStack): ItemVariant = of(stack.item, stack.nbt)
//        @JvmOverloads
//        @JvmStatic
//        fun of(item: ItemConvertible, tag: NbtCompound? = null): ItemVariant = ItemVariantImpl.of(item.asItem(), tag)
//        @JvmStatic
//        fun fromNbt(nbt: NbtCompound): ItemVariant = ItemVariantImpl.fromNbt(nbt)
//        @JvmStatic
//        fun fromPacket(buf: PacketByteBuf): ItemVariant = ItemVariantImpl.fromPacket(buf)
////        override val blank: Boolean
////            get() = true
////        override val `object`: Item
////            get() = Items.AIR
////        override val nbt: NbtCompound?
////            get() = null
////
////        override fun toNbt(): NbtCompound = NbtCompound()
////        override fun toPacket(buf: PacketByteBuf) {
////            buf.writeBoolean(false)
////        }
//    }
}
