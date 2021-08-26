package com.matousss.tractors;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebugTractor extends Command {
    protected DebugTractor() {
        super("debugtractor");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] strings) {
        sender.sendMessage("fuck");
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Entity vehicle = player.getVehicle();
            ArmorStand armorStand;
            if (vehicle != null && Tractors.getInstance().isTractor(vehicle)) {
                armorStand = (ArmorStand) vehicle;
            }
            else {
                player.sendMessage("Sit on tractor please and try it again");
                return true;
            }

            Location location = armorStand.getLocation();
            Vector v = location.getDirection().normalize();
            Vector w = location.getDirection().normalize().rotateAroundY(Math.PI/2.0).multiply(.5);
            Location[] locations = new Location[4];
            locations[0] = location.add(v.clone().multiply(1.5)).add(w);
            locations[1] = location.add(v.clone().multiply(1.5));
            locations[2] = location.add(v.clone().multiply(-.5)).add(w);
            locations[3] = location.add(v.clone().multiply(-.5));
            player.sendMessage(locations.toString());
            int counter = 0;
            for (Location l : locations) {
                location.getWorld().spawnEntity(l, EntityType.ARMOR_STAND).setCustomName(counter+"");
                counter++;
            }
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return new ArrayList<>();
    }
}
