package com.example.ciclodias;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.UUID;

public class VillagerDiscount {

    private final CicloDias plugin;
    private final double factorDescuento;
    private final HashSet<UUID> descuentoAplicado = new HashSet<>();

    public VillagerDiscount(CicloDias plugin, double porcentajeDescuento) {
        this.plugin = plugin;
        this.factorDescuento = 1.0 - (porcentajeDescuento / 100.0);
        plugin.getLogger().info("Descuento de aldeanos activo: " + porcentajeDescuento + "%");
        iniciarTarea();
    }

    private void iniciarTarea() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
                    if (!(entity instanceof Villager villager)) continue;
                    if (descuentoAplicado.contains(villager.getUniqueId())) continue;

                    boolean cerca = false;
                    for (Entity nearby : villager.getNearbyEntities(16, 16, 16)) {
                        if (nearby instanceof org.bukkit.entity.Player) {
                            cerca = true;
                            break;
                        }
                    }
                    if (!cerca) continue;

                    aplicarDescuento(villager);
                    descuentoAplicado.add(villager.getUniqueId());
                }
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }

    private void aplicarDescuento(Villager villager) {
        try {
            Entity nmsVillager = toNMS(villager);
            Method getOffers = nmsVillager.getClass().getMethod("getOffers");
            Object offerList = getOffers.invoke(nmsVillager);

            Method sizeMethod = offerList.getClass().getMethod("size");
            int size = (int) sizeMethod.invoke(offerList);

            Method getMethod = offerList.getClass().getMethod("get", int.class);

            for (int i = 0; i < size; i++) {
                Object offer = getMethod.invoke(offerList, i);
                aplicarDescuentoOferta(offer);
            }

            plugin.getLogger().info("Descuento aplicado al aldeano: " + villager.getUniqueId());
        } catch (Exception e) {
            plugin.getLogger().warning("Error aplicando descuento al aldeano: " + e.getMessage());
        }
    }

    private void aplicarDescuentoOferta(Object offer) {
        try {
            Method getCost = offer.getClass().getMethod("getCost");
            Object costItem = getCost.invoke(offer);

            Method getCount = costItem.getClass().getMethod("getCount");
            int countOriginal = (int) getCount.invoke(costItem);

            int countDescuento = Math.max(1, (int) Math.round(countOriginal * factorDescuento));

            if (countDescuento != countOriginal) {
                Field countField = costItem.getClass().getDeclaredField("count");
                countField.setAccessible(true);

                if (countField.getType() == int.class) {
                    countField.setInt(costItem, countDescuento);
                } else {
                    Object mutableInt = countField.get(costItem);
                    Field valueField = mutableInt.getClass().getDeclaredField("value");
                    valueField.setAccessible(true);
                    valueField.setInt(mutableInt, countDescuento);
                }
            }
        } catch (Exception e) {
            try {
                Method getCost = offer.getClass().getMethod("getCost");
                Object costItem = getCost.invoke(offer);

                Method setCount = costItem.getClass().getMethod("setCount", int.class);
                int countOriginal = (int) costItem.getClass().getMethod("getCount").invoke(costItem);
                int countDescuento = Math.max(1, (int) Math.round(countOriginal * factorDescuento));
                setCount.invoke(costItem, countDescuento);
            } catch (Exception e2) {
                plugin.getLogger().fine("No se pudo aplicar descuento a oferta: " + e2.getMessage());
            }
        }
    }

    private Entity toNMS(Villager villager) {
        try {
            Method getHandle = villager.getClass().getMethod("getHandle");
            return (Entity) getHandle.invoke(villager);
        } catch (Exception e) {
            return villager;
        }
    }
}
