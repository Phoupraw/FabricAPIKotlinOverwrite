package net.fabricmc.fabric.api.transfer.v1.client.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Defines how {@linkplain FluidVariant fluid variants} of a given Fluid should be displayed to clients.
 * Register with {@link FluidVariantRendering#register}.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface FluidVariantRenderHandler {
    /**
     * Append additional tooltips to the passed list if additional information is contained in the fluid variant.
     *
     * <p>The name of the fluid, and its identifier if the tooltip context is advanced, should not be appended.
     * They are already added by {@link FluidVariantRendering#getTooltip}.
     */
    default void appendTooltip(@NotNull FluidVariant fluidVariant, @NotNull List<@NotNull Text> tooltip, @NotNull TooltipContext tooltipContext) {
    }
    /**
     * Return an array of size at least 2 containing the sprites that should be used to render the passed fluid variant,
     * for use in baked models, (block) entity renderers, or user interfaces.
     * The first sprite in the array is the still sprite, and the second is the flowing sprite.
     *
     * <p>Null may be returned if the fluid variant should not be rendered, but if an array is returned it must have at least two entries and
     * they may not be null.
     */
    default @NotNull Sprite @Nullable [] getSprites(@NotNull FluidVariant fluidVariant) {
        // Use the fluid render handler by default.
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidVariant.getFluid());
        
        if (fluidRenderHandler != null) {
            return fluidRenderHandler.getFluidSprites(null, null, fluidVariant.getFluid().getDefaultState());
        } else {
            return null;
        }
    }
    /**
     * Return the color to use when rendering {@linkplain #getSprites the sprites} of this fluid variant.
     * Transparency (alpha) will generally be taken into account and should be specified as well.
     *
     * <p>The world and position are optional context parameters and may be {@code null}.
     * If they are null, this method must return a location-independent color.
     * If they are provided, this method may return a color that depends on the location.
     * For example, water returns the biome-dependent color if the context parameters are specified, or its default color if one of them is null.
     */
    default int getColor(@NotNull FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos) {
        // Use the fluid render handler by default.
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidVariant.getFluid());
        
        if (fluidRenderHandler != null) {
            return fluidRenderHandler.getFluidColor(view, pos, fluidVariant.getFluid().getDefaultState()) | 255 << 24;
        } else {
            return -1;
        }
    }
}
