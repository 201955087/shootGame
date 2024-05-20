package me.air_bottle.muneong_plugin.shootgame;

import org.bukkit.entity.Player;

public interface GameBuilder {

    void createItems(Player player);
    void createTarget(Player player);
    void targetHit(Player player, int brokenBlocks);
    void endGame(Player player);
    int getArrowCount(Player player, String name);
}
