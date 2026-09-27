package com.betterwhips.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/** Adapts the 1.21 item-source enchantment hooks to their 1.20.1 equivalents. */
final class WhipEnchantments {
    private WhipEnchantments() {}
    static float modifyDamage(ServerLevel level, ItemStack weapon, LivingEntity target, DamageSource source, float base) {
        return base + EnchantmentHelper.getDamageBonus(weapon, target.getMobType());
    }
    static float modifyKnockback(ServerLevel level, ItemStack weapon, LivingEntity target, DamageSource source, float base) {
        return base + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, weapon);
    }
    static void doPostAttackEffects(ServerLevel level, LivingEntity target, DamageSource source, ItemStack weapon) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            EnchantmentHelper.doPostDamageEffects(attacker, target);
        }
    }
}
