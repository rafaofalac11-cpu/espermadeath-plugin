package com.example.pollosmagicos;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class HuevitoVidaItem {

    private static final NamespacedKey KEY = new NamespacedKey("pollosmagicos", "huevito_vida");

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.DRAGON_EGG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Huevito de Vida");
        meta.setLore(List.of(
                "",
                ChatColor.LIGHT_PURPLE + "Un huevo magico que te otorga",
                ChatColor.LIGHT_PURPLE + "poderes sobrenaturales.",
                "",
                ChatColor.YELLOW + "Efectos: Fuerza, Resistencia,",
                ChatColor.YELLOW + "Regeneracion y Velocidad",
                "",
                ChatColor.GRAY + "Llevatelo en tu inventario para activar sus efectos."
        ));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isHuevitoDeVida(ItemStack item) {
        if (item == null || item.getType() != Material.DRAGON_EGG) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(KEY, PersistentDataType.INTEGER);
    }
}
