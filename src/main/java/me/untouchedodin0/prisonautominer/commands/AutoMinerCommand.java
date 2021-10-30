package me.untouchedodin0.prisonautominer.commands;

import me.untouchedodin0.prisonautominer.PrisonAutoMiner;
import me.untouchedodin0.prisonautominer.autominer.AutoMiner;
import me.untouchedodin0.prisonautominer.autominer.AutoMinerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

public class AutoMinerCommand {

    boolean isSpawned;
    AutoMiner autoMiner;
    PrisonAutoMiner prisonAutoMiner;

    @CommandHook("spawn")
    public void spawnNpc(CommandSender commandSender) {
        Player player = (Player) commandSender;
        this.autoMiner = new AutoMiner();
        this.isSpawned = autoMiner.isSpawned();
        this.prisonAutoMiner = PrisonAutoMiner.getPrisonAutoMiner();

        player.sendMessage("isSpawned before: " + isSpawned);
        if (isSpawned) {
            autoMiner.despawn();
        } else {
            AutoMinerData autoMinerData = new AutoMinerData();
            autoMinerData.setOwner(player.getUniqueId());
            autoMinerData.setLocation(player.getLocation());
            autoMiner.setAutoMinerData(autoMinerData);
            autoMiner.spawn(player, player.getLocation(), 5);
            player.sendMessage(autoMiner.toString());
            prisonAutoMiner.addAutoMiner(autoMiner, player.getUniqueId());
        }
        player.sendMessage("isSpawned after: " + isSpawned);
    }

    @CommandHook("despawn")
    public void despawnNpc(CommandSender commandSender) {
        Player player = (Player) commandSender;
        this.prisonAutoMiner = PrisonAutoMiner.getPrisonAutoMiner();
        this.autoMiner = prisonAutoMiner.getAutoMinerMap().get(player.getUniqueId());
        player.sendMessage("isSpawned before: " + isSpawned);
        if (autoMiner == null || !autoMiner.isSpawned()) {
            player.sendMessage(ChatColor.RED + "Your autominer wasn't even spawned!");
            return;
        } else {
            autoMiner.despawn();
            player.sendMessage("Picking up your auto miner");
        }
        player.sendMessage("isSpawned after: " + isSpawned);
    }
}
