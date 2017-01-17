package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.api.crafting.recipes.ISawmillRecipe;
import cofh.thermalexpansion.init.TEItemsOld;
import gnu.trove.map.hash.THashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SawmillManager {

	private static Map<ComparableItemStackSawmill, RecipeSawmill> recipeMap = new THashMap<ComparableItemStackSawmill, RecipeSawmill>();
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 1600;

	private static float logMultiplier = 1.5F;

	static {
		allowOverwrite = ThermalExpansion.CONFIG.get("RecipeManagers.Sawmill", "AllowRecipeOverwrite", false);

		String category = "RecipeManagers.Sawmill.Log";
		String comment = "This sets the default rate for Log->Plank conversion. This number is used in all automatically generated recipes.";
		logMultiplier = MathHelper.clamp((float) ThermalExpansion.CONFIG.get(category, "DefaultMultiplier", logMultiplier, comment), 1F, 64F);
	}

	public static RecipeSawmill getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		ComparableItemStackSawmill query = new ComparableItemStackSawmill(input);

		RecipeSawmill recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeSawmill[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeSawmill[0]);
	}

	public static void addDefaultRecipes() {

		/*
		 * For future recipes, we convert sticks to sawdust at a rate of 4 sticks per sawdust,
		 * so 1 stick is a 12.5% chance for 1 sawdust, and 8 is a 100% chance for 2 sawdust
		 */

		addRecipe(2400, new ItemStack(Blocks.CHEST), new ItemStack(Blocks.PLANKS, 8));
		addRecipe(2400, new ItemStack(Blocks.CRAFTING_TABLE), new ItemStack(Blocks.PLANKS, 4));
		addRecipe(2400, new ItemStack(Items.SIGN), new ItemStack(Blocks.PLANKS, 2));

		addRecipe(2400, new ItemStack(Items.OAK_DOOR), new ItemStack(Blocks.PLANKS, 6, 0));
		addRecipe(2400, new ItemStack(Items.SPRUCE_DOOR), new ItemStack(Blocks.PLANKS, 6, 1));
		addRecipe(2400, new ItemStack(Items.BIRCH_BOAT), new ItemStack(Blocks.PLANKS, 6, 2));
		addRecipe(2400, new ItemStack(Items.JUNGLE_DOOR), new ItemStack(Blocks.PLANKS, 6, 3));
		addRecipe(2400, new ItemStack(Items.ACACIA_DOOR), new ItemStack(Blocks.PLANKS, 6, 4));
		addRecipe(2400, new ItemStack(Items.DARK_OAK_DOOR), new ItemStack(Blocks.PLANKS, 6, 5));

		addRecipe(2400, new ItemStack(Blocks.WOODEN_PRESSURE_PLATE), new ItemStack(Blocks.PLANKS, 2));
		addRecipe(2400, new ItemStack(Blocks.TRAPDOOR), new ItemStack(Blocks.PLANKS, 3));

		addRecipe(2400, new ItemStack(Blocks.OAK_FENCE_GATE), new ItemStack(Blocks.PLANKS, 2, 0), TEItemsOld.sawdust);
		addRecipe(2400, new ItemStack(Blocks.SPRUCE_FENCE_GATE), new ItemStack(Blocks.PLANKS, 2, 1), TEItemsOld.sawdust);
		addRecipe(2400, new ItemStack(Blocks.BIRCH_FENCE_GATE), new ItemStack(Blocks.PLANKS, 2, 2), TEItemsOld.sawdust);
		addRecipe(2400, new ItemStack(Blocks.JUNGLE_FENCE_GATE), new ItemStack(Blocks.PLANKS, 2, 3), TEItemsOld.sawdust);
		addRecipe(2400, new ItemStack(Blocks.ACACIA_FENCE_GATE), new ItemStack(Blocks.PLANKS, 2, 4), TEItemsOld.sawdust);
		addRecipe(2400, new ItemStack(Blocks.DARK_OAK_FENCE_GATE), new ItemStack(Blocks.PLANKS, 2, 5), TEItemsOld.sawdust);

		addRecipe(800, new ItemStack(Blocks.WOODEN_BUTTON), new ItemStack(Blocks.PLANKS, 1));
		addRecipe(1200, new ItemStack(Items.BOWL, 2), new ItemStack(Blocks.PLANKS, 1), TEItemsOld.sawdust, 12);
		addRecipe(2400, new ItemStack(Items.BED), new ItemStack(Blocks.PLANKS, 3), new ItemStack(Blocks.WOOL, 3));
		addRecipe(2400, new ItemStack(Blocks.BOOKSHELF), new ItemStack(Blocks.PLANKS, 6), new ItemStack(Items.BOOK, 3));
		addRecipe(2400, new ItemStack(Blocks.NOTEBLOCK), new ItemStack(Blocks.PLANKS, 8), new ItemStack(Items.REDSTONE, 1));
		addRecipe(2400, new ItemStack(Blocks.JUKEBOX), new ItemStack(Blocks.PLANKS, 8), new ItemStack(Items.DIAMOND, 1));

		addRecipe(2400, new ItemStack(Items.BOAT), new ItemStack(Blocks.PLANKS, 5));
		addRecipe(1600, new ItemStack(Items.WOODEN_SWORD), new ItemStack(Blocks.PLANKS, 1), TEItemsOld.sawdust, 12);
		addRecipe(1600, new ItemStack(Items.WOODEN_SHOVEL), new ItemStack(Blocks.PLANKS, 1), TEItemsOld.sawdust, 25);
		addRecipe(1600, new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(Blocks.PLANKS, 2), TEItemsOld.sawdust, 25);
		addRecipe(1600, new ItemStack(Items.WOODEN_AXE), new ItemStack(Blocks.PLANKS, 2), TEItemsOld.sawdust, 25);
		addRecipe(1600, new ItemStack(Items.WOODEN_HOE), new ItemStack(Blocks.PLANKS, 1), TEItemsOld.sawdust, 25);

		addRecipe(3200, new ItemStack(Blocks.OAK_STAIRS, 2), new ItemStack(Blocks.PLANKS, 3, 0));
		addRecipe(3200, new ItemStack(Blocks.SPRUCE_STAIRS, 2), new ItemStack(Blocks.PLANKS, 3, 1));
		addRecipe(3200, new ItemStack(Blocks.BIRCH_STAIRS, 2), new ItemStack(Blocks.PLANKS, 3, 2));
		addRecipe(3200, new ItemStack(Blocks.JUNGLE_STAIRS, 2), new ItemStack(Blocks.PLANKS, 3, 3));
		addRecipe(3200, new ItemStack(Blocks.ACACIA_STAIRS, 2), new ItemStack(Blocks.PLANKS, 3, 4));
		addRecipe(3200, new ItemStack(Blocks.DARK_OAK_STAIRS, 2), new ItemStack(Blocks.PLANKS, 3, 5));

		addRecipe(800, new ItemStack(Blocks.MELON_BLOCK), new ItemStack(Items.MELON, 9));
		addRecipe(800, new ItemStack(Blocks.LEVER), new ItemStack(Blocks.COBBLESTONE, 1), TEItemsOld.sawdust, 12); // yeah. well. fun on resource limited maps?
		addRecipe(800, new ItemStack(Blocks.REDSTONE_TORCH), new ItemStack(Items.REDSTONE, 1), TEItemsOld.sawdust, 12);
		addRecipe(800, new ItemStack(Items.PAINTING), new ItemStack(Blocks.WOOL, 1), ItemHelper.cloneStack(TEItemsOld.sawdust, 2));
		addRecipe(800, new ItemStack(Items.ITEM_FRAME), new ItemStack(Items.LEATHER, 1), ItemHelper.cloneStack(TEItemsOld.sawdust, 2));
		addRecipe(1600, new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.LEATHER, 3), new ItemStack(Items.LEATHER, 1), 10);
		addRecipe(2400, new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.LEATHER, 6), new ItemStack(Items.LEATHER, 1), 25);
		addRecipe(2000, new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.LEATHER, 5), new ItemStack(Items.LEATHER, 1), 15);
		addRecipe(1600, new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Items.LEATHER, 3), new ItemStack(Items.LEATHER, 1), 5);
	}

	public static void loadRecipes() {

		addAllLogs();

		if (ItemHelper.oreNameExists("woodRubber")) {
			if (ItemHelper.oreNameExists("itemRawRubber")) {
				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.PLANKS, 5, 3), OreDictionary.getOres("itemRawRubber").get(0), 50);
			} else if (ItemHelper.oreNameExists("itemRubber")) {
				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.PLANKS, 5, 3), OreDictionary.getOres("itemRubber").get(0), 50);
			} else {
				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.PLANKS, 5, 3));
			}
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackSawmill, RecipeSawmill> tempMap = new THashMap<ComparableItemStackSawmill, RecipeSawmill>(recipeMap.size());
		RecipeSawmill tempRecipe;

		for (Entry<ComparableItemStackSawmill, RecipeSawmill> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSawmill(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input == null || primaryOutput == null || energy <= 0) {
			return false;
		}
		RecipeSawmill recipe = new RecipeSawmill(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackSawmill(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

		if (input == null || primaryOutput == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(input)) {
			return false;
		}
		RecipeSawmill recipe = new RecipeSawmill(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackSawmill(input), recipe);
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackSawmill(input)) != null;
	}

	/* HELPER FUNCTIONS */
	public static void addAllLogs() {

		Container tempContainer = new Container() {

			@Override
			public boolean canInteractWith(EntityPlayer player) {

				return false;
			}

		};
		InventoryCrafting tempCrafting = new InventoryCrafting(tempContainer, 3, 3);

		for (int i = 0; i < 9; i++) {
			tempCrafting.setInventorySlotContents(i, null);
		}
		List<ItemStack> registeredOres = OreDictionary.getOres("logWood");
		for (int i = 0; i < registeredOres.size(); i++) {
			ItemStack logEntry = registeredOres.get(i);

			if (ItemHelper.getItemDamage(logEntry) == OreDictionary.WILDCARD_VALUE) {
				for (int j = 0; j < 16; j++) {
					ItemStack log = ItemHelper.cloneStack(logEntry, 1);
					log.setItemDamage(j);
					tempCrafting.setInventorySlotContents(0, log);
					ItemStack resultEntry = ItemHelper.findMatchingRecipe(tempCrafting, null);

					if (resultEntry != null) {
						ItemStack result = resultEntry.copy();
						result.stackSize *= logMultiplier;
						addRecipe(800, log, result, TEItemsOld.sawdust);
					}
				}
			} else {
				ItemStack log = ItemHelper.cloneStack(logEntry, 1);
				tempCrafting.setInventorySlotContents(0, log);
				ItemStack resultEntry = ItemHelper.findMatchingRecipe(tempCrafting, null);

				if (resultEntry != null) {
					ItemStack result = resultEntry.copy();
					result.stackSize *= logMultiplier;
					addRecipe(800, log, result, TEItemsOld.sawdust);
				}
			}
		}
	}

	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addTERecipe(energy, input, primaryOutput, null, 0);
	}

	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addTERecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, null, 0, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, false);
	}

	/* RECIPE CLASS */
	public static class RecipeSawmill implements ISawmillRecipe {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		RecipeSawmill(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.input = input;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;

			if (input.stackSize <= 0) {
				input.stackSize = 1;
			}
			if (primaryOutput.stackSize <= 0) {
				primaryOutput.stackSize = 1;
			}
			if (secondaryOutput != null && secondaryOutput.stackSize <= 0) {
				secondaryOutput.stackSize = 1;
			}
		}

		@Override
		public ItemStack getInput() {

			return input.copy();
		}

		@Override
		public ItemStack getPrimaryOutput() {

			return primaryOutput.copy();
		}

		@Override
		public ItemStack getSecondaryOutput() {

			if (secondaryOutput == null) {
				return null;
			}
			return secondaryOutput.copy();
		}

		@Override
		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		@Override
		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackSawmill extends ComparableItemStack {

		static final String ORE = "ore";
		static final String INGOT = "ingot";
		static final String NUGGET = "nugget";

		static final String RUBBER = "woodRubber";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(ORE) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET) || oreName.equals(RUBBER);
		}

		public static int getOreID(ItemStack stack) {

			ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

			if (ids != null) {
				for (int i = 0, e = ids.size(); i < e; ) {
					int id = ids.get(i++);
					if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
						return id;
					}
				}
			}
			return -1;
		}

		public static int getOreID(String oreName) {

			if (!safeOreType(oreName)) {
				return -1;
			}
			return ItemHelper.oreProxy.getOreID(oreName);
		}

		public ComparableItemStackSawmill(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		public ComparableItemStackSawmill(Item item, int damage, int stackSize) {

			super(item, damage, stackSize);
			this.oreID = getOreID(this.toItemStack());
		}

		@Override
		public ComparableItemStackSawmill set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
