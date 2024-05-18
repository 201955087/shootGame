package me.air_bottle.muneong_plugin.shootgame;

import org.bukkit.entity.Player;

public interface TargetManager {

    void createTarget(Player player);
    void targetHit(Player player, int brokenBlocks);

}
