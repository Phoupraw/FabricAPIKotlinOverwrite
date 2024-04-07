package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.fluid.CombinedProvidersImpl;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public final class FluidStorage {
    public static final BlockApiLookup<Storage<FluidVariant>, @Nullable Direction> SIDED = BlockApiLookup.get(new Identifier("fabric:sided_fluid_storage"), Storage.asClass(), Direction.class);
    public static final ItemApiLookup<Storage<FluidVariant>, ContainerItemContext> ITEM = ItemApiLookup.get(new Identifier("fabric:fluid_storage"), Storage.asClass(), ContainerItemContext.class);
    public static Event<CombinedItemApiProvider> combinedItemApiProvider(Item item) {
        return CombinedProvidersImpl.getOrCreateItemEvent(item);
    }
    public static final Event<CombinedItemApiProvider> GENERAL_COMBINED_PROVIDER = CombinedProvidersImpl.createEvent(false);
    
    @FunctionalInterface
    public interface CombinedItemApiProvider {
        /**
         * Return a {@code Storage<FluidVariant>} if available in the given context, or {@code null} otherwise.
         * The current item variant can be {@linkplain ContainerItemContext#getItemVariant() retrieved from the context}.
         */
        @Nullable
        Storage<FluidVariant> find(ContainerItemContext context);
    }
    private FluidStorage() {
    }
}
