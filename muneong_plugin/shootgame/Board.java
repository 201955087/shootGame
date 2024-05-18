package me.air_bottle.muneong_plugin.shootgame;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public interface Board {
    void createScoreBoard(Player player);
    void updateArrowsLeft(Player player);
    void getArrowCount(Player player, String name);
    void targetHit(Player player, int brokenBlocks);
    void endGame(Player player);

    Scoreboard getNewScoreboard();
}
