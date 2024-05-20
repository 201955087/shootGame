package me.air_bottle.muneong_plugin.shootgame;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class shootGame extends BukkitCommand {
    public static NamespacedKey Arrow_Type = new NamespacedKey("arrow", "type");
    protected static final Map<Player, Long> gameStartTime = new HashMap<>();
    // 미니게임을 플레이하는 플레이어의 시간을 저장할 hashmap 생성
    protected static final Map<Player, Integer> remainingTargets = new HashMap<>();
    // 미니게임을 플레이하는 플레이어의 게임 내의 남은 타겟블록 수를 저장할 hashmap 생성
    protected static final Map<Player, Scoreboard> playerScoreboards = new HashMap<>();
    // 미니게임을 플레이하는 플레이어의 스코어보드 정보를 받아오는 hashmap 생성
    // 해당 hashmap을 업데이트 시 별도의 선언과정 없이 스코어보드에 데이터 항목 추가 가능

    public shootGame(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("플레이어만 해당 명령어 사용 가능");
            return true;
            //플레이어만 명령어 사용 가능하게 설정
        }
        if(strings.length == 0) {
            return false;
        }

        GameManager gameManager = new GameManager();
        ScoreChangeManager scoreChangeManager = new ScoreChangeManager();
        // 인터페이스로 선언한 GameManager, ScoreChangeManager를 받아오는 인스턴스


        if(strings[0].equals("start")){
            gameManager.createItems(player);
            // 아이템 지급 메소드
            gameManager.createTarget(player);
            // 과녁 생성 메소드
            scoreChangeManager.createScoreBoard(player);
            // 스코어보드 생성 메소드
            gameStartTime.put(player, System.currentTimeMillis());
            // 미니게임을 실행한 플레이어의 실행 시간 측정 시작, 해당 hashmap에 값 저장
            remainingTargets.put(player, 49);
            // 블럭 칸을 세는 hashmap에 49개의 블럭이 있다 선언

            return true;
        } else if(strings[0].equals("stop")) {
            player.sendMessage(ChatColor.GOLD + "조기 종료");
            gameManager.endGame(player);
            player.sendMessage(ChatColor.RED + "모든 과녁 제거 실패");
            return true;
        }
        return false;
    }

}
