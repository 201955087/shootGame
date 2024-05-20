package me.air_bottle.muneong_plugin.shootgame;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class shootGame extends BukkitCommand {
    private static final Map<Player, Long> gameStartTime = new HashMap<>();
    // 미니게임을 플레이하는 플레이어의 시간을 저장할 hashmap 생성
    private static final Map<Player, Integer> remainingTargets = new HashMap<>();
    // 미니게임을 플레이하는 플레이어의 게임 내의 남은 타겟블록 수를 저장할 hashmap 생성
    private static final Map<Player, Scoreboard> playerScoreboards = new HashMap<>();
    // 미니게임을 플레이하는 플레이어의 스코어보드 정보를 받아오는 hashmap 생성
    // 해당 hashmap을 업데이트 시 별도의 선언과정 없이 스코어보드에 데이터 항목 추가 가능

    public shootGame(@NotNull String name) {
        super(name);
    }
    public static NamespacedKey Arrow_Type = new NamespacedKey("arrow", "type");
    // type 키값(arrow)을 반환하는 Arrow_Type 이라는 이름의 네임스페이스드키 생성

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("플레이어만 해당 명령어 사용 가능");
            return true;
            //플레이어만 명령어 사용 가능하게 설정
        }

        ItemStack bowStack = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bowStack.getItemMeta();
        bowMeta.setDisplayName("미니게임용 활");
        bowStack.setItemMeta(bowMeta);
        // 활 아이템에 "미니게임용 활" 이름을 새기고 새로운 아이템으로 선언

        ItemStack arrowType1 = new ItemStack(Material.ARROW, 30);
        ItemMeta arrowMeta1 = arrowType1.getItemMeta();
        arrowMeta1.setDisplayName("1번 화살");
        arrowMeta1.setLore(Arrays.asList("맞춘 과녁 제거", "20칸 이상에서만 작동"));
        PersistentDataContainer data1 = arrowMeta1.getPersistentDataContainer();
        data1.set(Arrow_Type, PersistentDataType.STRING, "1번 화살");
        arrowType1.setItemMeta(arrowMeta1);
        // 화살 30개의 ItemStack에 "1번 화살" 이름에 위의 로어를 새기는 새로운 아이템으로 선언
        // 해당 1번 화살을 "1번 화살"이라는 type 값을 반환하게 설정

        ItemStack arrowType2 = new ItemStack(Material.ARROW, 20);
        ItemMeta arrowMeta2 = arrowType2.getItemMeta();
        arrowMeta2.setDisplayName("2번 화살");
        arrowMeta2.setLore(Arrays.asList("맞춘 과녁과 양옆 1칸 과녁 제거", "20칸 이상에서만 작동"));
        PersistentDataContainer data2 = arrowMeta2.getPersistentDataContainer();
        data2.set(Arrow_Type, PersistentDataType.STRING, "2번 화살");
        arrowType2.setItemMeta(arrowMeta2);
        // 1번 화살과 동일하게 선언
        // "2번 화살" 20개

        ItemStack arrowType3 = new ItemStack(Material.ARROW, 10);
        ItemMeta arrowMeta3 = arrowType3.getItemMeta();
        arrowMeta3.setDisplayName("3번 화살");
        arrowMeta3.setLore(Arrays.asList("맞춘 과녁과 위아래 1칸 과녁 제거", "20칸 이상에서만 작동"));
        PersistentDataContainer data3 = arrowMeta3.getPersistentDataContainer();
        data3.set(Arrow_Type, PersistentDataType.STRING, "3번 화살");
        arrowType3.setItemMeta(arrowMeta3);
        // 동일하게 선언
        // "3번 화살" 10개

        player.getInventory().addItem(bowStack, arrowType1, arrowType2, arrowType3);
        // 명령어를 입력한 플레이어에게 선언한 4개의 아이템들을 지급
        createTarget(player);
        // 과녁 생성 메소드
        createScoreBoard(player);
        // 스코어보드 생성 메소드
        gameStartTime.put(player, System.currentTimeMillis());
        // 미니게임을 실행한 플레이어의 실행 시간 측정 시작, 해당 hashmap에 값 저장
        remainingTargets.put(player, 49);
        // 블럭 칸을 세는 hashmap에 49개의 블럭이 있다 선언

        player.sendMessage("과녁 생성, 미니게임용 활과 화살 지급");
        return true;
    }
    public static void createTarget(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        Location targetLocation = new Location(world, location.getX() + 20, location.getY(), location.getZ());
        // 플레이어의 월드를 받고, 플레이어의 위치를 받아오고, 플레이어 위치 기준 x좌표로 +20의 위치를
        // targetLocation으로 선언

        for (int z = 0; z < 7; z++) {
            Location shooterLocation = location.clone().add(0, 0, z);
            world.getBlockAt(shooterLocation).setType(Material.STONE);
            // 플레이어의 위치부터 z좌표로 +7의 위치까지 돌 블럭 생성
            // 20칸의 경계 구분 목적
            for (int y = 0; y < 7; y++) {
                Location blockLocation = targetLocation.clone().add(0, y, z);
                world.getBlockAt(blockLocation).setType(Material.TARGET);
                // targetLocaton에서 y, z 좌표로 7 * 7 의 과녁 블럭으로 이루어진 과녁 생성
            }
        }
    }

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

        updateArrowsLeft(player);
        // 남은 화살개수를 반환하는 메소드 항시 시행
    }
    public static void updateArrowsLeft(Player player) {
        Scoreboard board = playerScoreboards.get(player);
        Objective objective = board.getObjective(ChatColor.RED + "화살게임");
        // playerScoreboards hashmap의 항목을 스코어보드에 업로드

        int arrows1 = getArrowCount(player, "1번 화살");
        int arrows2 = getArrowCount(player, "2번 화살");
        int arrows3 = getArrowCount(player, "3번 화살");
        // 화살 개수를 세는 getArrowCount 메소드로 각각의 화살 타입의 개수 반환, 각 변수에 대입

        Score arrowsLeft1 = objective.getScore(ChatColor.YELLOW + "1번 화살: ");
        arrowsLeft1.setScore(arrows1);
        Score arrowsLeft2 = objective.getScore(ChatColor.YELLOW + "2번 화살: ");
        arrowsLeft2.setScore(arrows2);
        Score arrowsLeft3 = objective.getScore(ChatColor.YELLOW + "3번 화살: ");
        arrowsLeft3.setScore(arrows3);
        // 스코어보드에 해당 각각 "1번 화살", "2번 화살", "3번 화살" 항목을 추가하고 사이드바
        // 값으로 세어놨던 화살 개수 업로드
    }
    private static int getArrowCount(Player player, String arrowType) {
        int count = 0;

        for(ItemStack item : player.getInventory()) {
            if(item != null && item.getType() == Material.ARROW) {
                // 아이템이 플레이어의 인벤에 있고 null 값이 아니고 아이템 타입이 화살이라면
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                String type = data.get(Arrow_Type, PersistentDataType.STRING);
                // 해당 화살의 네임스페이스키 값을 받아오고
                if(arrowType.equals(type)) {
                    count += item.getAmount();
                    // 해당 화살의 개수를 세어 count 변수에 대입
                }
            }
        } return count;
        // 증가된 count의 값을 리턴
    }
    public static void targetHit(Player player, int brokenBlocks) {
        int remaining = remainingTargets.get(player) - brokenBlocks;
        remainingTargets.put(player, remaining);
        // 남은 타겟 블럭 갯수 = 미니게임중인 플레이어의 남은 타겟 블럭 - 부서진 블럭 갯수(events에서 설정)

        Scoreboard board = playerScoreboards.get(player);
        Objective objective = board.getObjective(ChatColor.RED + "화살게임");
        Score targetLeft = objective.getScore(ChatColor.YELLOW + "남은 과녁: ");
        targetLeft.setScore(remaining);
        // 스코어보드의 남은 과녁 항목에 남은 과녁 수 업로드

        if(remaining <= 0) {
            endGame(player);
        }
        // 남은 과녁 블록 개수가 0 이라하면, endGame 메소드 실행
    }
    private static void endGame(Player player) {
        long endTime = System.currentTimeMillis();
        long startTime = gameStartTime.get(player);
        // 끝난 시간은 마지막으로 기록된 시간, 즉 현재시간
        // 시작 시간은 gameStarTime에 처음 기록된 시간
        // 해당 값들을 long 값으로 리턴
        long timeTaken = (endTime - startTime) / 1000;
        // 끝 시간과 시작 시간을 뺀 값에 1000을 나눠 초 단위로 값을 리턴
        // 해당 값이 미니게임이 진행된 시간

        player.sendMessage("미니게임 종료");
        player.sendMessage("과녁 제거 시간: " + timeTaken + "초");
        player.sendMessage("1번 화살 남은 개수: " + getArrowCount(player, "1번 화살"));
        player.sendMessage("2번 화살 남은 개수: " + getArrowCount(player, "2번 화살"));
        player.sendMessage("3번 화살 남은 개수: " + getArrowCount(player, "3번 화살"));
        // 종료 알림과 함께 미니게임이 진행된 시간을 출력
        // 미리 세어놨던 각각의 화살 개수들을 출력

        gameStartTime.remove(player);
        remainingTargets.remove(player);
        playerScoreboards.remove(player);
        // 값이 추가되던 각각의 hashmap 값들 제거(초기화)
        player.getInventory().clear();
        // 플레이어 인벤토리 비우기
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        // 스코어보드 초기값으로 되돌리기
    }
}

// 추가되어야할 조건들
// 조기종료 메소드 - 바로 endgame 실행에 생성된 블럭들 파괴
// 실패 시 뜨는 것들 추가 필요(endgame메소드 내에서 구현 가능?)