package com.matousss.tractors;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Tractors extends JavaPlugin {
    ProtocolLib protocolLib;


    public final HashMap<String, ItemStack> TRACTOR_ITEMS = new HashMap<>();

    private static Tractors instance;
    public static Tractors getInstance() {
        return instance;
    }

    private ProtocolManager protocolManager;

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }


    @Override
    public void onEnable() {
        instance = this;
        protocolLib = (ProtocolLib) getServer().getPluginManager().getPlugin("ProtocolLib");
        if (protocolLib == null) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + String.format(
                    "[%s] No ProtocolLib dependency found! Disabling ", getDescription().getName()) +
                    getDescription().getName() + getDescription().getVersion());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerTractor(new Tractor(), "1");
        registerTractor(new Tractor(Material.JIGSAW, "§4Traktor"), "2");
        registerTractor(new Tractor(Material.CHORUS_FLOWER, "§5Traktor"), "experimental");
        ((CraftServer) getServer()).getCommandMap().register("givetractor", new GiveCommand());

    }

    @Override
    public void onDisable() {

    }

    private void registerTractor(Tractor tractor, String tierID) {
        TRACTOR_ITEMS.put(tierID, tractor.TRACTOR_ITEM);
        getServer().getPluginManager().registerEvents(tractor, this);
    }
}
