package thermalexpansion.block.strongbox;

import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.item.ItemStack;

public class TileStrongboxCreative extends TileStrongbox {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongboxCreative.class, "thermalexpansion.StrongboxCreative");
	}

	public TileStrongboxCreative() {

	}

	public TileStrongboxCreative(int metadata) {

		super(metadata);
	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		return ItemHelper.cloneStack(inventory[slot], amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (stack == null) {
			return;
		}
		inventory[slot] = stack;
		inventory[slot].stackSize = stack.getMaxStackSize();
	}

}
