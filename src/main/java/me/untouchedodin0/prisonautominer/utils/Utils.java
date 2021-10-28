package me.untouchedodin0.prisonautominer.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

//    public Location toWeLocation(org.bukkit.Location location) {
//        return BukkitAdapter.adapt(location);
//    }

    public static EulerAngle angleToEulerAngle(int degrees) {
        return angleToEulerAngle(Math.toRadians(degrees));
    }

    public static EulerAngle angleToEulerAngle(double radians) {
        double x = Math.cos(radians);
        double z = Math.sin(radians);
        return new EulerAngle(x, 0, z);
    }

    public List<Block> getBlockList(org.bukkit.Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        World world = location.getWorld();

        Location minimum = new Location(
                location.getWorld(),
                location.getX() - radius,
                location.getY() - 1,
                location.getZ() - radius);
        Location maximum = new Location(location.getWorld(),
                location.getX() + radius,
                location.getY() - 1,
                location.getZ() + radius);

        int topBlockX = (Math.max(minimum.getBlockX(), maximum.getBlockX()));
        int bottomBlockX = (Math.min(minimum.getBlockX(), maximum.getBlockX()));

        int topBlockY = (Math.max(minimum.getBlockY(), maximum.getBlockY()));
        int bottomBlockY = (Math.min(minimum.getBlockY(), maximum.getBlockY()));

        int topBlockZ = (Math.max(minimum.getBlockZ(), maximum.getBlockZ()));
        int bottomBlockZ = (Math.min(minimum.getBlockZ(), maximum.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = null;
                    if (world != null) {
                        block = world.getBlockAt(x, y, z);
                    }
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public Location getRandomLocation(List<Block> blocks) {
        Random random = new Random();
        return blocks.get(random.nextInt(blocks.size())).getLocation();
    }

    public boolean canBuild(final Player player, final Location location) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = regionContainer.createQuery();
        return regionQuery.testState(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK);
    }
}

