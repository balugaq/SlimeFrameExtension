package io.github.acdeasdff.SlimeFrameExtension.Listeners;

import city.norain.slimefun4.compatibillty.VersionedAttribute;
import io.github.acdeasdff.SlimeFrameExtension.ItemMetaRelated.Keys;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

import static io.github.mooy1.infinitylib.core.AbstractAddon.instance;

public class PlayerKillEntityListener implements Listener {

    Random random = new Random();

    @EventHandler
    public void EndoListener(EntityDamageByEntityEvent e) {

        //givingEndo
        if (!(e.getEntity() instanceof Player) || (e.getEntity() instanceof LivingEntity)) {//not attacking player
            if (e.getDamager() instanceof Player) {//player attacking
                if (random.nextInt(50) <= 1) {//drop chance:4%
                    int endos = PersistentDataAPI.getInt(e.getDamager(), Keys.ENDOS_OWNED, 0);

                    if (((LivingEntity) e.getEntity())
                            .getAttribute(VersionedAttribute.getMaxHealth())//has health
                            != null
                    ) {
                        if (((LivingEntity) e.getEntity()).getHealth() <= e.getDamage()) {//dead
                            endos += ((LivingEntity) e.getEntity())
                                    .getAttribute(VersionedAttribute.getMaxHealth())
                                    .getValue()
                                    + random.nextInt(10);
                            PersistentDataAPI.setInt(e.getDamager(), Keys.ENDOS_OWNED, endos);
                        }

                    }
                }
            } else if (e.getDamager() instanceof Projectile) {//ranged attack
                if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    //do the same thing
                    if (random.nextInt(50) <= 1) {//drop chance:4%
                        int endos = PersistentDataAPI.getInt(e.getDamager(), Keys.ENDOS_OWNED, 0);

                        if (((LivingEntity) e.getEntity())
                                .getAttribute(VersionedAttribute.getMaxHealth())//has health
                                != null
                        ) {
                            if (((LivingEntity) e.getEntity()).getHealth() <= e.getDamage()) {//dead
                                endos += Math.min(((LivingEntity) e.getEntity())
                                        .getAttribute(VersionedAttribute.getMaxHealth())
                                        .getValue(), 600)
                                        + random.nextInt(10);
                                PersistentDataAPI.setInt(e.getDamager(), Keys.ENDOS_OWNED, endos);
                            }

                        }
                    }
                }
            }
        }

        long currentTime = System.currentTimeMillis();
        Entity entity1 = e.getEntity();
        if (entity1.getMetadata("sfe_viral").size() > 0) {
            long viral_time = entity1.getMetadata("sfe_viral_time").get(0).asLong();
            int viral_level = entity1.getMetadata("sfe_viral_level").get(0).asInt();

            if (entity1.getMetadata("sfe_viral_lasts").get(0).asLong()
                    < currentTime - viral_time) {
                entity1.removeMetadata("sfe_viral_lasts", instance());
                entity1.removeMetadata("sfe_viral_time", instance());
                entity1.removeMetadata("sfe_viral_first_time", instance());
                entity1.removeMetadata("sfe_viral_level", instance());
                entity1.removeMetadata("sfe_viral", instance());
            } else {
                e.setDamage(e.getDamage() * (viral_level * 0.25 + 0.75));
            }
        }

    }


}
