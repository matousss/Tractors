package com.matousss.tractors;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class Resourcepack implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setResourcePack("https://github.com/matousss/Tractors/raw/resourcepack/resourcepack.zip");
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "VAROVANI:"
            + ChatColor.RESET + "Pokud si neaktivujes resourcepack nemusi vse fungovat sptavne!");
        }
    }
}

