package me.untouchedodin0.prisonautominer.autominer;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.untouchedodin0.prisonautominer.utils.Utils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;

import java.util.List;

public class AutoMiner {

    private AutoMinerData autoMinerData;
    private Location npcLocation;
    private Location randomLocation = null;
    private boolean isSpawned = false;
    private Task task;
    private Utils utils;

    public AutoMinerData getAutoMinerData() {
        return autoMinerData;
    }

    public void setAutoMinerData(AutoMinerData autoMinerData) {
        this.autoMinerData = autoMinerData;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public void spawn(Player player, Location npcLocation) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
        npc.data().setPersistent(NPC.ITEM_ID_METADATA, Material.STONE.name());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = container.createQuery();
        ApplicableRegionSet protectedRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(npcLocation));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        utils = new Utils();

        if (!regionQuery.testState(BukkitAdapter.adapt(npcLocation), localPlayer, Flags.BLOCK_BREAK)) {
            player.sendMessage(ChatColor.RED + "You can't place your npc here! =(");
        } else {
            player.sendMessage(ChatColor.GREEN + "Spawning your NPC!");
            List<Block> blockList = utils.getBlockList(npcLocation, 15);
            autoMinerData.setOwner(player.getUniqueId());
            autoMinerData.setLocation(npcLocation);
            autoMinerData.setNpc(npc);
            setAutoMinerData(autoMinerData);
            this.npcLocation = npcLocation.add(0, 5, 0);
            this.isSpawned = npc.spawn(npcLocation);
            this.task = Task.syncRepeating(() -> {
                randomLocation = utils.getRandomLocation(blockList);
                boolean canBuild = utils.canBuild(player, randomLocation);
                PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
                npc.faceLocation(randomLocation);
                if (canBuild) {
                    randomLocation.getBlock().setType(Material.EMERALD_BLOCK);
                }
            }, 0L, 20L);
            Bukkit.broadcastMessage("blockList: " + blockList);
        }
    }

    public void pickup() {
        NPC npc = autoMinerData.getNpc();
            npc.destroy();
            this.isSpawned = false;
            this.task.cancel();
    }
}

