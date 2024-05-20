package me.air_bottle.muneong_plugin.shootgame;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import static me.air_bottle.muneong_plugin.shootgame.shootGame.*;

public class ScoreChangeManager implements ScoreChangeBuilder{
    @Override
    public void createScoreBoard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective(ChatColor.RED + "화살게임", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.RED + "화살게임");
        // 새로운 스코어보드 생성
        // 상위제목을 화살게임으로 설정

        Score remainTarget = objective.getScore(ChatColor.YELLOW + "남은 과녁: ");
        remainTarget.setScore(49);
        // 남은 과녁 항목과 해당 항목의 초기값을 49로 초기화

        player.setScoreboard(board);
        playerScoreboards.put(player, board);
        // playerScoreboards hashmap의 항목 가져오기
    }

}
