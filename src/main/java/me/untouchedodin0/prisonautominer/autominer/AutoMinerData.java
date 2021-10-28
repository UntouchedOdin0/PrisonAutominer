package me.untouchedodin0.prisonautominer.autominer;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import redempt.redlib.region.Region;

import java.util.UUID;

public class AutoMinerData {

    private UUID owner;
    private Region region;
    private NPC npc;
    private Location location;

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
