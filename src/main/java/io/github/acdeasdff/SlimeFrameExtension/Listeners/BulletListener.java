package io.github.acdeasdff.SlimeFrameExtension.Listeners;
//package io.github.seggan.slimefunwarfare.listeners;

//import io.github.seggan.slimefunwarfare.SlimefunWarfare;
//import io.github.seggan.slimefunwarfare.Util;

import io.github.acdeasdff.SlimeFrameExtension.Items.Abstracts.AbstractRangedWeapon;
import io.github.mooy1.infinitylib.common.Scheduler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.libraries.dough.versions.Versioned;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedEnchantment;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedPotionEffectType;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static io.github.acdeasdff.SlimeFrameExtension.SlimeFrameExtension.*;

public class BulletListener implements Listener {


    public static Random random = new Random();

    public static List<Double> punchLocation(Location location, double punchThrough) {
        return punchLocation(location.getX(), location.getY(), location.getZ(), punchThrough);
    }

    public static List<Double> punchLocation(double x, double y, double z, double punchThrough) {
        if (punchThrough > 0) {
            double fastInvSqrt = invSqrt((float) (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
            if (punchThrough >= 1) {
                return List.of(x + (x / fastInvSqrt), y + (y / fastInvSqrt), z + (z / fastInvSqrt), punchThrough - 1);
            } else {
                return List.of(x + (x * punchThrough / fastInvSqrt), y + (y * punchThrough / fastInvSqrt), z + (z * punchThrough / fastInvSqrt), 0.);
            }
        } else {
            return List.of(x, y, z, 0.);
        }
    }

    public static float invSqrt(float x) {
        float xHalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xHalf * x * x);
        return x;
    }

    public static double[] CalculateDamage(double[] basicDamages, double[] ExtraCalculates, EntityDamageByEntityEvent e, double[] MODAdder) {
        if (e.getDamager() instanceof Projectile) {
            return CalculateDamage(basicDamages, ExtraCalculates, e.getDamager(), ((Projectile) e.getDamager()).getShooter(), e.getEntity(), MODAdder);
        } else {
            return CalculateDamage(basicDamages, ExtraCalculates, e.getDamager(), null, e.getEntity(), MODAdder);
        }
    }

    public static double[] CalculateDamage(double[] basicDamages, double[] ExtraCalculates, Object projectile, Object shooter, Entity targetEntity, double[] MODAdder) {
        return CalculateDamage(basicDamages, ExtraCalculates, shooter, targetEntity, MODAdder);
    }

    public static double[] CalculateDamage(double[] basicDamages, double[] ExtraCalculates, Object shooter, Entity targetEntity, double[] MODAdder) {
        double mobTypeDamageMultiplier = 1;
        double Damage = 0;
        double fireMODMultiplier = 1 + MODAdder[4];
        double toxinMODMultiplier = 1 + MODAdder[6];
        double electricMODMultiplier = 1 + MODAdder[7];
        double criticalMultipier = ExtraCalculates[0];
        double criticalChance = ExtraCalculates[1];
        double statusChance = ExtraCalculates[2];
        double statusTime = ExtraCalculates[3];
        long currentTime = System.currentTimeMillis();

        for (double i : basicDamages) {
            Damage += i;
        }

        Damage *= mobTypeDamageMultiplier;
        Damage *= (MODAdder[0] + 1);

        if (targetEntity instanceof LivingEntity entity1) {
            //            logger.log(Level.WARNING, "A");
            if (entity1.getMetadata("sfe_puncture").size() > 0) {

                long puncture_time = entity1.getMetadata("sfe_puncture_time").get(0).asLong();
                int puncture_level = entity1.getMetadata("sfe_puncture_level").get(0).asInt();

                if (entity1.getMetadata("sfe_puncture_lasts").get(0).asLong()
                        < currentTime - puncture_time) {
                    entity1.removeMetadata("sfe_puncture_lasts", instance());
                    entity1.removeMetadata("sfe_puncture_time", instance());
                    entity1.removeMetadata("sfe_puncture_level", instance());
                    entity1.removeMetadata("sfe_puncture", instance());
                } else {
                    criticalChance += 0.05 * puncture_level;
                }
            }
//            logger.log(Level.WARNING, "B");
            if (entity1.getMetadata("sfe_cold").size() > 0) {
                long cold_time = entity1.getMetadata("sfe_cold_time").get(0).asLong();
                int cold_level = entity1.getMetadata("sfe_cold_level").get(0).asInt();

                if (entity1.getMetadata("sfe_cold_lasts").get(0).asLong()
                        < currentTime - cold_time) {
                    entity1.removeMetadata("sfe_cold_lasts", instance());
                    entity1.removeMetadata("sfe_cold_time", instance());
                    entity1.removeMetadata("sfe_cold_level", instance());
                    entity1.removeMetadata("sfe_cold", instance());
                } else {
                    criticalMultipier += (cold_level + 1) * 0.05;
                }
            }
//            logger.log(Level.WARNING, "C");
            double criticalPow = Math.floor(criticalChance);//actually it's an int
            if (criticalChance - criticalPow >= random.nextDouble()) {
                criticalPow += 1;
            }
//            logger.log(Level.WARNING, String.valueOf(Damage));
            Damage *= Math.pow(criticalMultipier, criticalPow);
//            logger.log(Level.WARNING, String.valueOf(Damage));
//            double slashDamage = 0;
            if (entity1.getMetadata("sfe_slash").size() > 0) {
                long slash_time = entity1.getMetadata("sfe_slash_time").get(0).asLong();
                int slash_level = entity1.getMetadata("sfe_slash_level").get(0).asInt();
                int slash_lasts = entity1.getMetadata("sfe_slash_lasts").get(0).asInt();

                if (entity1.getMetadata("sfe_slash_lasts").get(0).asLong()
                        < currentTime - slash_time) {
//                    slashDamage = 0;
                    entity1.removeMetadata("sfe_slash_lasts", instance());
                    entity1.removeMetadata("sfe_slash_time", instance());
                    entity1.removeMetadata("sfe_slash_level", instance());
                    entity1.removeMetadata("sfe_slash", instance());
                }
//                else {
////                logger.log(Level.WARNING, "slash damage calculating");
//                    slashDamage += (int) (Math.min(
//                            entity1.getMetadata("sfe_slash_lasts").get(0).asLong()
//                            , currentTime - slash_time) / 1000) * mobTypeDamageMultiplier * Damage;
//                    int slash_multiplier = ((int) ((currentTime - slash_time) / slash_lasts)
//                            - ((int) (slash_time - slash_first_time) / slash_lasts));
//                    slashDamage *= slash_multiplier * 0.35 ;
////                    logger.log(Level.WARNING, String.valueOf(slashDamage));
////                    slashDamage *= realDamageMultiplier(entity1, slashDamage);
////                    logger.log(Level.WARNING, String.valueOf(slashDamage));
//                }
            }
//            logger.log(Level.WARNING, "D");
            double heat_damage = 0;
            if (entity1.getMetadata("sfe_heat").size() > 0) {
                long heat_time = entity1.getMetadata("sfe_heat_time").get(0).asLong();
                int heat_level = entity1.getMetadata("sfe_heat_level").get(0).asInt();

                if (entity1.getMetadata("sfe_heat_lasts").get(0).asLong()
                        < currentTime - heat_time) {
                    entity1.removeMetadata("sfe_heat_lasts", instance());
                    entity1.removeMetadata("sfe_heat_time", instance());
                    entity1.removeMetadata("sfe_heat_level", instance());
                    entity1.removeMetadata("sfe_heat", instance());
                } else {
                    heat_damage += 0.5 * Damage * heat_level * fireMODMultiplier * mobTypeDamageMultiplier;
                }
            }
//            logger.log(Level.WARNING, "E");

            if (entity1.getMetadata("sfe_viral").size() > 0) {
                long viral_time = entity1.getMetadata("sfe_viral_time").get(0).asLong();
                int viral_level = entity1.getMetadata("sfe_viral_level").get(0).asInt();
                int viral_lasts = entity1.getMetadata("sfe_viral_lasts").get(0).asInt();

                if (entity1.getMetadata("sfe_viral_lasts").get(0).asLong()
                        < currentTime - viral_time) {
                    entity1.removeMetadata("sfe_viral_lasts", instance());
                    entity1.removeMetadata("sfe_viral_time", instance());
                    entity1.removeMetadata("sfe_viral_first_time", instance());
                    entity1.removeMetadata("sfe_viral_level", instance());
                    entity1.removeMetadata("sfe_viral", instance());
                }
            }

            double toxin_damage = 0;
            if (entity1.getMetadata("sfe_toxin").size() > 0) {
                long toxin_time = entity1.getMetadata("sfe_toxin_time").get(0).asLong();
                int toxin_level = entity1.getMetadata("sfe_toxin_level").get(0).asInt();
                int toxin_lasts = entity1.getMetadata("sfe_toxin_lasts").get(0).asInt();

                if (entity1.getMetadata("sfe_toxin_lasts").get(0).asLong()
                        < currentTime - toxin_time) {
                    entity1.removeMetadata("sfe_toxin_lasts", instance());
                    entity1.removeMetadata("sfe_toxin_time", instance());
                    entity1.removeMetadata("sfe_toxin_first_time", instance());
                    entity1.removeMetadata("sfe_toxin_level", instance());
                    entity1.removeMetadata("sfe_toxin", instance());
                }
            }
//            logger.log(Level.WARNING, "F");
            double electricityDamage = 0;
            if (entity1.getMetadata("sfe_electricity").size() > 0) {
                long electricity_time = entity1.getMetadata("sfe_electricity_time").get(0).asLong();
                int electricity_level = entity1.getMetadata("sfe_electricity_level").get(0).asInt();

                if (entity1.getMetadata("sfe_electricity_lasts").get(0).asLong()
                        < currentTime - electricity_time) {
                    entity1.removeMetadata("sfe_electricity_lasts", instance());
                    entity1.removeMetadata("sfe_electricity_time", instance());
                    entity1.removeMetadata("sfe_electricity_level", instance());
                    entity1.removeMetadata("sfe_electricity", instance());
                } else {
                    electricityDamage += Damage * 0.5 * electricMODMultiplier * mobTypeDamageMultiplier;
                    if (
                            shooter
                                    instanceof Entity) {
                        for (Entity entity : entity1.getNearbyEntities(5, 5, 5)) {
                            if (entity instanceof Mob && !entity.equals(shooter)) {
                                for (int i = 1; i < 8; i++) {
                                    double finalElectricityDamage = electricityDamage;
                                    Scheduler.run(() ->
                                                    ((Mob) entity).damage(finalElectricityDamage,
//                                        (Entity) ((Projectile) e.getDamager()).getShooter()
                                                            (Entity) shooter)
                                    );
                                }
                            }
                        }
                    }
                }
            }
//            logger.log(Level.WARNING, "G");
            if (entity1.getMetadata("sfe_blast").size() > 0) {
                long blast_time = entity1.getMetadata("sfe_blast_time").get(0).asLong();
                int blast_level = entity1.getMetadata("sfe_blast_level").get(0).asInt();

                if (entity1.getMetadata("sfe_blast_lasts").get(0).asLong()
                        < currentTime - blast_time) {
                    entity1.removeMetadata("sfe_blast_lasts", instance());
                    entity1.removeMetadata("sfe_blast_time", instance());
                    entity1.removeMetadata("sfe_blast_level", instance());
                    entity1.removeMetadata("sfe_blast", instance());
                }
            }
//            logger.log(Level.WARNING, "H");
            double gas_damage = 0;
            if (entity1.getMetadata("sfe_gas").size() > 0) {
                long gas_time = entity1.getMetadata("sfe_gas_time").get(0).asLong();
                int gas_level = entity1.getMetadata("sfe_gas_level").get(0).asInt();
                int gas_lasts = entity1.getMetadata("sfe_gas_lasts").get(0).asInt();

                if (entity1.getMetadata("sfe_gas_lasts").get(0).asLong()
                        < currentTime - gas_time) {
                    entity1.removeMetadata("sfe_gas_lasts", instance());
                    entity1.removeMetadata("sfe_gas_time", instance());
                    entity1.removeMetadata("sfe_gas_level", instance());
                    entity1.removeMetadata("sfe_gas", instance());
                }
            }
//            logger.log(Level.WARNING, "I");
            if (entity1.getMetadata("sfe_radiation").size() > 0) {
                long radiation_time = entity1.getMetadata("sfe_radiation_time").get(0).asLong();
                int radiation_level = entity1.getMetadata("sfe_radiation_level").get(0).asInt();

                if (entity1.getMetadata("sfe_radiation_lasts").get(0).asLong()
                        < currentTime - radiation_time) {
                    entity1.removeMetadata("sfe_radiation_lasts", instance());
                    entity1.removeMetadata("sfe_radiation_time", instance());
                    entity1.removeMetadata("sfe_radiation_level", instance());
                    entity1.removeMetadata("sfe_radiation", instance());
                }
            }
//            logger.log(Level.WARNING, "J");
            if (entity1.getMetadata("sfe_corrosive").size() > 0) {
                long corrosive_time = entity1.getMetadata("sfe_corrosive_time").get(0).asLong();
                int corrosive_level = entity1.getMetadata("sfe_corrosive_level").get(0).asInt();

                if (entity1.getMetadata("sfe_corrosive_lasts").get(0).asLong()
                        < currentTime - corrosive_time) {
                    entity1.removeMetadata("sfe_corrosive_lasts", instance());
                    entity1.removeMetadata("sfe_corrosive_time", instance());
                    entity1.removeMetadata("sfe_corrosive_level", instance());
                    entity1.removeMetadata("sfe_corrosive", instance());
                } else {
                    Damage += (Damage * (realDamageMultiplier(entity1, Damage)) - Damage) * ((double) corrosive_level / 10);
                }
            }
//            logger.log(Level.WARNING, "K");
//            logger.log(Level.WARNING, String.valueOf(Damage));
//            entity1.setHealth(entity1.getHealth() - slashDamage - toxin_damage - gas_damage);
//            if (shooter instanceof Entity){
//                entity1.damage(0, (Entity) shooter);
//            }
//            return Damage + heat_damage + electricityDamage;
//            logger.log(Level.WARNING, String.valueOf(Damage));
            return new double[]{Damage, heat_damage, electricityDamage};
        } else {
            return new double[]{Damage, 0, 0};
        }


    }

    public static void AffectEffects(double[] damages, double[] ExtraCalculates, LivingEntity e, ProjectileSource shooter, Projectile projectile, double damage, double[] MODAdder) {
        double mobTypeMultiplier = 1;
        double statusChance = ExtraCalculates[2];
        double statusTime = ExtraCalculates[3];
        List<Integer> availableDamageType = new ArrayList<>();
        int ite = 0;

        for (int i = 1; i < damages.length; i++) {
            if (damages[i] > 0) {
                availableDamageType.add(i);
            }
        }
        if (availableDamageType.size() > 0) {
            double tempStatusChance = statusChance;
            while (true) {
                if (tempStatusChance > 0) {
                    if (tempStatusChance > 1) {
                        tempStatusChance--;
                        ite = random.nextInt(availableDamageType.size());
                        AffectEffect(damages[availableDamageType.get(ite)]
                                , statusChance
                                , statusTime
                                , e
                                , SFEEffects.values()[availableDamageType.get(ite) - 1].getPotionEffectType()
                                , shooter
                                , SFEEffects.values()[availableDamageType.get(ite) - 1].getSfeEffect()
                                , SFEEffects.values()[availableDamageType.get(ite) - 1].getMaxLevel()
                                , projectile
                                , damage
                                , mobTypeMultiplier
                                , MODAdder);

                    } else if (random.nextDouble() <= tempStatusChance) {
//                        logger.log(Level.WARNING, "5");
//                        AffectEffect(statusTime, e, potionEffectType, shooter, EffectString, maxLevel, projectile);
                        ite = random.nextInt(availableDamageType.size());
                        AffectEffect(damages[availableDamageType.get(ite)]
                                , statusChance
                                , statusTime
                                , e
                                , SFEEffects.values()[availableDamageType.get(ite) - 1].getPotionEffectType()
                                , shooter
                                , SFEEffects.values()[availableDamageType.get(ite) - 1].getSfeEffect()
                                , SFEEffects.values()[availableDamageType.get(ite) - 1].getMaxLevel()
                                , projectile
                                , damage
                                , mobTypeMultiplier
                                , MODAdder);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public static void AffectEffect(double damage, double statusChance, double statusTime, LivingEntity e, PotionEffectType potionEffectType, ProjectileSource shooter, String EffectString
            , int maxLevel, Projectile projectile, double totalDamage, double mobTypeMultiplier, double[] MODAdder) {
        AffectEffect(damage, statusTime, e, potionEffectType, shooter, EffectString, maxLevel, projectile, totalDamage, mobTypeMultiplier, MODAdder);
//        boolean returnValue = false;

//        return returnValue;
    }

    public static void AffectEffect(double effectDamage, double statusTime, LivingEntity entity, PotionEffectType potionEffectType, ProjectileSource shooter
            , String EffectString, int maxLevel, Projectile projectile
            , double damage, double mobTypeMultiplier, double[] MODAdder) {
        long currentTime = System.currentTimeMillis();
//        logger.log(Level.WARNING, EffectString);
        if (shooter instanceof Player) {
            ((Player) shooter).playNote(
                    ((Player) shooter).getLocation(),
                    Instrument.IRON_XYLOPHONE,
                    Note.natural(1, Note.Tone.F)
            );
//            bulletListener.
//            ((Player) shooter);
        }

        if (potionEffectType != null) {
            if (!potionEffectType.equals(PotionEffectType.HEALTH_BOOST) && !(potionEffectType.equals(PotionEffectType.ABSORPTION))) {
                if (entity.hasPotionEffect(potionEffectType)) {
                    {
                        if (entity.getPotionEffect(potionEffectType).getAmplifier() < maxLevel) {
                            entity.addPotionEffect(
                                    new PotionEffect(
                                            potionEffectType,
                                            (int) (statusTime / 50),
                                            entity.getPotionEffect(potionEffectType).getAmplifier() + 1
                                    ));
                        } else {
                            entity.addPotionEffect(
                                    new PotionEffect(
                                            potionEffectType,
                                            (int) (statusTime / 50),
                                            entity.getPotionEffect(potionEffectType).getAmplifier() + 1
                                    ));
                        }
                    }
                } else {
                    entity.addPotionEffect(
                            new PotionEffect(
                                    potionEffectType,
                                    (int) statusTime / 50,
                                    1
                            ));
                }
            } else {
                if (entity.hasPotionEffect(potionEffectType)) {
                    {
                        if (-entity.getPotionEffect(potionEffectType).getAmplifier() > -maxLevel) {
                            entity.addPotionEffect(
                                    new PotionEffect(
                                            potionEffectType,
                                            (int) (statusTime / 50),
                                            entity.getPotionEffect(potionEffectType).getAmplifier() - 1
                                    ));
                        } else {
                            entity.addPotionEffect(
                                    new PotionEffect(
                                            potionEffectType,
                                            (int) (statusTime / 50),
                                            entity.getPotionEffect(potionEffectType).getAmplifier() - 1
                                    ));
                        }
                    }
                } else {
                    entity.addPotionEffect(
                            new PotionEffect(
                                    potionEffectType,
                                    (int) statusTime / 50,
                                    -1
                            ));
                }
            }
        }

        if (EffectString.equals("sfe_heat")) {
            entity.setFireTicks(
                    Math.min(
                            (int) ((entity.getFireTicks() + statusTime) / 50),
                            (int) (statusTime * maxLevel / 50)
                    )
            );
        }

        if (EffectString.equals("sfe_toxin")) {
            for (int i = 2; i < 7; i++) {
                Scheduler.run(i * 20, () -> {
                    if (entity.getHealth() > 0) {
                        entity.setNoDamageTicks(0);
                        entity.setHealth(Math.max(0, entity.getHealth() - 0.5 * damage * mobTypeMultiplier * (1 + MODAdder[6])));
                        if (shooter instanceof Entity) {
                            entity.damage(0, (Entity) shooter);
                        }
                    }
                });
            }
        }

        if (EffectString.equals("sfe_slash")) {
            for (int i = 2; i < 7; i++) {
                Scheduler.run(i * 20, () -> {
                    if (entity.getHealth() > 0) {
                        entity.setNoDamageTicks(0);
                        entity.setHealth(Math.max(0, entity.getHealth() - 0.35 * damage * mobTypeMultiplier * (1 + MODAdder[1])));
                        if (shooter instanceof Entity) {
                            entity.damage(0, (Entity) shooter);
                        }
                    }
                });
            }
        }

        if (entity.getMetadata(EffectString).size() > 0) {

            if (entity.getMetadata(EffectString + "_time").get(0).asLong()
                    + entity.getMetadata(EffectString + "_lasts").get(0).asLong()
                    >= currentTime) {

                entity.setMetadata(EffectString + "_time", new FixedMetadataValue(
                        instance(),
                        currentTime));

                entity.setMetadata(EffectString + "_lasts", new FixedMetadataValue(
                        instance(),
                        statusTime / 50));

                if (entity.getMetadata(EffectString + "_level").get(0).asInt() < maxLevel) {
                    entity.setMetadata(EffectString + "_level", new FixedMetadataValue(
                            instance(),
                            1 + entity.getMetadata(EffectString + "_level").get(0).asInt()));
                }

            } else {
                entity.setMetadata(EffectString, new FixedMetadataValue(
                        instance(),
                        true)
                );

                entity.setMetadata(EffectString + "_time", new FixedMetadataValue(
                        instance(),
                        currentTime));

                entity.setMetadata(EffectString + "_lasts", new FixedMetadataValue(
                        instance(),
                        statusTime / 50));

                entity.setMetadata(EffectString + "_level", new FixedMetadataValue(
                        instance(),
                        1));
            }
        } else {
            entity.setMetadata(EffectString, new FixedMetadataValue(
                    instance(),
                    true)
            );

            entity.setMetadata(EffectString + "_time", new FixedMetadataValue(
                    instance(),
                    currentTime));

            entity.setMetadata(EffectString + "_lasts", new FixedMetadataValue(
                    instance(),
                    statusTime / 50));

            entity.setMetadata(EffectString + "_level", new FixedMetadataValue(
                    instance(),
                    1));
        }

        if (EffectString.equals("sfe_impact")) {
            entity.setVelocity(
                    entity.getVelocity()
                            .add(projectile.getVelocity().multiply(0.05).multiply(entity.getMetadata(EffectString + "_level").get(0).asInt()))
            );
        }

        if (EffectString.equals("sfe_blast")) {
            if (shooter instanceof Entity) {
                projectile.getWorld().createExplosion(projectile.getLocation(), entity.getMetadata(EffectString + "_level").get(0).asInt()
                        , false
                        , false
                        , (Entity) shooter);
            }
        }

        if (EffectString.equals("sfe_gas")) {
            double radius = 3 + 0.3 * entity.getMetadata("sfe_gas_level").get(0).asInt();
            for (Entity e1 : entity.getNearbyEntities(radius, radius, radius)) {
                if (e1 instanceof LivingEntity && !e1.equals(shooter)) {
                    for (int i = 1; i < 7; i++) {
                        Scheduler.run(i * 20, () -> {
                            if (((LivingEntity) e1).getHealth() > 0) {
                                ((LivingEntity) e1).setNoDamageTicks(0);
                                ((LivingEntity) e1).setHealth(Math.max(0, ((LivingEntity) e1).getHealth() - 0.5 * damage));
                                if (shooter instanceof Entity) {
                                    ((LivingEntity) e1).damage(0, (Entity) shooter);
                                }
                            }
                        });
                    }
                }
            }
        }

//        logger.log(Level.WARNING,EffectString);
//        if (EffectString.equals("sfe_gas")){
//            if (shooter instanceof Entity){
//                ThrownPotion thrownPotion = entity.launchProjectile(ThrownPotion.class);
//                thrownPotion.setShooter(shooter);
//                thrownPotion.setVelocity(new Vector(0,-0.1,0));
//                ItemStack GAS_CLOUD = new ItemStack(Material.LINGERING_POTION);
//                PotionMeta potionMeta = (PotionMeta) GAS_CLOUD.getItemMeta();
//                potionMeta.setColor(Color.fromRGB(0x006633));
//                potionMeta.addCustomEffect(
//                        new PotionEffect(PotionEffectType.HARM,
//                                120,
//                                1
//                        )
//                        , true
//                );
//                potionMeta.addCustomEffect(
//                        new PotionEffect(PotionEffectType.POISON,
//                                120,
//                                entity.getMetadata(EffectString + "_level").get(0).asInt()
//                                )
//                        , true
//                );
//                GAS_CLOUD.setItemMeta(potionMeta);
//                thrownPotion.setItem(GAS_CLOUD);
//                thrownPotion.teleport(entity.getLocation().add(0,1,0));
////                logger.log(Level.WARNING, "thrownPotion");
//            }
//        }

        if (EffectString.equals("sfe_radiation")) {
            if (!(entity instanceof Player) && (entity instanceof Mob)) {
                entity.addPotionEffect(new PotionEffect(
                        VersionedPotionEffectType.STRENGTH,
                        240,
                        entity.getMetadata(EffectString + "_level").get(0).asInt() + 1));
                List<Entity> entities = entity.getNearbyEntities(10, 10, 10);
                for (Entity entity1 : entities) {
                    if (entity1 instanceof LivingEntity && !(entity1 instanceof Player)) {
                        ((Mob) entity).setAware(true);
                        ((Mob) entity).setTarget((LivingEntity) entity1);
                        entity.attack(entity1);
                        break;
                    }
                }
            }
        }
    }

    public static double realDamageMultiplier(Entity entity, double Damage) {
        double realDamageMultiplier = 1;
        int armor = 0;
        int armorToughness = 0;
        double EPF = 0;

        if (entity instanceof Mob) {
//            entity = ((Mob) entity);
//            logger.log(Level.WARNING, "1");
            if (((Mob) entity).hasPotionEffect(VersionedPotionEffectType.RESISTANCE)) {
                realDamageMultiplier /= ((((Mob) entity).getPotionEffect(VersionedPotionEffectType.RESISTANCE).getAmplifier() + 1) * 0.2);
            }
            ;
//            logger.log(Level.WARNING, "2");
            if (((Mob) entity).getEquipment() != null) {
                ItemStack[] armors = ((Mob) entity).getEquipment().getArmorContents();
                for (ItemStack itemStack : armors) {
                    armor += getArmorFromArmor(itemStack);
                    armorToughness += getToughnessFromArmor(itemStack);
                    EPF += itemStack.getEnchantmentLevel(VersionedEnchantment.PROTECTION);
                }
                EPF = Math.min(20, EPF);
//                logger.log(Level.WARNING, "3");
                realDamageMultiplier /= (1 - (EPF / 25));
                realDamageMultiplier /= (1 - Math.min(20, Math.max(0.2 * armor, armor - (Damage / (2 + 0.25 * armorToughness)))) / 25);
            }

        }
//        logger.log(Level.WARNING, String.valueOf(realDamageMultiplier));
        return realDamageMultiplier;
    }

    ;

    public static int getArmorFromArmor(ItemStack armor) {
        if (armor == null) {
            return 0;
        } else {
            return switch (armor.getType()) {
                case DIAMOND_HELMET, DIAMOND_BOOTS, NETHERITE_HELMET, NETHERITE_BOOTS, GOLDEN_LEGGINGS, LEATHER_CHESTPLATE, LEATHER_HORSE_ARMOR ->
                        3;
                case DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE -> 8;
                case DIAMOND_LEGGINGS, NETHERITE_LEGGINGS, IRON_CHESTPLATE -> 6;
                case IRON_HELMET, IRON_BOOTS, CHAINMAIL_HELMET, GOLDEN_HELMET, LEATHER_LEGGINGS, TURTLE_HELMET -> 2;
                case IRON_LEGGINGS, CHAINMAIL_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_HORSE_ARMOR -> 5;
                case CHAINMAIL_LEGGINGS -> 4;
                case CHAINMAIL_BOOTS, GOLDEN_BOOTS, LEATHER_BOOTS, LEATHER_HELMET -> 1;
                case GOLDEN_HORSE_ARMOR -> 7;
                case DIAMOND_HORSE_ARMOR -> 11;
                default -> 0;

            };
        }
    }

    public static int getToughnessFromArmor(ItemStack armor) {
        if (armor == null) {
            return 0;
        } else {
            return switch (armor.getType()) {
                case DIAMOND_HELMET, DIAMOND_BOOTS, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS -> 2;
                case NETHERITE_HELMET, NETHERITE_BOOTS, NETHERITE_LEGGINGS, NETHERITE_CHESTPLATE -> 3;
                default -> 0;

            };
        }
    }

    public static void sendActionBar(Player player, String message) {
        if (placeholderAPIEnabled) {
            try {
                message = PlaceholderAPI.setPlaceholders(player, message);
            } catch (Throwable ignored) {
                placeholderAPIEnabled = false;
            }
        }
        if (ProtocolLibEnabled) {
            protocolLibSupport.sendLegacyActionBar(player, message);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    @EventHandler
    public void onEntityBulletHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Projectile)) return;

        Projectile bullet = (Projectile) e.getDamager();
        Entity shot = e.getEntity();
        if (bullet.hasMetadata("sfe_isGunBullet")) {
            if (bullet.getShooter() instanceof Player) {
                Player shooter = (Player) bullet.getShooter();
                if (!Slimefun.getProtectionManager().hasPermission(shooter, shot.getLocation(), Interaction.ATTACK_PLAYER)) {
                    return;
                }
            }
            double[] damages = new double[]{
                    0, //pure
                    0, 0, 0,//slash impact puncture
                    0, 0, 0, 0,//heat cold toxin electricity
                    0, 0, 0, 0, 0, 0//blast gas radiation viral magnetic corrosive
            };//for example

            String damageStr = bullet.getMetadata("sfe_damage").get(0).asString();
            String[] damageStrings = damageStr.substring(1, damageStr.length() - 1).split(",");
//            logger.log(Level.WARNING, Arrays.toString(damageStrings));

            for (int i = 0; i < damages.length; i++) {
                damages[i] = Double.parseDouble(damageStrings[i]);
            }

            double[] ExtraCalculates = new double[]{
                    0,
                    0,
                    0,
                    0,
                    0
            };//for example

            String ExtraCalculatesStr = bullet.getMetadata("sfe_ExtraCalculates").get(0).asString();
            String[] ExtraCalculateStrings = ExtraCalculatesStr.substring(1, ExtraCalculatesStr.length() - 1).split(",");

            for (int i = 0; i < ExtraCalculates.length; i++) {
                ExtraCalculates[i] = Double.parseDouble(ExtraCalculateStrings[i]);
            }

            double[] MODAdder = AbstractRangedWeapon.standardMODAdder.clone();

            String MODAdderStr = bullet.getMetadata("sfe_MODAdders").get(0).asString();
            String[] MODAdderStrings = MODAdderStr.substring(1, ExtraCalculatesStr.length() - 1).split(",");

            for (int i = 0; i < MODAdderStrings.length; i++) {
                MODAdder[i] = Double.parseDouble(MODAdderStrings[i]);
            }

//            logger.log(Level.WARNING, Arrays.toString(damages));
            double[] damage = CalculateDamage(damages, ExtraCalculates, e, MODAdder);
            if (e.getEntity() instanceof LivingEntity) {
                AffectEffects(damages, ExtraCalculates, ((LivingEntity) e.getEntity()), ((Projectile) e.getDamager()).getShooter(), (Projectile) e.getDamager(), damage[0], MODAdder);
                ((LivingEntity) e.getEntity()).setNoDamageTicks(0);
            }
//            if (e.getEntity() instanceof EnderDragon && e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Entity) {
//                ((EnderDragon) e.getEntity()).damage(damage[0] + damage[1] + damage[2], (Entity) ((Projectile) e.getDamager()).getShooter());
//            }
            e.setDamage(damage[0] + damage[1] + damage[2]);
//            logger.log(Level.WARNING, "2");

//            e.setDamage(CalculateDamage(damages, ExtraCalculates, e));


//            if (bullet instanceof ShulkerBullet && shot instanceof LivingEntity) {
//                Bukkit.getScheduler().runTaskLater(SlimeFrameExtension.instance(), () -> ((LivingEntity) shot).removePotionEffect(PotionEffectType.LEVITATION), 1);
//            }
//            else {
//                e.setCancelled(true);
//            }
        }
    }

    @EventHandler
    public void onBulletHit(ProjectileHitEvent e) {
        boolean punch = true;
        if (e.getEntity().hasMetadata("sfe_isGunBullet")) {
            {

                double ExplodeModifier = 1;
                Block b = e.getHitBlock();
                Projectile bullet = e.getEntity();

                double[] damages = new double[]{
                        0, //pure
                        0, 0, 0,//slash impact puncture
                        0, 0, 0, 0,//heat cold toxin electricity
                        0, 0, 0, 0, 0, 0//blast gas radiation viral magnetic corrosive
                };//for example

                String damageStr = bullet.getMetadata("sfe_damage").get(0).asString();
                String[] damageStrings = damageStr.substring(1, damageStr.length() - 1).split(",");
//            logger.log(Level.WARNING, Arrays.toString(damageStrings));

                for (int i = 0; i < damages.length; i++) {
                    damages[i] = Double.parseDouble(damageStrings[i]);
                }

                double[] ExtraCalculates = new double[]{
                        0,
                        0,
                        0,
                        0,
                        0,
                        0
                };//for example

                String ExtraCalculatesStr = bullet.getMetadata("sfe_ExtraCalculates").get(0).asString();
                String[] ExtraCalculateStrings = ExtraCalculatesStr.substring(1, ExtraCalculatesStr.length() - 1).split(",");

                for (int i = 0; i < ExtraCalculates.length; i++) {
                    ExtraCalculates[i] = Double.parseDouble(ExtraCalculateStrings[i]);
                }

                double[] MODAdder = AbstractRangedWeapon.standardMODAdder.clone();

                String MODAdderStr = bullet.getMetadata("sfe_MODAdders").get(0).asString();
                String[] MODAdderStrings = MODAdderStr.substring(1, MODAdderStr.length() - 1).split(",");
//                logger.log(Level.WARNING, MODAdderStr);
//                logger.log(Level.WARNING, Arrays.toString(MODAdderStrings));
                for (int i = 0; i < MODAdderStrings.length; i++) {
                    MODAdder[i] = Double.parseDouble(MODAdderStrings[i]);
                }


                if (ExtraCalculates[5] > 0) {
                    bullet.getWorld().createExplosion(bullet.getLocation()
                            , (float) ((float) ExtraCalculates[5] * ExplodeModifier)
                            , false
                            , false);
                    punch = false;
                    double[] damage = CalculateDamage(damages, ExtraCalculates, bullet.getShooter(), e.getHitEntity(), MODAdder);
                    if (bullet.getShooter() instanceof Entity) {
                        for (Entity e1 : e.getEntity().getNearbyEntities(
                                ExtraCalculates[5] * ExplodeModifier * 2,
                                ExtraCalculates[5] * ExplodeModifier * 2,
                                ExtraCalculates[5] * ExplodeModifier * 2)) {
                            if (e1 instanceof LivingEntity) {
                                AffectEffects(damages, ExtraCalculates, (LivingEntity) e1, bullet.getShooter(), bullet, damage[0], MODAdder);
                                ((LivingEntity) e1).damage(damage[0], (Entity) bullet.getShooter());
                                ((LivingEntity) e1).setNoDamageTicks(0);
                            }
                        }
                    }
                }

                //punching through
                if (ExtraCalculates[4] > 0 && e.getEntity().getShooter() != null && punch) {
                    List<Double> getPunchLocation = punchLocation(bullet.getLocation(), ExtraCalculates[4]);

                    bullet.teleport(new Location(bullet.getWorld()
                            , getPunchLocation.get(0)
                            , getPunchLocation.get(1)
                            , getPunchLocation.get(2)
                    ));

                    if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity) {
                        double[] damage = CalculateDamage(damages, ExtraCalculates, bullet.getShooter(), e.getHitEntity(), MODAdder);
                        AffectEffects(damages, ExtraCalculates, ((LivingEntity) e.getHitEntity()), bullet.getShooter(), bullet, damage[0], MODAdder);
                        ((LivingEntity) e.getHitEntity()).setNoDamageTicks(0);
                        if (bullet.getShooter() instanceof Entity) {
                            ((LivingEntity) e.getHitEntity()).damage(damage[0] + damage[1], (Entity) bullet.getShooter());
                        }
                    }


                    bullet.removeMetadata(
                            "sfe_ExtraCalculates"
                            , instance());

                    bullet.setMetadata("sfe_ExtraCalculates",
                            new FixedMetadataValue(
                                    instance()
                                    , Arrays.toString(new double[]{
                                            ExtraCalculates[0]
                                            , ExtraCalculates[1]
                                            , ExtraCalculates[2]
                                            , ExtraCalculates[3]
                                            , getPunchLocation.get(3)
                                            , ExtraCalculates[5]
                                    }
                            )));

                    e.setCancelled(true);
                }

            }
        }
    }

    public static enum SFEEffects {
        SLASH(1, null, "sfe_slash", 10),
        IMPACT(2, null, "sfe_impact", 10),
        PUNCTURE(3, PotionEffectType.WEAKNESS, "sfe_puncture", 10),
        HEAT(4, null, "sfe_heat", 10),
        COLD(5, VersionedPotionEffectType.SLOWNESS, "sfe_cold", 10),
        TOXIN(6, PotionEffectType.POISON, "sfe_toxin", 10),
        ELECTRICITY(7, null, "sfe_electricity", 10),
        BLAST(8, null, "sfe_blast", 10),
        GAS(9, PotionEffectType.POISON, "sfe_gas", 10),
        RADIATION(10, VersionedPotionEffectType.NAUSEA, "sfe_radiation", 10),
        VIRAL(11, null, "sfe_viral", 10),
        MAGNETIC(12, PotionEffectType.ABSORPTION, "sfe_magnetic", 10),
        CORROSIVE(13, null, "sfe_corrosive", 10);

        public final int ite;
        public final PotionEffectType potionEffectType;
        public final String sfeEffect;
        public final int maxLevel;

        SFEEffects(int i, PotionEffectType potionEffectType, String sfeEffect, int maxLevel) {
            this.ite = i;
            this.potionEffectType = potionEffectType;
            this.sfeEffect = sfeEffect;
            this.maxLevel = maxLevel;
        }

        public int getIte() {
            return ite;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public PotionEffectType getPotionEffectType() {
            return potionEffectType;
        }

        public String getSfeEffect() {
            return sfeEffect;
        }
    }
}