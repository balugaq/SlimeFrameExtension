package io.github.acdeasdff.SlimeFrameExtension.Items.Abstracts;

import io.github.acdeasdff.SlimeFrameExtension.ItemMetaRelated.DataTypeMethods;
import io.github.acdeasdff.SlimeFrameExtension.ItemMetaRelated.Keys;
import io.github.acdeasdff.SlimeFrameExtension.ItemMetaRelated.PersistentSFEPotatoableType;
import io.github.acdeasdff.SlimeFrameExtension.Items.Instance.PotatoableInstance;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class AbstractWeapons extends AbstractModifiableItem {
    public AbstractWeapons(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        ItemMeta itemMeta = item.getItemMeta();
        DataTypeMethods.setCustom(itemMeta, Keys.POTATOABLE_INSTANCE, PersistentSFEPotatoableType.TYPE, new PotatoableInstance(false, "blue"));
        item.setItemMeta(itemMeta);
    }

    @NotNull
    @Override
    public ItemUseHandler getItemHandler() {
        return null;
    }


}
