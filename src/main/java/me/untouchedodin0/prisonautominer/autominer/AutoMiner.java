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
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    public void spawn(Player player, Location npcLocation, int reachDistance) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
        npc.data().setPersistent(NPC.ITEM_ID_METADATA, Material.STONE.name());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = container.createQuery();
        ApplicableRegionSet protectedRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(npcLocation));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        utils = new Utils();

        if (!regionQuery.testState(BukkitAdapter.adapt(npcLocation.subtract(0, 1, 0)),
                localPlayer, Flags.BLOCK_BREAK)) {
            player.sendMessage(ChatColor.RED + "You can't place your npc here! =(");
        } else {
            player.sendMessage(ChatColor.GREEN + "Spawning your NPC!");
            List<Block> blockList = utils.getBlockList(npcLocation.add(0, 1, 0), reachDistance);
            autoMinerData.setOwner(player.getUniqueId());
            autoMinerData.setLocation(npcLocation);
            autoMinerData.setNpc(npc);
            setAutoMinerData(autoMinerData);
            boolean isBlockSafe = npcLocation.getBlock().getRelative(BlockFace.SELF).isEmpty();
            if (!isBlockSafe) {
                player.sendMessage("It wasn't safe to spawn your npc here!");
                return;
            }

            this.isSpawned = npc.spawn(npcLocation);

            ItemStack diamondPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);

            Equipment equipment = npc.getOrAddTrait(Equipment.class);
            equipment.set(Equipment.EquipmentSlot.HAND, diamondPickaxe);

            this.task = Task.syncRepeating(() -> {
                randomLocation = utils.getRandomLocation(blockList);
                boolean canBuild = utils.canBuild(player, randomLocation);

                PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
                npc.faceLocation(randomLocation);
                if (!randomLocation.getBlock().isEmpty() && canBuild) {
                    randomLocation.getBlock().setType(Material.EMERALD_BLOCK);
                }
            }, 0L, 20L);
        }
    }

    public void pickup() {
        NPC npc = autoMinerData.getNpc();
            npc.destroy();
            this.isSpawned = false;
            this.task.cancel();
    }
}

