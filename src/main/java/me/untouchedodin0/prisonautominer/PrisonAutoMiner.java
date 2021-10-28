package me.untouchedodin0.prisonautominer;

import me.untouchedodin0.prisonautominer.commands.AutoMinerCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;

public class PrisonAutoMiner extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Loading the Prison Auto Miner!");
        new CommandParser(this.getResource("command.rdcml"))
                .parse()
                .register("autominer",
                        new AutoMinerCommand());
    }
}
