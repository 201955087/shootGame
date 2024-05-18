package me.air_bottle.muneong_plugin;

import me.air_bottle.muneong_plugin.command.run_command;
import me.air_bottle.muneong_plugin.event.events;
import me.air_bottle.muneong_plugin.shootgame.shootGame;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Muneong_plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().warning("플러그인 활성화");
        Bukkit.getCommandMap().register("command_plugin", new run_command("random_tp"));
        Bukkit.getCommandMap().register("command_plugin", new shootGame("shootGame"));
        Bukkit.getCommandMap().register("command_plugin", new shootGame("earlyEnd"));
        Bukkit.getPluginManager().registerEvents(new events(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().warning("플러그인 비활성화");
    }
}
