package com.permadeath.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PermadeathListener implements Listener {

    private final PermaDeath plugin;

    public PermadeathListener(PermaDeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMuerte(PlayerDeathEvent event) {
        Player jugador = event.getEntity();
        plugin.marcarEliminado(jugador.getUniqueId());

        // Mensaje que verá el jugador al ser expulsado
        Component mensajeKick = Component.text("☠ Has muerto y quedas ELIMINADO de forma permanente.\n", NamedTextColor.DARK_RED)
                .append(Component.text("No podrás volver a entrar a este servidor.", NamedTextColor.GRAY));

        // Se expulsa un tick después para que la animación/mensaje de muerte se procese bien
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (jugador.isOnline()) {
                jugador.kick(mensajeKick);
            }
        });
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.estaEliminado(event.getUniqueId())) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("☠ Estás ELIMINADO de este servidor (permadeath).", NamedTextColor.DARK_RED)
            );
        }
    }
}
