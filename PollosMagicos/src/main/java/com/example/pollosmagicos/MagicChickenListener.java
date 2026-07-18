package com.example.pollosmagicos;

import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MagicChickenListener implements Listener {

    private final PollosMagicos plugin;
    private static final NamespacedKey MAGIC_KEY = new NamespacedKey("pollosmagicos", "magico");
    private static final String GUI_TITLE = ChatColor.GOLD + "\u2726 Pollo Magico - Comercio \u2726";
    private static final int[] OUTPUT_SLOTS = {12, 16, 21, 25};

    private record Trade(Material input, int inputAmount, Material output, int outputAmount, String name) {}

    private static final List<Trade> TRADES = List.of(
            new Trade(Material.OAK_LOG, 64, Material.DIAMOND, 2, "Diamantes"),
            new Trade(Material.IRON_INGOT, 20, Material.GOLDEN_APPLE, 1, "Manzana Dorada"),
            new Trade(Material.GOLD_INGOT, 64, Material.TOTEM_OF_UNDYING, 1, "Totem de la Inmortalidad"),
            new Trade(Material.GOLD_BLOCK, 10, Material.ENCHANTED_GOLDEN_APPLE, 1, "Manzana de Notch")
    );

    public MagicChickenListener(PollosMagicos plugin) {
        this.plugin = plugin;
        iniciarTareaProximidad();
        iniciarTareaParticulas();
    }

    // ==================== SPAWN: 5% chance ====================

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.CHICKEN) return;
        if (isMagicChicken(event.getEntity())) return;

        if (new Random().nextDouble() < 0.05) {
            convertToMagic(event.getEntity());
        }
    }

    // ==================== RIGHT-CLICK: Abrir GUI ====================

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Chicken)) return;
        if (!isMagicChicken(event.getRightClicked())) return;

        event.setCancelled(true);
        abrirGUIComercio(event.getPlayer());
    }

    // ==================== CLICK EN GUI: Tradeo ====================

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 27) return;

        int tradeIndex = -1;
        for (int i = 0; i < OUTPUT_SLOTS.length; i++) {
            if (OUTPUT_SLOTS[i] == slot) {
                tradeIndex = i;
                break;
            }
        }

        if (tradeIndex >= 0) {
            realizarTradeo(event.getWhoClicked(), tradeIndex);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }

    // ==================== MUERTE: 15% drop huevito ====================

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Chicken)) return;
        if (!isMagicChicken(event.getEntity())) return;

        event.getDrops().clear();
        event.setDroppedExp(0);

        if (new Random().nextDouble() < 0.15) {
            event.getDrops().add(HuevitoVidaItem.create());
            for (Player player : event.getEntity().getWorld().getPlayers()) {
                if (player.getLocation().distance(event.getEntity().getLocation()) <= 20) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD +
                            "El Pollo Magico dejo caer un Huevito de Vida!");
                }
            }
        }
    }

    // ==================== UTILIDADES ====================

    public static boolean isMagicChicken(Entity entity) {
        return entity.getPersistentDataContainer().has(MAGIC_KEY, PersistentDataType.INTEGER);
    }

    public static void convertToMagic(Entity entity) {
        if (isMagicChicken(entity)) return;

        entity.getPersistentDataContainer().set(MAGIC_KEY, PersistentDataType.INTEGER, 1);
        entity.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + "\u2726 Pollo Magico \u2726");
        entity.setCustomNameVisible(true);
        entity.setGlowing(true);

        if (entity instanceof Chicken chicken) {
            chicken.setAgeLock(true);
            chicken.setBaby(false);
        }
    }

    // ==================== GUI DE COMERCIO ====================

    private void abrirGUIComercio(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        ItemStack vidriera =crearVidriera();

        for (int i = 0; i < 9; i++) gui.setItem(i, vidriera);
        gui.setItem(9, vidriera);
        gui.setItem(13, vidriera);
        gui.setItem(17, vidriera);
        gui.setItem(18, vidriera);
        gui.setItem(22, vidriera);
        gui.setItem(26, vidriera);

        gui.setItem(10, crearItemInput(Material.OAK_LOG, 64, "Troncos de Roble"));
        gui.setItem(11, crearFlecha());
        gui.setItem(12, crearItemOutput(Material.DIAMOND, 2, "Diamantes"));

        gui.setItem(14, crearItemInput(Material.IRON_INGOT, 20, "Lingotes de Hierro"));
        gui.setItem(15, crearFlecha());
        gui.setItem(16, crearItemOutput(Material.GOLDEN_APPLE, 1, "Manzana Dorada"));

        gui.setItem(19, crearItemInput(Material.GOLD_INGOT, 64, "Lingotes de Oro"));
        gui.setItem(20, crearFlecha());
        gui.setItem(21, crearItemOutput(Material.TOTEM_OF_UNDYING, 1, "Totem de la Inmortalidad"));

        gui.setItem(23, crearItemInput(Material.GOLD_BLOCK, 10, "Bloques de Oro"));
        gui.setItem(24, crearFlecha());
        gui.setItem(25, crearItemOutput(Material.ENCHANTED_GOLDEN_APPLE, 1, "Manzana de Notch"));

        player.openInventory(gui);
    }

    private void realizarTradeo(HumanEntity player, int tradeIndex) {
        Trade trade = TRADES.get(tradeIndex);

        if (player.getInventory().containsAtLeast(new ItemStack(trade.input()), trade.inputAmount())) {
            player.getInventory().removeItem(new ItemStack(trade.input(), trade.inputAmount()));

            ItemStack output = new ItemStack(trade.output(), trade.outputAmount());
            HashMap<Integer, ItemStack> sobrante = player.getInventory().addItem(output);
            if (!sobrante.isEmpty()) {
                sobrante.values().forEach(item ->
                        player.getWorld().dropItemNaturally(player.getLocation(), item));
            }

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.2f);
            player.sendMessage(ChatColor.GREEN + "Tradeo completado! Has recibido " +
                    trade.outputAmount() + "x " + trade.name());
        } else {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(ChatColor.RED + "No tienes suficientes " + trade.input().name() +
                    " (" + trade.inputAmount() + ") para este tradeo.");
        }
    }

    // ==================== EFECTOS DE PROXIMIDAD ====================

    private void iniciarTareaProximidad() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Chicken chicken : world.getEntitiesByClass(Chicken.class)) {
                        if (!isMagicChicken(chicken)) continue;

                        for (Player player : world.getPlayers()) {
                            if (player.getLocation().distance(chicken.getLocation()) <= 10) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.STRENGTH, 40, 1, true, false, false));
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.RESISTANCE, 40, 0, true, false, false));
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.REGENERATION, 40, 0, true, false, false));
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.SPEED, 40, 0, true, false, false));
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // ==================== PARTICULAS MAGICAS ====================

    private void iniciarTareaParticulas() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Chicken chicken : world.getEntitiesByClass(Chicken.class)) {
                        if (!isMagicChicken(chicken)) continue;

                        Location loc = chicken.getLocation().add(0, 1, 0);
                        world.spawnParticle(Particle.FLAME, loc, 3, 0.3, 0.3, 0.3, 0.01);
                        world.spawnParticle(Particle.PORTAL, loc, 2, 0.2, 0.5, 0.2, 0.5);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    // ==================== HELPERS DE ITEMS ====================

    private ItemStack crearVidriera() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack crearFlecha() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "\u2192");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack crearItemInput(Material material, int amount, String nombre) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + nombre);
        meta.setLore(List.of(
                ChatColor.GRAY + "Necesitas: " + amount + "x " + nombre,
                "",
                ChatColor.YELLOW + "Click en el resultado para tradear"
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack crearItemOutput(Material material, int amount, String nombre) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + nombre);
        meta.setLore(List.of(
                ChatColor.GRAY + "Recibes: " + amount + "x " + nombre,
                "",
                ChatColor.GREEN + "Click para tradear"
        ));
        item.setItemMeta(meta);
        return item;
    }
}
