package net.fabricmc.fabric.api.transfer.v1.client.fluid;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Client-side display of fluid variants.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public final class FluidVariantRendering {
    private static final ApiProviderMap<Fluid, FluidVariantRenderHandler> HANDLERS = ApiProviderMap.create();
    private static final FluidVariantRenderHandler DEFAULT_HANDLER = new FluidVariantRenderHandler() {};
    private FluidVariantRendering() {
    }
    /**
     * Register a render handler for the passed fluid.
     */
    public static void register(@NotNull Fluid fluid, @NotNull FluidVariantRenderHandler handler) {
        if (HANDLERS.putIfAbsent(fluid, handler) != null) {
            throw new IllegalArgumentException("Duplicate handler registration for fluid " + fluid);
        }
    }
    /**
     * Return the render handler for the passed fluid, if available, and {@code null} otherwise.
     */
    public static @Nullable FluidVariantRenderHandler getHandler(@NotNull Fluid fluid) {
        return HANDLERS.get(fluid);
    }
    /**
     * Return the render handler for the passed fluid, if available, or the default instance otherwise.
     */
    public static FluidVariantRenderHandler getHandlerOrDefault(@NotNull Fluid fluid) {
        FluidVariantRenderHandler handler = HANDLERS.get(fluid);
        return handler == null ? DEFAULT_HANDLER : handler;
    }
    /**
     * Return a mutable list: the tooltip for the passed fluid variant, including the name and additional lines if available
     * and the id of the fluid if advanced tooltips are enabled.
     *
     * <p>Compared to {@linkplain #getTooltip(FluidVariant, TooltipContext) the other overload}, the current tooltip context is automatically used.
     */
    public static List<Text> getTooltip(@NotNull FluidVariant fluidVariant) {
        return getTooltip(fluidVariant, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC);
    }
    /**
     * Return a mutable list: the tooltip for the passed fluid variant, including the name and additional lines if available
     * and the id of the fluid if advanced tooltips are enabled.
     */
    public static List<Text> getTooltip(@NotNull FluidVariant fluidVariant, @NotNull TooltipContext context) {
        List<Text> tooltip = new ArrayList<>();
        
        // Name first
        tooltip.add(FluidVariantAttributes.getName(fluidVariant));
        
        // Additional tooltip information
        getHandlerOrDefault(fluidVariant.getFluid()).appendTooltip(fluidVariant, tooltip, context);
        
        // If advanced tooltips are enabled, render the fluid id
        if (context.isAdvanced()) {
            tooltip.add(Text.literal(Registries.FLUID.getId(fluidVariant.getFluid()).toString()).formatted(Formatting.DARK_GRAY));
        }
        
        // TODO: consider adding an event to append to tooltips?
        
        return tooltip;
    }
    /**
     * Return the still and the flowing sprite that should be used to render the passed fluid variant, or null if they are not available.
     * The sprites should be rendered using the color returned by {@link #getColor}.
     *
     * @see FluidVariantRenderHandler#getSprites
     */
    public static @NotNull Sprite @Nullable [] getSprites(@NotNull FluidVariant fluidVariant) {
        return getHandlerOrDefault(fluidVariant.getFluid()).getSprites(fluidVariant);
    }
    /**
     * Return the still sprite that should be used to render the passed fluid variant, or null if it's not available.
     * The sprite should be rendered using the color returned by {@link #getColor}.
     */
    public static @Nullable Sprite getSprite(FluidVariant fluidVariant) {
        Sprite[] sprites = getSprites(fluidVariant);
        return sprites != null ? Objects.requireNonNull(sprites[0]) : null;
    }
    /**
     * Return the position-independent color that should be used to render {@linkplain #getSprite the sprite} of the passed fluid variant.
     */
    public static int getColor(@NotNull FluidVariant fluidVariant) {
        return getColor(fluidVariant, null, null);
    }
    /**
     * Return the color that should be used when rendering {@linkplain #getSprite the sprite} of the passed fluid variant.
     *
     * <p>If the world and the position parameters are null, a position-independent color is returned.
     * If the world and position parameters are not null, the color may depend on the position.
     * For example, if world and position are passed, water will use them to return a biome-dependent color.
     */
    public static int getColor(@NotNull FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos) {
        return getHandlerOrDefault(fluidVariant.getFluid()).getColor(fluidVariant, view, pos);
    }
}
