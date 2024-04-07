package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common fluid variant attributes, accessible both client-side and server-side.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public final class FluidVariantAttributes {
    private static final ApiProviderMap<Fluid, FluidVariantAttributeHandler> HANDLERS = ApiProviderMap.create();
    private static final FluidVariantAttributeHandler DEFAULT_HANDLER = new FluidVariantAttributeHandler() {};
    private static volatile boolean coloredVanillaFluidNames = false;
    private FluidVariantAttributes() {
    }
    /**
     * Register an attribute handler for the passed fluid.
     */
    public static void register(Fluid fluid, FluidVariantAttributeHandler handler) {
        if (HANDLERS.putIfAbsent(fluid, handler) != null) {
            throw new IllegalArgumentException("Duplicate handler registration for fluid " + fluid);
        }
    }
    /**
     * Enable blue- and red-colored names for water and lava respectively.
     */
    public static void enableColoredVanillaFluidNames() {
        coloredVanillaFluidNames = true;
    }
    /**
     * Return the attribute handler for the passed fluid, if available, and {@code null} otherwise.
     */
    public static @Nullable FluidVariantAttributeHandler getHandler(Fluid fluid) {
        return HANDLERS.get(fluid);
    }
    /**
     * Return the attribute handler for the passed fluid, if available, or the default instance otherwise.
     */
    public static FluidVariantAttributeHandler getHandlerOrDefault(Fluid fluid) {
        FluidVariantAttributeHandler handler = HANDLERS.get(fluid);
        return handler == null ? DEFAULT_HANDLER : handler;
    }
    /**
     * Return the name that should be used for the passed fluid variant.
     */
    public static Text getName(@NotNull FluidVariant variant) {
        return getHandlerOrDefault(variant.getFluid()).getName(variant);
    }
    /**
     * Return the sound corresponding to a container of this fluid variant being filled if available,
     * or the default (water) filling sound otherwise.
     */
    public static SoundEvent getFillSound(@NotNull FluidVariant variant) {
        return getHandlerOrDefault(variant.getFluid()).getFillSound(variant)
          .or(() -> variant.getFluid().getBucketFillSound())
          .orElse(SoundEvents.ITEM_BUCKET_FILL);
    }
    /**
     * Return the sound corresponding to a container of this fluid variant being emptied if available,
     * or the default (water) emptying sound otherwise.
     */
    public static SoundEvent getEmptySound(@NotNull FluidVariant variant) {
        return getHandlerOrDefault(variant.getFluid()).getEmptySound(variant).orElse(SoundEvents.ITEM_BUCKET_EMPTY);
    }
    /**
     * Return an integer in [0, 15]: the light level emitted by this fluid variant, or 0 if it doesn't naturally emit light.
     */
    public static int getLuminance(@NotNull FluidVariant variant) {
        int luminance = getHandlerOrDefault(variant.getFluid()).getLuminance(variant);
        
        if (luminance < 0 || luminance > 15) {
            TransferApiImpl.LOGGER.warn("Broken FluidVariantAttributeHandler. Invalid luminance %d for fluid variant %s".formatted(luminance, variant));
            return DEFAULT_HANDLER.getLuminance(variant);
        }
        
        return luminance;
    }
    /**
     * Return a non-negative integer, representing the temperature of this fluid in Kelvin.
     * The reference values are {@value FluidConstants#WATER_TEMPERATURE} for water, and {@value FluidConstants#LAVA_TEMPERATURE} for lava.
     */
    public static int getTemperature(@NotNull FluidVariant variant) {
        int temperature = getHandlerOrDefault(variant.getFluid()).getTemperature(variant);
        
        if (temperature < 0) {
            TransferApiImpl.LOGGER.warn("Broken FluidVariantAttributeHandler. Invalid temperature %d for fluid variant %s".formatted(temperature, variant));
            return DEFAULT_HANDLER.getTemperature(variant);
        }
        
        return temperature;
    }
    /**
     * Return a positive integer, representing the viscosity of this fluid variant.
     * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
     *
     * <p>More precisely, viscosity should be {@value FluidConstants#VISCOSITY_RATIO} * {@link FlowableFluid#getFlowSpeed} for flowable fluids.
     * The reference values are {@value FluidConstants#WATER_VISCOSITY} for water,
     * {@value FluidConstants#LAVA_VISCOSITY_NETHER} for lava in ultrawarm dimensions (such as the nether),
     * and {@value FluidConstants#LAVA_VISCOSITY} for lava in other dimensions.
     *
     * @param world World if available, otherwise null.
     */
    public static int getViscosity(@NotNull FluidVariant variant, @Nullable World world) {
        int viscosity = getHandlerOrDefault(variant.getFluid()).getViscosity(variant, world);
        
        if (viscosity <= 0) {
            TransferApiImpl.LOGGER.warn("Broken FluidVariantAttributeHandler. Invalid viscosity %d for fluid variant %s".formatted(viscosity, variant));
            return DEFAULT_HANDLER.getViscosity(variant, world);
        }
        
        return viscosity;
    }
    /**
     * Return true if this fluid is lighter than air.
     * Fluids that are lighter than air generally flow upwards.
     */
    public static boolean isLighterThanAir(@NotNull FluidVariant variant) {
        return getHandlerOrDefault(variant.getFluid()).isLighterThanAir(variant);
    }
}
