package net.fabricmc.fabric.impl.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ItemVariantImpl implements ItemVariant {
    public static ItemVariant of(Item item, @Nullable NbtCompound tag) {
        throw new IllegalStateException();
        //Objects.requireNonNull(item, "Item may not be null.");
        //
        //// Only tag-less or empty item variants are cached for now.
        //if (tag == null || item == Items.AIR) {
        //	return ((ItemVariantCache) item).fabric_getCachedItemVariant();
        //} else {
        //	return new ItemVariantImpl(item, tag);
        //}
    }
    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-transfer-api-v1/item");
    private final Item item;
    private final @Nullable NbtCompound nbt;
    private final int hashCode;
    /**
     * Lazily computed, equivalent to calling toStack(1). <b>MAKE SURE IT IS NEVER MODIFIED!</b>
     */
    private volatile @Nullable ItemStack cachedStack = null;
    public ItemVariantImpl(Item item, NbtCompound nbt) {
        this.item = item;
        this.nbt = nbt == null ? null : nbt.copy(); // defensive copy
        hashCode = Objects.hash(item, nbt);
    }
    @Override
    public Item getObject() {
        return item;
    }
    @Nullable
    @Override
    public NbtCompound getNbt() {
        return nbt;
    }
    @Override
    public boolean isBlank() {
        return item == Items.AIR;
    }
    @Override
    public @NotNull NbtCompound toNbt() {
        NbtCompound result = new NbtCompound();
        result.putString("item", Registries.ITEM.getId(item).toString());
        
        if (nbt != null) {
            result.put("tag", nbt.copy());
        }
        
        return result;
    }
    public static ItemVariant fromNbt(NbtCompound tag) {
        throw new IllegalStateException();
        //try {
        //	Item item = Registries.ITEM.get(new Identifier(tag.getString("item")));
        //	NbtCompound aTag = tag.contains("tag") ? tag.getCompound("tag") : null;
        //	return of(item, aTag);
        //} catch (RuntimeException runtimeException) {
        //	LOGGER.debug("Tried to load an invalid ItemVariant from NBT: {}", tag, runtimeException);
        //	return ItemVariant.blank();
        //}
    }
    @Override
    public void toPacket(@NotNull PacketByteBuf buf) {
        if (isBlank()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeVarInt(Item.getRawId(item));
            buf.writeNbt(nbt);
        }
    }
    public static ItemVariant fromPacket(PacketByteBuf buf) {
        throw new IllegalStateException();
        //if (!buf.readBoolean()) {
        //	return ItemVariant.blank();
        //} else {
        //	Item item = Item.byRawId(buf.readVarInt());
        //	NbtCompound nbt = buf.readNbt();
        //	return of(item, nbt);
        //}
    }
    @Override
    public String toString() {
        return "ItemVariant{item=" + item + ", tag=" + nbt + '}';
    }
    @Override
    public boolean equals(Object o) {
        // succeed fast with == check
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ItemVariantImpl ItemVariant = (ItemVariantImpl) o;
        // fail fast with hash code
        return hashCode == ItemVariant.hashCode && item == ItemVariant.item && nbtMatches(ItemVariant.nbt);
    }
    @Override
    public int hashCode() {
        return hashCode;
    }
    public ItemStack getCachedStack() {
        ItemStack ret = cachedStack;
        
        if (ret == null) {
            // multiple stacks could be created at the same time by different threads, but that is not an issue
            cachedStack = ret = toStack();
        }
        
        return ret;
    }
}
