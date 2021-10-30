package me.untouchedodin0.prisonautominer.autominer.utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class AutoMinerNPC {

    public static NPC createAutoMinerNPC(String name, Location location, UUID owner) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.setProtected(true);

        AutoMinerTrait autoMinerTrait = npc.getOrAddTrait(AutoMinerTrait.class);

        if (autoMinerTrait == null) {
            autoMinerTrait = new AutoMinerTrait();
            npc.addTrait(autoMinerTrait);
        }
        autoMinerTrait.setOwner(owner);
        return npc;
    }
}
