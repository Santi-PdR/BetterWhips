package com.betterwhips.util;

import net.minecraft.resources.ResourceLocation;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Stable 1.20.1 UUID bridge for resource-location attribute modifier identifiers. */
public final class WhipAttributeIds {
    private WhipAttributeIds() {}
    public static UUID uuid(ResourceLocation id) {
        return UUID.nameUUIDFromBytes(id.toString().getBytes(StandardCharsets.UTF_8));
    }
}
