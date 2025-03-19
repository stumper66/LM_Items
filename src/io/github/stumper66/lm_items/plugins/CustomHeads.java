package io.github.stumper66.lm_items.plugins;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.github.stumper66.lm_items.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@SuppressWarnings("unused")
public class CustomHeads implements ItemsAPI {
    public boolean getIsInstalled() {
        return true;
    }

    public @NotNull String getName(){
        return "CustomHeads";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());

        if (itemRequest.extras != null) {
            final String idStr = Utils.getStringValue(itemRequest.extras, "id");
            final UUID id = idStr != null ? UUID.fromString(idStr) : UUID.randomUUID();
            final String name = Utils.getStringValue(itemRequest.extras, "name");
            final String useName = name != null ? name : id.toString().substring(0, 16);
            final ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            playerHead.editMeta(SkullMeta.class, skullMeta -> {
                final PlayerProfile profile = Bukkit.createProfile(id, useName);
                skullMeta.setPlayerProfile(profile);
            });
            result.itemStack = playerHead;
        }

        return result;
    }

    @Override
    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
