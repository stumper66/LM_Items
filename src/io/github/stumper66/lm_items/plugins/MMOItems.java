package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.github.stumper66.lm_items.SupportsExtras;
import io.github.stumper66.lm_items.Utils;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.manager.TierManager;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class MMOItems implements ItemsAPI, SupportsExtras {
    public MMOItems() {
        checkDeps();
        this.supportedTypes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (this.isInstalled)
            buildSupportedItemTypes();
    }

    private boolean isInstalled;
    private final Set<String> supportedTypes;

    private void checkDeps(){
        for (final String dep : List.of(getName(), "MythicLib")){
            if (Bukkit.getPluginManager().getPlugin(dep) == null)
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName(){
        return "MMOItems";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(this.isInstalled);

        if (!result.pluginIsInstalled)
            return result;

        final List<ItemStack> items = processItems(itemRequest);
        int minItems;
        int maxItems;

        minItems = Math.max(itemRequest.minItems, 1);
        maxItems = Math.max(itemRequest.maxItems, minItems);

        for (final ItemStack item : items) {
            if (itemRequest.amount != null) {
                final double useAmount = itemRequest.amount;
                item.setAmount((int) useAmount);
            }
        }

        if (!items.isEmpty()){
            if (items.size() > 1) {
                Collections.shuffle(items);
                minItems = Math.min(minItems, items.size());
                maxItems = Math.min(maxItems, items.size());

                final int numberToUse = minItems != maxItems ?
                        ThreadLocalRandom.current().nextInt(minItems, maxItems + 1) :
                        maxItems;

                result.itemStacks = new LinkedList<>();
                int count = 0;
                for (final ItemStack itemStack : items){
                    result.itemStacks.add(itemStack);
                    count++;
                    if (count >= numberToUse) break;
                }
            }
            // this is for backwards compatibility
            result.itemStack = items.get(0);
        }

        return result;
    }

    private @NotNull List<ItemStack> processItems(final @NotNull ExternalItemRequest itemRequest){
        final List<ItemStack> results = new LinkedList<>();
        final net.Indyuce.mmoitems.MMOItems plugin = net.Indyuce.mmoitems.MMOItems.plugin;
        final TierManager tiers = net.Indyuce.mmoitems.MMOItems.plugin.getTiers();
        boolean getSingleItem = true;
        int itemLevel = 0;
        String itemTier = null;
        if (itemRequest.extras != null){
            final Integer itemLevelInt = Utils.getIntNValue(itemRequest.extras, "ItemLevel");
            if (itemLevelInt != null)
                itemLevel = itemLevelInt;
            else{
                final Double itemLevelDbl = Utils.getDblNValue(itemRequest.extras, "ItemLevel");
                if (itemLevelDbl != null)
                    itemLevel = (int) Math.round(itemLevelDbl);
            }

            if (itemRequest.extras.containsKey("ItemTier"))
                itemTier = Utils.getStringValue(itemRequest.extras, "ItemTier");
        }

        if (itemRequest.getMultipleItems){
            getSingleItem = false;
            final net.Indyuce.mmoitems.api.Type itemType = plugin.getTypes().get(itemRequest.itemType);
            if (itemType == null){
                return results;
            }

            final Collection<MMOItemTemplate> col = plugin.getTemplates().getTemplates(itemType);
            for (final MMOItemTemplate mmoItemTemplate : col){
                final ItemTier useTier = itemTier != null ?
                        tiers.get(itemTier) : generateRandomTier(tiers);

                final MMOItemBuilder builder = mmoItemTemplate.newBuilder(itemLevel, useTier);
                final MMOItem mmoItem = builder.build();

                if (checkFilterCriteria(mmoItem.getId(), itemRequest)) {
                    final ItemStack result = mmoItem.newBuilder().build();
                    if (result != null) results.add(result);
                }
            }
        }

        if (getSingleItem){
            final ItemTier useTier = itemTier != null ?
                    tiers.get(itemTier) : generateRandomTier(tiers);

            final MMOItem mmoitem = plugin.getMMOItem(
                    plugin.getTypes().get(itemRequest.itemType),
                    itemRequest.itemId,
                    itemLevel,
                    useTier
            );

            if (mmoitem != null) results.add(mmoitem.newBuilder().build());
        }

        return results;
    }

    private ItemTier generateRandomTier(final @NotNull TierManager tiers) {
        double total = 0.0;

        for (ItemTier it : tiers.getAll()) {
            total += it.getGenerationChance();
        }

        double rand = Math.random() * total;

        for (ItemTier tier: tiers.getAll()) {
            if (rand < tier.getGenerationChance()) {
                return tier;
            }

            rand -= tier.getGenerationChance();
        }

        return tiers.get("COMMON");
    }

    private boolean checkFilterCriteria(final @NotNull String itemId, final @NotNull ExternalItemRequest itemRequest){
        if ((itemRequest.excludedList == null || itemRequest.excludedList.isEmpty()) &&
                (itemRequest.allowedList == null || itemRequest.allowedList.isEmpty())) return true;

        if (itemRequest.excludedList != null){
            for (final String excludeWord : itemRequest.excludedList){
                if (excludeWord.contains("*")){
                    // wildcard search
                    final String itemIdLower = itemId.toLowerCase();
                    if (checkWildcardMatch(itemIdLower, excludeWord.toLowerCase()))
                        return false;
                }
                else{
                    if (itemId.equalsIgnoreCase(excludeWord)) return false;
                }
            }
        }

        if (itemRequest.allowedList != null){
            for (final String allowedWord : itemRequest.allowedList){
                if (allowedWord.contains("*")){
                    // wildcard search
                    final String itemIdLower = itemId.toLowerCase();
                    if (!checkWildcardMatch(itemIdLower, allowedWord.toLowerCase()))
                        return true;
                }
                else{
                    if (itemId.equalsIgnoreCase(allowedWord)) return true;
                }
            }
        }

        return true;
    }

    private boolean checkWildcardMatch(final @NotNull String itemIdL, final @NotNull String criteriaL){
        boolean hasStart = false;
        boolean hasEnd = false;
        String searchWord = criteriaL;
        if (criteriaL.startsWith("*")){
            hasStart = true;
            searchWord = searchWord.substring(1);
        }
        if (criteriaL.endsWith("*")){
            hasEnd = true;
            searchWord = searchWord.substring(0, searchWord.length() - 1);
        }

        if (hasStart && hasEnd){
            return !itemIdL.contains(searchWord);
        }
        else if (hasStart){
            return !itemIdL.endsWith(searchWord);
        }
        else {
            return !itemIdL.startsWith(searchWord);
        }
    }

    private void buildSupportedItemTypes(){
        final net.Indyuce.mmoitems.MMOItems mmoItems = net.Indyuce.mmoitems.MMOItems.plugin;

        final List<String> names = new ArrayList<>(mmoItems.getTypes().getAll().size());
        for (final net.Indyuce.mmoitems.api.Type type : mmoItems.getTypes().getAll())
            this.supportedTypes.add(type.toString());

        Collections.sort(names);
        this.supportedTypes.addAll(names);
    }

    public @NotNull Collection<String> getItemTypes() {
        if (this.isInstalled)
            return this.supportedTypes;
        else
            return Collections.emptyList();
    }

    public @NotNull Collection<String> getSupportedExtras(){
        return List.of("ItemLevel");
    }
}
