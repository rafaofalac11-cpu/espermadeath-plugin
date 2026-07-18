package com.permadeath.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RevivirCommand implements CommandExecutor {

    private final PermaDeath plugin;

    public RevivirCommand(PermaDeath plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("permadeath.revivir")) {
            sender.sendMessage("No tienes permiso para usar este comando.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("Uso: /revivir <nombre_de_jugador>");
            return true;
        }

        OfflinePlayer jugador = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.estaEliminado(jugador.getUniqueId())) {
            sender.sendMessage(args[0] + " no está eliminado actualmente.");
            return true;
        }

        plugin.quitarEliminado(jugador.getUniqueId());
        sender.sendMessage(args[0] + " ha sido revivido y ya puede volver a entrar al servidor.");
        return true;
    }
}
