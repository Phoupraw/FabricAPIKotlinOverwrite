package net.fabricmc.fabric.api.transfer.v1.storage.base;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public record ResourceAmount<T>(T resource, long amount) {
}
