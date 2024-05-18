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
    private static final Map<Player, Integer> remainingTargets = new HashMap<>();
    private static final Map<Player, Scoreboard> playerScoreboards = new HashMap<>();
    private static final Map<Player, BukkitTask> playerTimers = new HashMap<>();

    public shootGame(@NotNull String name) {
        super(name);
    }
    public static NamespacedKey Arrow_Type = new NamespacedKey("arrow", "type");

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("플레이어만 해당 명령어 사용 가능");
            return true;
        }

        ItemStack bowStack = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bowStack.getItemMeta();
        bowMeta.setDisplayName("미니게임용 활");
        bowStack.setItemMeta(bowMeta);

        ItemStack arrowType1 = new ItemStack(Material.ARROW, 30);
        ItemMeta arrowMeta1 = arrowType1.getItemMeta();
        arrowMeta1.setDisplayName("1번 화살");
        arrowMeta1.setLore(Arrays.asList("맞춘 과녁 제거", "20칸 이상에서만 작동"));
        PersistentDataContainer data1 = arrowMeta1.getPersistentDataContainer();
        data1.set(Arrow_Type, PersistentDataType.STRING, "1번 화살");
        arrowType1.setItemMeta(arrowMeta1);


        ItemStack arrowType2 = new ItemStack(Material.ARROW, 20);
        ItemMeta arrowMeta2 = arrowType2.getItemMeta();
        arrowMeta2.setDisplayName("2번 화살");
        arrowMeta2.setLore(Arrays.asList("맞춘 과녁과 양옆 1칸 과녁 제거", "20칸 이상에서만 작동"));
        PersistentDataContainer data2 = arrowMeta2.getPersistentDataContainer();
        data2.set(Arrow_Type, PersistentDataType.STRING, "2번 화살");

        arrowType2.setItemMeta(arrowMeta2);

        ItemStack arrowType3 = new ItemStack(Material.ARROW, 10);
        ItemMeta arrowMeta3 = arrowType3.getItemMeta();
        arrowMeta3.setDisplayName("3번 화살");
        arrowMeta3.setLore(Arrays.asList("맞춘 과녁과 위아래 1칸 과녁 제거", "20칸 이상에서만 작동"));
        PersistentDataContainer data3 = arrowMeta3.getPersistentDataContainer();
        data3.set(Arrow_Type, PersistentDataType.STRING, "3번 화살");
        arrowType3.setItemMeta(arrowMeta3);


        player.getInventory().addItem(bowStack, arrowType1, arrowType2, arrowType3);
        createTarget(player);
        createScoreBoard(player);
        gameStartTime.put(player, System.currentTimeMillis());
        remainingTargets.put(player, 49);
        startTimer(player);

        player.sendMessage("과녁 생성, 미니게임용 활과 화살 지급");
        return true;
    }
    private void startTimer(Player player) {
        @NotNull Plugin plugin = null;
        BukkitTask timer = Bukkit.getScheduler().runTaskTimer(null, () -> {
            long startTime = gameStartTime.get(player);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime - startTime) / 1000;

            Scoreboard board = playerScoreboards.get(player);
            Objective objective = board.getObjective(ChatColor.RED + "화살게임");
            if(objective != null) {
                Score timeTaken = objective.getScore(ChatColor.YELLOW + "이상 시간: ");
                timeTaken.setScore((int) elapsedTime);
            }
        },0L, 20L);
        playerTimers.put(player, timer);
    }

    public static void createTarget(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        Location targetLocation = new Location(world, location.getX() + 20, location.getY(), location.getZ());

        for (int z = 0; z < 7; z++) {
            Location shooterLocation = location.clone().add(0, 0, z);
            world.getBlockAt(shooterLocation).setType(Material.STONE);
            for (int y = 0; y < 7; y++) {
                Location blockLocation = targetLocation.clone().add(0, y, z);
                world.getBlockAt(blockLocation).setType(Material.TARGET);
            }
        }
    }

    public void createScoreBoard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective(ChatColor.RED + "화살게임", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.RED + "화살게임");

        Score remainTarget = objective.getScore(ChatColor.YELLOW + "남은 과녁: ");
        remainTarget.setScore(49);

        Score time = objective.getScore(ChatColor.YELLOW + "걸린 시간: ");
        time.setScore(0);

        player.setScoreboard(board);
        playerScoreboards.put(player, board);

        updateArrowsLeft(player);
    }
    public static void updateArrowsLeft(Player player) {
        Scoreboard board = playerScoreboards.get(player);
        Objective objective = board.getObjective(ChatColor.RED + "화살게임");

        int arrows1 = getArrowCount(player, "1번 화살");
        int arrows2 = getArrowCount(player, "2번 화살");
        int arrows3 = getArrowCount(player, "3번 화살");

        Score arrowsLeft1 = objective.getScore(ChatColor.YELLOW + "1번 화살: ");
        arrowsLeft1.setScore(arrows1);
        Score arrowsLeft2 = objective.getScore(ChatColor.YELLOW + "2번 화살: ");
        arrowsLeft2.setScore(arrows2);
        Score arrowsLeft3 = objective.getScore(ChatColor.YELLOW + "3번 화살: ");
        arrowsLeft3.setScore(arrows3);
    }
    private static int getArrowCount(Player player, String arrowType) {
        int count = 0;

        for(ItemStack item : player.getInventory()) {
            if(item != null && item.getType() == Material.ARROW) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                String type = data.get(Arrow_Type, PersistentDataType.STRING);
                if(arrowType.equals(type)) {
                    count += item.getAmount();
                }
            }
        } return count;
    }
    public static void targetHit(Player player, int brokenBlocks) {
        int remaining = remainingTargets.get(player) - brokenBlocks;
        remainingTargets.put(player, remaining);

        Scoreboard board = playerScoreboards.get(player);
        Objective objective = board.getObjective(ChatColor.RED + "화살게임");
        Score targetLeft = objective.getScore(ChatColor.YELLOW + "남은 과녁: ");
        targetLeft.setScore(remaining);

        if(remaining <= 0) {
            endGame(player);
        }
    }
    private static void endGame(Player player) {
        long endTime = System.currentTimeMillis();
        long startTime = gameStartTime.get(player);
        long timeTaken = (endTime - startTime) / 1000;

        player.sendMessage("미니게임 종료");
        player.sendMessage("과녁 제거 시간: " + timeTaken + "초");
        player.sendMessage("1번 화살 남은 개수: " + getArrowCount(player, "1번 화살"));
        player.sendMessage("2번 화살 남은 개수: " + getArrowCount(player, "2번 화살"));
        player.sendMessage("3번 화살 남은 개수: " + getArrowCount(player, "3번 화살"));

        gameStartTime.remove(player);
        remainingTargets.remove(player);
        playerScoreboards.remove(player);
        player.getInventory().clear();
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
