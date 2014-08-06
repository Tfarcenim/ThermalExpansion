package thermalexpansion.block.dynamo;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.client.dynamo.GuiDynamoMagmatic;
import thermalexpansion.gui.container.ContainerTEBase;

public class TileDynamoMagmatic extends TileDynamoBase implements IFluidHandler {

	static final int TYPE = BlockDynamo.Types.MAGMATIC.ordinal();

	public static void initialize() {

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Dynamo.Magmatic.BasePower", 80), 10, 160);
		ThermalExpansion.config.set("block.tweak", "Dynamo.Magmatic.BasePower", maxPower);
		maxPower /= 10;
		maxPower *= 10;
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsDefault(maxPower);

		GameRegistry.registerTileEntity(TileDynamoMagmatic.class, "thermalexpansion.DynamoMagmatic");
	}

	static TMap fuels = new THashMap<Fluid, Integer>();

	FluidTankAdv tank = new FluidTankAdv(MAX_FLUID);
	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);

	public static boolean isValidFuel(FluidStack stack) {

		return stack == null ? false : fuels.containsKey(stack.getFluid());
	}

	public static boolean registerFuel(Fluid fluid, int energy) {

		if (fluid == null || energy < 10000) {
			return false;
		}
		fuels.put(fluid, energy / 20);
		return true;
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : (Integer) fuels.get(stack.getFluid());
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getLightValue() {

		return isActive ? 14 : 0;
	}

	@Override
	protected boolean canGenerate() {

		return fuelRF > 0 ? true : tank.getFluidAmount() >= 50;
	}

	@Override
	public void generate() {

		if (fuelRF <= 0) {
			fuelRF += getFuelEnergy(tank.getFluid()) * fuelMod / FUEL_MOD;
			tank.drain(50, true);
		}
		int energy = calcEnergy() * energyMod;
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return renderFluid.getFluid().getIcon(renderFluid);
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoMagmatic(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	@Override
	public FluidTankAdv getTank(int tankIndex) {

		return tank;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		tank.readFromNBT(nbt);

		if (!isValidFuel(tank.getFluid())) {
			tank.setFluid(null);
		}
		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		tank.writeToNBT(nbt);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		renderFluid = payload.getFluidStack();
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (resource == null || from.ordinal() == facing && !augmentCoilDuct) {
			return 0;
		}
		if (isValidFuel(resource)) {
			return tank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (resource == null || !augmentCoilDuct) {
			return null;
		}
		if (isValidFuel(resource)) {
			return tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (!augmentCoilDuct) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
