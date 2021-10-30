package me.untouchedodin0.prisonautominer;

import me.untouchedodin0.prisonautominer.autominer.AutoMiner;
import me.untouchedodin0.prisonautominer.autominer.utils.AutoMinerTrait;
import me.untouchedodin0.prisonautominer.commands.AutoMinerCommand;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;

import java.util.*;

public class PrisonAutoMiner extends JavaPlugin {

    private static PrisonAutoMiner prisonAutoMiner;
    private final List<Integer> npcList = new ArrayList<>();
    private final List<AutoMiner> autoMinerList = new ArrayList<>();
    private final Map<UUID, AutoMiner> autoMinerMap = new HashMap<>();

    @Override
    public void onEnable() {
        prisonAutoMiner = this;
        Bukkit.getLogger().info("Loading the Prison Auto Miner!");
        new CommandParser(this.getResource("command.rdcml"))
                .parse()
                .register("autominer",
                        new AutoMinerCommand());
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(AutoMinerTrait.class)
                .withName("AutoMinerTrait"));
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Auto Miner...");
        npcList.forEach(integer -> {
            NPC npc = CitizensAPI.getNPCRegistry().getById(integer);
            getLogger().info("Found npc: " + npc);
            npc.despawn();
            CitizensAPI.getNPCRegistry().deregister(npc);
        });
        autoMinerMap.forEach((uuid, autoMiner) -> {
            if (autoMiner.isSpawned()) {
                autoMiner.despawn();
            }
        });

//        CitizensAPI.getNPCRegistry().forEach(npc -> {
//            if (npc.hasTrait(AutoMinerTrait.class)) {
//                npc.despawn();
//            }
//        });
//        npcList.forEach(NPC::despawn);
//        autoMinerMap.forEach((uuid, autoMiner) -> {
//            autoMiner.pickup();
//        });
    }

    public void addNPC(int id) {
        if (npcList.contains(id)) {
            getLogger().info("NPC Was already in the list!");
        } else {
            npcList.add(id);
        }
    }

    public void removeNPC(int id) {
        if (!npcList.contains(id)) {
            getLogger().info("NPC didn't exist in the database!");
        } else {
            npcList.remove(id);
        }
    }

    public void addAutoMiner(AutoMiner autoMiner) {
        autoMinerList.add(autoMiner);
    }

    public void addAutoMiner(AutoMiner autoMiner, UUID uuid) {
        autoMinerMap.put(uuid, autoMiner);
    }


    public Map<UUID, AutoMiner> getAutoMinerMap() {
        return autoMinerMap;
    }

    public AutoMiner getAutoMiner(UUID uuid) {
        if (autoMinerMap.containsKey(uuid)) {
            return autoMinerMap.get(uuid);
        }
        return null;
    }

    //    public void clearAllNPCs() {
//        if (npcList.isEmpty()) {
//            getLogger().info("There were no npcs to clear!");
//        } else {
//            getLogger().info("Clearing all the NPCs!");
//            npcList.forEach(npc -> {
//                CitizensAPI.getNPCRegistry().getNPC(npc.getEntity()).despawn();
//                removeNPC(npc);
//            });
//        }
//    }

    public static PrisonAutoMiner getPrisonAutoMiner() {
        return prisonAutoMiner;
    }
}
