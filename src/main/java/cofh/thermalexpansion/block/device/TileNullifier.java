package cofh.thermalexpansion.block.device;

import cofh.core.CoFHProps;
import cofh.core.render.IconRegistry;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.device.BlockDevice.Types;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.gui.client.device.GuiNullifier;
import cofh.thermalexpansion.gui.container.device.ContainerNullifier;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class TileNullifier extends TileDeviceBase {

	public static void initialize() {

		int type = BlockDevice.Types.NULLIFIER.ordinal();

		defaultSideConfig[type] = new SideConfig();
		defaultSideConfig[type].numConfig = 2;
		defaultSideConfig[type].slotGroups = new int[][] { {}, { 0 }, {} };
		defaultSideConfig[type].allowInsertionSide = new boolean[] { false, false, false };
		defaultSideConfig[type].allowExtractionSide = new boolean[] { false, false, false };
		defaultSideConfig[type].allowInsertionSlot = new boolean[] { true };
		defaultSideConfig[type].allowExtractionSlot = new boolean[] { false };
		defaultSideConfig[type].sideTex = new int[] { 0, 1, 4 };
		defaultSideConfig[type].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileNullifier.class, "thermalexpansion.Nullifier");
	}

	protected static final int[] SLOTS = { 0 };
	protected static final Fluid renderFluid = FluidRegistry.LAVA;

	public TileNullifier() {

		super(Types.NULLIFIER);
		inventory = new ItemStack[1];
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing] = 1;
	}

	@Override
	public int getLightValue() {

		return FluidHelper.getFluidLuminosity(renderFluid);
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	protected boolean isSideAccessible(EnumFacing side) {

		return sideCache[side.ordinal()] == 1 && redstoneControlOrDisable();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiNullifier(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerNullifier(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readInventoryFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public void writeInventoryToNBT(NBTTagCompound nbt) {

	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return super.hasCapability(capability, facing) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new net.minecraftforge.fluids.capability.IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					return new IFluidTankProperties[] { new FluidTankProperties(null, Integer.MAX_VALUE, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return isSideAccessible(facing) ? resource.amount : 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return null;
				}
			});
		}
		return super.getCapability(capability, facing);
	}

	/* IInventory */
	@Override
	public ItemStack getStackInSlot(int slot) {

		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (inventory[slot] == null) {
			return null;
		}
		if (inventory[slot].stackSize <= amount) {
			amount = inventory[slot].stackSize;
		}
		ItemStack stack = inventory[slot].splitStack(amount);

		if (inventory[slot].stackSize <= 0) {
			inventory[slot] = null;
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {

		if (inventory[slot] == null) {
			return null;
		}
		ItemStack stack = inventory[slot];
		inventory[slot] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (slot == 0) {
			return;
		}
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 1;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : redstoneControlOrDisable() ? RenderHelper.getFluidTexture(renderFluid) : IconRegistry.getIcon("DeviceFace", type);
		} else if (side < 6) {
			return side != facing ? IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]) : redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive", type) : IconRegistry.getIcon("DeviceFace", type);
		}
		return IconRegistry.getIcon("DeviceSide");
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return isSideAccessible(side) ? SLOTS : CoFHProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return isSideAccessible(side);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		return false;
	}

}
