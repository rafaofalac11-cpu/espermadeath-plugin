package com.example.ciclodias;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DiasCommand implements CommandExecutor, TabCompleter {

    private final CicloDias plugin;

    public DiasCommand(CicloDias plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DiasManager dm = plugin.getDiasManager();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Dia actual: " + ChatColor.WHITE + dm.getDiaActual());
            sender.sendMessage(ChatColor.GRAY + "Uso: /dias siguiente|anterior|set <num>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "siguiente", "next" -> {
                if (!sender.hasPermission("ciclodias.admin")) {
                    sender.sendMessage(ChatColor.RED + "No tienes permiso.");
                    return true;
                }
                dm.avanzarDia();
                sender.sendMessage(ChatColor.GREEN + "Dia avanzado a " + dm.getDiaActual());
            }
            case "anterior", "prev" -> {
                if (!sender.hasPermission("ciclodias.admin")) {
                    sender.sendMessage(ChatColor.RED + "No tienes permiso.");
                    return true;
                }
                dm.retrocederDia();
                sender.sendMessage(ChatColor.GREEN + "Dia retrocedido a " + dm.getDiaActual());
            }
            case "set" -> {
                if (!sender.hasPermission("ciclodias.admin")) {
                    sender.sendMessage(ChatColor.RED + "No tienes permiso.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /dias set <numero>");
                    return true;
                }
                try {
                    int dia = Integer.parseInt(args[1]);
                    dm.setDia(dia);
                    sender.sendMessage(ChatColor.GREEN + "Dia establecido a " + dia);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Numero invalido.");
                }
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Subcomandos: siguiente, anterior, set <num>");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("siguiente");
            completions.add("anterior");
            completions.add("set");
        }

        String input = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        return completions;
    }
}
