/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.minecraft.entity.effect.StatusEffectInstanceAccessor;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StatusEffectAmplifierMapSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class PotionSpoof extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Object2IntMap<StatusEffect>> potions = sgGeneral.add(new StatusEffectAmplifierMapSetting.Builder()
            .name("potions")
            .description("Potions to add.")
            .defaultValue(Utils.createStatusEffectMap())
            .build()
    );

    public PotionSpoof() {
        super(Categories.Player, "potion-spoof", "Spoofs specified potion effects for you. SOME effects DO NOT work.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for (StatusEffect statusEffect : potions.get().keySet()) {
            int level = potions.get().getInt(statusEffect);
            if (level <= 0) continue;

            if (mc.player.hasStatusEffect(statusEffect)) {
                StatusEffectInstance instance = mc.player.getStatusEffect(statusEffect);
                ((StatusEffectInstanceAccessor) instance).setAmplifier(level - 1);
                if (instance.getDuration() < 20) ((StatusEffectInstanceAccessor) instance).setDuration(20);
            } else {
                mc.player.addStatusEffect(new StatusEffectInstance(statusEffect, 20, level - 1));
            }
        }
    }
}
