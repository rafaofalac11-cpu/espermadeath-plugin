package com.example.pollosmagicos;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class PollosMagicosCommand implements CommandExecutor, TabCompleter {

    private final PollosMagicos plugin;

    public PollosMagicosCommand(PollosMagicos plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden usar este comando.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Uso: /pollosmagicos <spawn|give>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "spawn" -> {
                Chicken chicken = (Chicken) player.getWorld().spawnEntity(
                        player.getLocation(), EntityType.CHICKEN);
                MagicChickenListener.convertToMagic(chicken);
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +
                        "Un Pollo Magico ha sido invocado!");
            }
            case "give" -> {
                if (args.length > 1) {
                    Player target = plugin.getServer().getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "Jugador no encontrado.");
                        return true;
                    }
                    if (target.getInventory().addItem(HuevitoVidaItem.create()).isEmpty()) {
                        player.sendMessage(ChatColor.GREEN + "Huevito de Vida entregado a " + target.getName());
                    } else {
                        player.sendMessage(ChatColor.RED + "El inventario de " + target.getName() + " esta lleno.");
                    }
                } else {
                    if (player.getInventory().addItem(HuevitoVidaItem.create()).isEmpty()) {
                        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +
                                "Has recibido un Huevito de Vida!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Tu inventario esta lleno.");
                    }
                }
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Subcomando desconocido. Usa: spawn, give");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("spawn", "give");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        return List.of();
    }
}
