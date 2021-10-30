package me.untouchedodin0.prisonautominer.autominer.utils;

import me.untouchedodin0.prisonautominer.PrisonAutoMiner;
import net.citizensnpcs.api.persistence.DelegatePersistence;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.persistence.UUIDPersister;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AutoMinerTrait extends Trait {

    PrisonAutoMiner prisonAutoMiner;

    @Persist("owner")
    @DelegatePersistence(UUIDPersister.class)
    private UUID owner;

    public AutoMinerTrait() {
        super("AutoMiner");
        prisonAutoMiner = JavaPlugin.getPlugin(PrisonAutoMiner.class);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }
}
