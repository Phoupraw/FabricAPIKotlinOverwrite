package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Defines the common attributes of {@linkplain FluidVariant fluid variants} of a given Fluid.
 * Register with {@link FluidVariantAttributes#register}.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface FluidVariantAttributeHandler {
    /**
     * Return the name that should be used for the passed fluid variant.
     */
    default @NotNull Text getName(@NotNull FluidVariant fluidVariant) {
        Block fluidBlock = fluidVariant.getFluid().getDefaultState().getBlockState().getBlock();
        
        if (!fluidVariant.isBlank() && fluidBlock == Blocks.AIR) {
            // Some non-placeable fluids use air as their fluid block, in that case infer translation key from the fluid id.
            return Text.translatable(Util.createTranslationKey("block", Registries.FLUID.getId(fluidVariant.getFluid())));
        } else {
            return fluidBlock.getName();
        }
    }
    /**
     * Return the sound corresponding to this fluid being filled, or none if no sound is available.
     *
     * <p>If a non-empty sound event is returned, {@link Fluid#getBucketFillSound} will return that sound.
     */
    default Optional<SoundEvent> getFillSound(@NotNull FluidVariant variant) {
        return Optional.empty();
    }
    /**
     * Return the sound corresponding to this fluid being emptied, or none if no sound is available.
     *
     * <p>If a non-empty sound event is returned, {@link BucketItem#playEmptyingSound} will play that sound.
     */
    default Optional<SoundEvent> getEmptySound(@NotNull FluidVariant variant) {
        return Optional.empty();
    }
    /**
     * Return an integer in [0, 15]: the light level emitted by this fluid, or 0 if it doesn't naturally emit light.
     */
    default int getLuminance(@NotNull FluidVariant variant) {
        return variant.getFluid().getDefaultState().getBlockState().getLuminance();
    }
    /**
     * Return a non-negative integer, representing the temperature of this fluid in Kelvin.
     * The reference values are {@value FluidConstants#WATER_TEMPERATURE} for water, and {@value FluidConstants#LAVA_TEMPERATURE} for lava.
     */
    default int getTemperature(@NotNull FluidVariant variant) {
        return FluidConstants.WATER_TEMPERATURE;
    }
    /**
     * Return a positive integer, representing the viscosity of this fluid.
     * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
     *
     * <p>More precisely, viscosity should be {@value FluidConstants#VISCOSITY_RATIO} * {@link FlowableFluid#getFlowSpeed} for flowable fluids.
     * The reference values are {@value FluidConstants#WATER_VISCOSITY} for water,
     * {@value FluidConstants#LAVA_VISCOSITY_NETHER} for lava in ultrawarm dimensions (such as the nether),
     * and {@value FluidConstants#LAVA_VISCOSITY} for lava in other dimensions.
     *
     * @param world World if available, otherwise null.
     */
    default int getViscosity(@NotNull FluidVariant variant, @Nullable World world) {
        return FluidConstants.WATER_VISCOSITY;
    }
    /**
     * Return true if this fluid is lighter than air.
     * Fluids that are lighter than air generally flow upwards.
     */
    default boolean isLighterThanAir(@NotNull FluidVariant variant) {
        return false;
    }
}
