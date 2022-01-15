package me.vaimok.nekoplus.client.modules.misc;

import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class BurrowCounter extends Module {

    private final ConcurrentHashMap<EntityPlayer, Integer> players = new ConcurrentHashMap<>();
    List<EntityPlayer> anti_spam = new ArrayList<>();
    private boolean flag;

    public BurrowCounter() {
        super("BurrowCounter", "counts burrows", Category.MISC, true, false, false);
    }

// author Trambled very hot femboy <3 minor edits made by vaimok


    @Override
    public void onEnable() {
        players.clear();
        anti_spam.clear();
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            BlockPos position = new BlockPos(player.posX, player.posY + 0.2D, player.posZ);
            if (mc.world.getBlockState(position).getBlock().equals(Blocks.OBSIDIAN) && !flag) {
                if (anti_spam.contains(player)) continue;
                add_player(player);
                anti_spam.add(player);
            } else if (!mc.world.getBlockState(position).getBlock().equals(Blocks.OBSIDIAN)) {
                anti_spam.remove(player);
            }
        }
    }

    private void add_player(EntityPlayer player) {
        if (player == null) return;
        if (players.containsKey(player)) {
            int value = players.get(player) + 1;
            players.put(player, value);
            Command.sendMessage (player.getName() + TextUtil.DARK_RED + " has burrowed " + value + " times");
        } else {
            players.put(player, 1);
            Command.sendMessage (player.getName() + TextUtil.DARK_RED + " has burrowed");
        }
    }
}