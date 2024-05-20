package me.air_bottle.muneong_plugin.event;

import me.air_bottle.muneong_plugin.shootgame.GameManager;
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
            // 이벤트를 발생시킨 projectile이 화살이 아니면 해당 메소드 실행 안함
        }
        if(!(event.getEntity().getShooter() instanceof Player player)) {
            return;
            // projectile을 쏜 개체가 플레이어가 아니면 메소드 실행 안함
        }

        GameManager gameManager = new GameManager();
        // GameManager 인터페이스 구현 메소드들 가져오기

        Block hitBlock = event.getHitBlock();
        // 화살이 때린 블록 받아오기
        ItemStack itemInHand = player.getInventory().getItemInOffHand();
        ItemMeta meta = itemInHand.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String type = data.getOrDefault(Arrow_Type, PersistentDataType.STRING, "-1");
        // 플레이어의 왼손의 아이템의 네임스페이스드키 값을 받아오기
        // 값이 없다면, -1 값을 반환

        Vector arrowLocation = arrow.getLocation().toVector();
        Vector playerLocation = player.getLocation().toVector();
        double distance = playerLocation.distance(arrowLocation);
        // 화살이 꽂힌 위치, 화살을 쏜 플레이어의 위치를 벡터로 받고
        // 둘 사이의 거리를 대충 피타고라스 뭐시기로 계산

        if(distance < 20) {
            player.sendMessage("20칸 미만 위치에서 쐈음, 다시쏘삼");
            event.getEntity().remove();
            return;
            // 두 지점 사이의 거리가 20칸(?) 미만이면 해당 메소드들 실행 안함
        }
        if(hitBlock == null || hitBlock.getType() != Material.TARGET) {
            player.sendMessage("과녁에 화살이 맞지 않았음ㅋ");
            return;
            // 화살이 맞춘 블럭이 존재하지 않거나 해당 블럭 타입이 과녁이 아니라면
            // 허접이라고 놀리면서 추가 메소드 실행 안함
        }
        Block rightHit = hitBlock.getRelative(0, 0, 1);
        Block leftHit = hitBlock.getRelative(0, 0, -1);
        Block upHit = hitBlock.getRelative(0, 1, 0);
        Block downHit = hitBlock.getRelative(0, -1, 0);

        if(hitBlock.getType() == Material.TARGET) {
            // 화살이 맞춘 블럭이 과녁이라면
            int brokenBlocks = 0;
            // 먼저 해당 변수의 값을 0으로 초기화
            switch (type) {
                // 해당 화살의 네임스페이스드 키의 값에 따라
                case "1번 화살":
                    hitBlock.breakNaturally();
                    event.getEntity().remove();
                    brokenBlocks = 1;
                    break;
                    // 값이 "1번 화살" 이면 맞춘 블러 하나만 제거, 바로 화살 삭제
                    // brokenBlocks의 값을 1로 변경
                case "2번 화살":
                    if(rightHit.getType().isAir() && leftHit.getType().isAir()) {
                        brokenBlocks = 1;
                    } else if(rightHit.getType().isAir() || leftHit.getType().isAir()) {
                        brokenBlocks = 2;
                    } else {
                        brokenBlocks = 3;
                    }
                    // 맞힌 위치의 오른쪽과 왼쪽이 공기면 brokenBlocks 1 반환
                    // 둘 중 하나만 공기면 2 반환
                    // 둘다 과녁이면 3반환
                    hitBlock.breakNaturally();
                    rightHit.breakNaturally();
                    leftHit.breakNaturally();
                    event.getEntity().remove();
                    break;
                    // 값이 "2번 화살" 이면 맞춘 블럭과 양옆 블럭 제거, 바로 화살 삭제
                    // breakenBlocks의 값을 3으로 변경
                case "3번 화살":
                    if(upHit.getType().isAir() && downHit.getType().isAir()) {
                        brokenBlocks = 1;
                    } else if(upHit.getType().isAir() || downHit.getType().isAir()) {
                        brokenBlocks = 2;
                    } else {
                        brokenBlocks = 3;
                    }
                    // 맞힌 위치의 위와 아래가 공기면 brokenBlocks 1 반환
                    // 둘 중 하나만 공기면 2 반환
                    // 둘다 과녁이면 3반환
                    hitBlock.breakNaturally();
                    upHit.breakNaturally();
                    downHit.breakNaturally();
                    event.getEntity().remove();
                    break;
                    // 값이 "2번 화살" 이면 맞춘 블럭과 양옆 블럭 제거, 바로 화살 삭제
                    // breakenBlocks의 값을 3으로 변경
            }
            gameManager.targetHit(player, brokenBlocks);
            // 변경된 brokenBlocks의 값을 targetHit 메소드에 대입하여
            // 남아있는 과녁 블록의 개수를 반환
        }
    }
}
