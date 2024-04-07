package net.fabricmc.fabric.api.transfer.v1.storage

import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import org.jetbrains.annotations.ApiStatus

/**
 * An immutable association of an immutable object instance (for example `Item` or `Fluid`) and an optional NBT tag.
 *
 *
 * This is exposed for convenience for code that needs to be generic across multiple transfer variants,
 * but note that a [Storage] is not necessarily bound to `TransferVariant`. Its generic parameter can be any immutable object.
 *
 *
 * **Transfer variants must always be compared with `equals`, never by reference!**
 * `hashCode` is guaranteed to be correct and constant time independently of the size of the NBT.
 *
 * @param <O> The type of the immutable object instance, for example `Item` or `Fluid`.
 *
 * **Experimental feature**, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
</O> */
@ApiStatus.Experimental
interface TransferVariant<O> {
    /**
     * Return true if this variant is blank, and false otherwise.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @get:JvmName("isBlank")
    val blank: Boolean
    /**
     * Return the immutable object instance of this variant.
     */
    val `object`: O
    /**
     * Return the underlying tag.
     *
     *
     * **NEVER MUTATE THIS NBT TAG**, if you need to mutate it you can use [.copyNbt] to retrieve a copy instead.
     */
    val nbt: NbtCompound?
    /**
     * Return true if this variant has a tag, false otherwise.
     */
    fun hasNbt(): Boolean = nbt != null
    /**
     * Return true if the tag of this variant matches the passed tag, and false otherwise.
     *
     *
     * Note: True is returned if both tags are `null`.
     */
    fun nbtMatches(other: NbtCompound?): Boolean = nbt == other
    /**
     * Return `true` if the object of this variant matches the passed fluid.
     */
    fun isOf(`object`: O): Boolean = this.`object` == `object`
    /**
     * Return a copy of the tag of this variant, or `null` if this variant doesn't have a tag.
     *
     *
     * Note: Use [.nbtMatches] if you only need to check for custom tag equality, or [.getNbt] if you don't need to mutate the tag.
     */
    fun copyNbt(): NbtCompound? = this.nbt?.copy()
    /**
     * Return a copy of the tag of this variant, or a new compound if this variant doesn't have a tag.
     */
    fun copyOrCreateNbt(): NbtCompound = nbt?.copy() ?: NbtCompound()
    /**
     * Save this variant into an NBT compound tag. Subinterfaces should have a matching static `fromNbt`.
     *
     *
     * Note: This is safe to use for persisting data as objects are saved using their full Identifier.
     */
    fun toNbt(): NbtCompound
    /**
     * Write this variant into a packet byte buffer. Subinterfaces should have a matching static `fromPacket`.
     *
     *
     * Implementation note: Objects are saved using their raw registry integer id.
     */
    fun toPacket(buf: PacketByteBuf)
}
