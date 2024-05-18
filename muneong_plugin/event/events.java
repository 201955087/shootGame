package me.air_bottle.muneong_plugin.event;

import me.air_bottle.muneong_plugin.shootgame.shootGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.net.http.WebSocket;

import static me.air_bottle.muneong_plugin.shootgame.shootGame.Arrow_Type;

public class events implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onShoot(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }
        if(!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        Block hitBlock = event.getHitBlock();
        ItemStack itemInHand = player.getInventory().getItemInOffHand();
        ItemMeta meta = itemInHand.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String type = data.getOrDefault(Arrow_Type, PersistentDataType.STRING, "-1");

        Vector arrowLocation = arrow.getLocation().toVector();
        Vector playerLocation = player.getLocation().toVector();
        double distance = playerLocation.distance(arrowLocation);

        if(distance < 20) {
            return;
        }
        if(hitBlock == null || hitBlock.getType() != Material.TARGET) {
            player.sendMessage("과녁에 화살이 맞지 않았음ㅋ");
            return;
        }

        if(hitBlock.getType() == Material.TARGET) {
            int brokenBlocks = 0;
            switch (type) {
                case "1번 화살":
                    hitBlock.breakNaturally();
                    event.getEntity().remove();
                    brokenBlocks = 1;
                    break;
                case "2번 화살":
                    hitBlock.breakNaturally();
                    hitBlock.getRelative(0, 0, 1).breakNaturally();
                    hitBlock.getRelative(0, 0, -1).breakNaturally();
                    event.getEntity().remove();
                    brokenBlocks = 3;
                    break;
                case "3번 화살":
                    hitBlock.breakNaturally();
                    hitBlock.getRelative(0, 1, 0).breakNaturally();
                    hitBlock.getRelative(0, -1, 0).breakNaturally();
                    event.getEntity().remove();
                    brokenBlocks = 3;
                    break;
            }
            shootGame.targetHit(player, brokenBlocks);
        }
    }
}
