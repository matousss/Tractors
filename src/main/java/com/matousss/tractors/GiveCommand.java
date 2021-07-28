package com.matousss.tractors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveCommand extends Command {
    protected GiveCommand() {
        super("givetractor");
        setPermission("tractors.givetractor");
        autoCompleteTiers = new ArrayList<>(Tractors.getInstance().TRACTOR_ITEMS.keySet());
        setUsage("/<command> <player> <amount> <tier>");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return false;
            }
            if (!Tractors.getInstance().TRACTOR_ITEMS.containsKey(args[2])) {
                sender.sendMessage(ChatColor.RED + "Invalid tier");
                return true;
            }
            ItemStack itemStack = new ItemStack(Tractors.getInstance().TRACTOR_ITEMS.get(args[2]));
            if (amount > itemStack.getMaxStackSize() || amount < 0) {
                sender.sendMessage(ChatColor.RED + "Invalid amount");
            }
            itemStack.setAmount(amount);
            target.getInventory().addItem(itemStack);
            sender.sendMessage(String.format("Gave %o [%s§r] to %s",
                    amount, itemStack.getItemMeta().getDisplayName(), target.getName()));
            return true;
        }
        return false;
    }

    private final List<String> autoCompleteTiers;
    private final List<String> autoCompleteAmount = new ArrayList<>(Arrays.asList("1", "64"));
    private final List<String> emptyList = new ArrayList<>();

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        sender.sendMessage(String.valueOf(args.length));
        if (args.length == 1) return super.tabComplete(sender, alias, args);

        if (args.length == 2) return autoCompleteAmount;
        if (args.length == 3) return autoCompleteTiers;
        if (args.length == 4) return Arrays.asList("lol");

        return emptyList;
    }
}
