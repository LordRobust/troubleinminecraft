package io.indices.troubleinminecraft.shop.items;


import io.indices.troubleinminecraft.abilities.DisguiserAbility;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import org.bukkit.Material;

public class Disguiser extends ShopItem {
    public Disguiser() {
        name = "Disguiser";
        cost = 2;
        itemStack = new ItemBuilder(Material.SKULL).amount(1).name("Disguiser").lore("Disguise as a random player.").lore("Useful for when your cover is blown!").build();
        addAbility(DisguiserAbility.class);
    }
}
