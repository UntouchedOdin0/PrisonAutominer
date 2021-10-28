package me.untouchedodin0.prisonautominer.autominer.utils;

import me.untouchedodin0.prisonautominer.PrisonAutoMiner;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoMinerTrait extends Trait {

    PrisonAutoMiner prisonAutoMiner;
    NPC npc;

    public AutoMinerTrait() {
        super("AutoMiner");
        prisonAutoMiner = JavaPlugin.getPlugin(PrisonAutoMiner.class);
    }
}
