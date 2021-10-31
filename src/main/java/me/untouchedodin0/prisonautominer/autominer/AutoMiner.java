package me.untouchedodin0.prisonautominer.autominer;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.untouchedodin0.prisonautominer.PrisonAutoMiner;
import me.untouchedodin0.prisonautominer.autominer.utils.AutoMinerNPC;
import me.untouchedodin0.prisonautominer.utils.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.misc.Task;

import java.util.List;
import java.util.UUID;

public class AutoMiner {

    private AutoMinerData autoMinerData;
    private Location npcLocation;
    private Location randomLocation = null;
    private boolean isSpawned = false;
    private Task task;
    private Utils utils;
    private PrisonAutoMiner prisonAutoMiner;
    private UUID owner;
    private NPC npc;

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

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = container.createQuery();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        utils = new Utils();
        prisonAutoMiner = PrisonAutoMiner.getPrisonAutoMiner();
        owner = player.getUniqueId();
        boolean isBlockSafe = npcLocation.getBlock().getRelative(BlockFace.SELF).isEmpty();
        Location spawnLocation = player.getLocation();
        Location startLocation;

        // Quick check to test if the user could break blocks below the npc
        if (!regionQuery.testState(BukkitAdapter.adapt(npcLocation.subtract(0, 1, 0)),
                localPlayer, Flags.BLOCK_BREAK) || !isBlockSafe) {
            player.sendMessage(ChatColor.RED + "You can't place your npc here! =(");
        } else {

            // Let the player know their npc will be spawned
            player.sendMessage(ChatColor.GREEN + "Spawning your NPC!");

            // Set the npc field up by creating the npc
            this.npc = AutoMinerNPC.createAutoMinerNPC(player.getName(), spawnLocation,
                    player.getUniqueId());

            // This might come in useful at a later point actually to despawn the npcs,
            // run a loop over all the npc's to see if they have the autominer string and
            // if so disable them?

            npc.data().setPersistent(NPC.ITEM_ID_METADATA, Material.STONE.name());
            startLocation = spawnLocation.clone(); // no need to subtract from this getBlockList does that

            // This gets a list of the blocks in a square region within a certain radius

            List<Block> blockList = utils.getBlockList(startLocation, reachDistance);

            // Set the auto miners data such as the owner, location and npc

            autoMinerData.setOwner(player.getUniqueId());
            autoMinerData.setLocation(npcLocation);
            autoMinerData.setNpc(npc);
            setAutoMinerData(autoMinerData);

            // Tell the player the auto miner is spawned
            this.isSpawned = true;

            // Create the auto miner item stack aka what they're holding in their hand such as a pickaxe

            ItemStack diamondPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);

            // Get or add the npc's equipment
            // Currently sets the item in the hand to a diamond pickaxe :D

            Equipment equipment = npc.getOrAddTrait(Equipment.class);
            equipment.set(Equipment.EquipmentSlot.HAND, diamondPickaxe);

            // Start the repeating task, this is what's used to get a random block and then break it

            this.task = Task.syncRepeating(() -> {

                // Get a random location from a block list
                randomLocation = utils.getRandomLocation(blockList);

                // A check to see if we can build at the location
                boolean canBuild = utils.canBuild(player, randomLocation);

                // If we can build at the location then we can then
                // face the location and break the block

                if (canBuild) {
                    npc.faceLocation(randomLocation);
                    PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
                    randomLocation.getBlock().setType(Material.EMERALD_BLOCK);
                }
            }, 0L, 20L);
            prisonAutoMiner.addNPC(npc.getId());
            prisonAutoMiner.addAutoMiner(this);
        }
    }

    public void despawn() {
        NPC npc = autoMinerData.getNpc();
        npc.destroy();
        task.cancel();
    }
}

