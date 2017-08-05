package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class CreeperEggAbility extends Ability {
    public static ItemStack itemStack = new ItemBuilder(Material.MONSTER_EGG).meta((itemMeta) -> ((SpawnEggMeta) itemMeta).setSpawnedType(EntityType.CREEPER))
            .name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_CREEPER_EGG_TITLE))).lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_CREEPER_EGG_LORE))).build();

    public CreeperEggAbility(User user) {
        super(user);
    }

    @Override
    public void start() {
        affected.getPlayer().getInventory().addItem(itemStack);
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @EventHandler
    public void firedArrowLands(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            if (arrow.getShooter() instanceof Player && ((Player) arrow.getShooter()).getUniqueId().equals(affected.getUuid())) {
                if (affected.getPlayer().getInventory().contains(itemStack)) {
                    Location spawnLoc;

                    if (event.getHitBlock() != null) {
                        spawnLoc = event.getHitBlock().getLocation();
                    } else {
                        spawnLoc = event.getHitEntity().getLocation();
                    }

                    for (ItemStack stack : affected.getPlayer().getInventory().getContents()) {
                        if (stack == itemStack) {
                            if (stack.getAmount() == 1) {
                                affected.getPlayer().getInventory().remove(itemStack);
                            } else {
                                stack.setAmount(stack.getAmount() - 1);
                            }
                        }
                    }

                    Bukkit.getWorld(spawnLoc.getWorld().getName()).spawnEntity(spawnLoc, EntityType.CREEPER);
                }
            }
        }
    }
}
