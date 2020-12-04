/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2020 Meteor Development.
 */

package minegame159.meteorclient.modules.misc;

import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import minegame159.meteorclient.Config;
import minegame159.meteorclient.events.SendMessageEvent;
import minegame159.meteorclient.modules.Category;
import minegame159.meteorclient.modules.ToggleModule;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.settings.StringSetting;
import minegame159.meteorclient.utils.Utils;

public class Suffix extends ToggleModule {
    private static final Char2CharMap SMALL_CAPS = new Char2CharArrayMap();

    static {
        String[] a = "abcdefghijklmnopqrstuvwxyz".split("");
        String[] b = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩqʀꜱᴛᴜᴠᴡxyᴢ".split("");
        for (int i = 0; i < a.length; i++) SMALL_CAPS.put(a[i].charAt(0), b[i].charAt(0));
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> text = sgGeneral.add(new StringSetting.Builder()
            .name("text")
            .description("Text to add.")
            .defaultValue(" meteor on crack")
            .build()
    );

    private final Setting<Boolean> smallCaps = sgGeneral.add(new BoolSetting.Builder()
            .name("small-caps")
            .description("Uses the small caps Unicode font.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> random = sgGeneral.add(new BoolSetting.Builder()
            .name("random")
            .description("Example: <msg> (538)")
            .defaultValue(false)
            .build()
    );

    private final StringBuilder sb = new StringBuilder();

    public Suffix() {
        super(Category.Misc, "suffix", "Adds a suffix after every message you send.");
    }

    @EventHandler
    private final Listener<SendMessageEvent> onSendMessage = new Listener<>(event -> {
        if (!event.msg.startsWith(Config.INSTANCE.getPrefix() + "b")) {
            event.msg += getSuffix();
        }
    });

    private String getSuffix() {
        String text;

        if (random.get()) {
            text = String.format(" (%03d)", Utils.random(0, 1000));
        } else {
            text = this.text.get();

            if (smallCaps.get()) {
                sb.setLength(0);

                for (char ch : text.toCharArray()) {
                    if (SMALL_CAPS.containsKey(ch)) sb.append(SMALL_CAPS.get(ch));
                    else sb.append(ch);
                }

                text = sb.toString();
            }
        }

        return text;
    }
}
