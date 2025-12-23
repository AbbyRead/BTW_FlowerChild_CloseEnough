package net.minecraft.src;

public class mod_FCBetterThanWolves extends BaseMod {
	public static final Material fcCementMaterial = new MaterialLiquid();
	public static final Block fcLightBulbOff = (new FCBlockLightBulb(222, 49)).setHardness(0.4F).setStepSound(Block.soundGlassFootstep).setBlockName("fclightbulb");
	public static final Block fcLightBulbOn = (new FCBlockLightBulb(223, 23)).setLightValue(1.0F).setHardness(0.4F).setStepSound(Block.soundGlassFootstep).setBlockName("fclightbulb");
	public static final Block fcBBQ = (new FCBlockBBQ(224)).setHardness(0.4F).setStepSound(Block.soundStoneFootstep).setBlockName("fcbbq");
	public static final Block fcMinecartBoosterIdle = (new FCBlockMinecartBooster(225, 22)).setHardness(0.4F).setResistance(10.0F).setStepSound(Block.soundMetalFootstep).setBlockName("fcminecartbooster");
	public static final Block fcMinecartBoosterActive = (new FCBlockMinecartBooster(226, 22)).setHardness(0.4F).setResistance(10.0F).setStepSound(Block.soundMetalFootstep).setBlockName("fcminecartbooster");
	public static final Block fcMinecartPressurePlateStone = (new FCBlockMinecartPressurePlate(231, 22, Material.rock)).setHardness(0.4F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setBlockName("fcminecartpressureplate");
	public static final Block fcCementMoving = (new FCBlockCement(228)).setHardness(100.0F).setLightOpacity(255).setBlockName("cement");
	public static final Block fcCementStill = (new BlockStationary(229, fcCementMaterial)).setHardness(100.0F).setLightOpacity(255).setBlockName("cement");
	public static final Block fcPressurePlateIron = (new BlockPressurePlate(230, 22, EnumMobType.players)).setHardness(0.5F).setStepSound(Block.soundMetalFootstep).setBlockName("pressurePlate");
	public static final Block fcMinecartPressurePlateWood = (new FCBlockMinecartPressurePlate(227, 22, Material.wood)).setHardness(0.4F).setResistance(10.0F).setStepSound(Block.soundWoodFootstep).setBlockName("fcminecartpressureplate");
	public static final Block fcMinecartPressurePlateIron = (new FCBlockMinecartPressurePlate(232, 22, Material.iron)).setHardness(0.4F).setResistance(10.0F).setStepSound(Block.soundMetalFootstep).setBlockName("fcminecartpressureplate");

	public mod_FCBetterThanWolves() {
		ModLoader.AddName(fcLightBulbOff, "Light Block");
		ModLoader.AddName(fcBBQ, "Hibachi");
		ModLoader.AddName(fcMinecartBoosterIdle, "Minecart Booster");
		ModLoader.AddName(fcMinecartPressurePlateStone, "Minecart Plate");
		ModLoader.AddName(fcCementMoving, "Cement");
		ModLoader.AddName(FCItemBucketCement.fcBucketCement, "Cement Bucket");
		ModLoader.AddRecipe(new ItemStack(fcLightBulbOff, 1), new Object[]{" # ", "#X#", " Y ", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Item.lightStoneDust, Character.valueOf('Y'), Item.redstone});
		ModLoader.AddRecipe(new ItemStack(fcBBQ), new Object[]{"#X#", "#Z#", "#Y#", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Block.bloodStone, Character.valueOf('Y'), Item.redstone, Character.valueOf('Z'), Item.flint});
		ModLoader.AddRecipe(new ItemStack(fcMinecartBoosterIdle), new Object[]{"#Y#", "# #", "#X#", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Item.redstone, Character.valueOf('Y'), Block.slowSand});
		ModLoader.AddRecipe(new ItemStack(fcMinecartPressurePlateStone), new Object[]{"#Y#", "# #", "#X#", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Item.redstone, Character.valueOf('Y'), Block.pressurePlateStone});
		ModLoader.AddRecipe(new ItemStack(fcPressurePlateIron, 1), new Object[]{"##", Character.valueOf('#'), Item.ingotIron});
		ModLoader.AddRecipe(new ItemStack(fcMinecartPressurePlateWood), new Object[]{"#Y#", "# #", "#X#", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Item.redstone, Character.valueOf('Y'), Block.pressurePlatePlanks});
		ModLoader.AddRecipe(new ItemStack(fcMinecartPressurePlateIron), new Object[]{"#Y#", "# #", "#X#", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Item.redstone, Character.valueOf('Y'), fcPressurePlateIron});
		ModLoader.AddRecipe(new ItemStack(FCItemBucketCement.fcBucketCement, 1), new Object[]{"   ", "#YX", " Z ", Character.valueOf('#'), Item.slimeBall, Character.valueOf('X'), Block.gravel, Character.valueOf('Y'), Item.bucketWater, Character.valueOf('Z'), Item.bucketEmpty});
	}

	public String Version() {
		return "1.1";
	}
}
