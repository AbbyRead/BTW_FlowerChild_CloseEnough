package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public final class ModLoader {
	private static final List<TextureFX> animList = new LinkedList();
	private static final Map<Integer, BaseMod> blockModels = new HashMap();
	private static final Map<Integer, Boolean> blockSpecialInv = new HashMap();
	private static Map<String, Class<? extends Entity>> classMap = null;
	private static long clock = 0L;
	public static final boolean DEBUG = false;
	private static Field field_animList = null;
	private static Field field_armorList = null;
	private static Field field_blockList = null;
	private static Field field_modifiers = null;
	private static Field field_TileEntityRenderers = null;
	private static boolean hasInit = false;
	private static int highestEntityId = 3000;
	private static final Map<BaseMod, Boolean> inGameHooks = new HashMap();
	private static final Map<BaseMod, Boolean> inGUIHooks = new HashMap();
	private static Minecraft instance = null;
	private static int itemSpriteIndex = 0;
	private static final Map<BaseMod, Map<KeyBinding, boolean[]>> keyList = new HashMap();
	private static final Logger logger = Logger.getLogger("ModLoader");
	private static FileHandler logHandler = null;
	private static Method method_RegisterEntityID = null;
	private static Method method_RegisterTileEntity = null;
	private static final LinkedList<BaseMod> modList = new LinkedList();
	private static int nextBlockModelID = 1000;
	private static final Map<Integer, Map<String, Integer>> overrides = new HashMap();
	private static MobSpawnerBase[] standardBiomes;
	private static int terrainSpriteIndex = 0;
	private static String texPack = null;
	private static boolean texturesAdded = false;
	private static final boolean[] usedItemSprites = new boolean[256];
	private static final boolean[] usedTerrainSprites = new boolean[256];
	public static final String VERSION = "ModLoader Beta 1.4_01";

	public static int AddAllFuel(int id) {
		logger.finest("Finding fuel for " + id);
		int result = 0;

		for(Iterator iter = modList.iterator(); iter.hasNext() && result == 0; result = ((BaseMod)iter.next()).AddFuel(id)) {
		}

		if(result != 0) {
			logger.finest("Returned " + result);
		}

		return result;
	}

	public static void AddAllRenderers(Map<Class<? extends Entity>, Render> renderers) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Iterator var2 = modList.iterator();

		while(var2.hasNext()) {
			BaseMod mod = (BaseMod)var2.next();
			mod.AddRenderer(renderers);
		}

	}

	public static void addAnimation(TextureFX anim) {
		logger.finest("Adding animation " + anim.toString());
		animList.add(anim);
	}

	public static int AddArmor(String armor) {
		try {
			String[] e = (String[])field_armorList.get((Object)null);
			List existingArmorList = Arrays.asList(e);
			ArrayList combinedList = new ArrayList();
			combinedList.addAll(existingArmorList);
			if(!combinedList.contains(armor)) {
				combinedList.add(armor);
			}

			int index = combinedList.indexOf(armor);
			field_armorList.set((Object)null, combinedList.toArray(new String[0]));
			return index;
		} catch (IllegalArgumentException var5) {
			logger.throwing("ModLoader", "AddArmor", var5);
			ThrowException("An impossible error has occured!", var5);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "AddArmor", var6);
			ThrowException("An impossible error has occured!", var6);
		}

		return -1;
	}

	public static void AddLocalization(String key, String value) {
		Properties props = null;

		try {
			props = (Properties)getPrivateValue(StringTranslate.class, StringTranslate.getInstance(), 1);
		} catch (SecurityException var4) {
			logger.throwing("ModLoader", "AddLocalization", var4);
			ThrowException(var4);
		} catch (NoSuchFieldException var5) {
			logger.throwing("ModLoader", "AddLocalization", var5);
			ThrowException(var5);
		}

		if(props != null) {
			props.put(key, value);
		}

	}

	private static void addMod(ClassLoader loader, String filename) {
		try {
			String e = filename.split("\\.")[0];
			if(e.contains("$")) {
				return;
			}

			Package pack = ModLoader.class.getPackage();
			if(pack != null) {
				e = pack.getName() + "." + e;
			}

			Class instclass = loader.loadClass(e);
			if(instclass.isAssignableFrom(BaseMod.class)) {
				return;
			}

			BaseMod mod = (BaseMod)instclass.newInstance();
			if(mod != null) {
				modList.add(mod);
				logger.fine("Mod Loaded: \"" + mod.toString() + "\" from " + filename);
				System.out.println("Mod Loaded: " + mod.toString());
			}
		} catch (Throwable var6) {
			logger.fine("Failed to load mod from \"" + filename + "\"");
			System.out.println("Failed to load mod from \"" + filename + "\"");
			logger.throwing("ModLoader", "addMod", var6);
			ThrowException(var6);
		}

	}

	public static void AddName(Object instance, String name) {
		String tag = null;
		Exception e3;
		if(instance instanceof Item) {
			Item e = (Item)instance;
			if(e.getItemName() != null) {
				tag = e.getItemName() + ".name";
			}
		} else if(instance instanceof Block) {
			Block e1 = (Block)instance;
			if(e1.getBlockName() != null) {
				tag = e1.getBlockName() + ".name";
			}
		} else if(instance instanceof ItemStack) {
			ItemStack e2 = (ItemStack)instance;
			if(e2.func_20109_f() != null) {
				tag = e2.func_20109_f() + ".name";
			}
		} else {
			e3 = new Exception(instance.getClass().getName() + " cannot have name attached to it!");
			logger.throwing("ModLoader", "AddName", e3);
			ThrowException(e3);
		}

		if(tag != null) {
			AddLocalization(tag, name);
		} else {
			e3 = new Exception(instance + " is missing name tag!");
			logger.throwing("ModLoader", "AddName", e3);
			ThrowException(e3);
		}

	}

	public static int addOverride(String fileToOverride, String fileToAdd) {
		try {
			int e = getUniqueSpriteIndex(fileToOverride);
			addOverride(fileToOverride, fileToAdd, e);
			return e;
		} catch (Throwable var3) {
			logger.throwing("ModLoader", "addOverride", var3);
			ThrowException(var3);
			throw new RuntimeException(var3);
		}
	}

	public static void addOverride(String path, String overlayPath, int index) {
		boolean dst = true;
		byte dst1;
		if(path.equals("/terrain.png")) {
			dst1 = 0;
		} else {
			if(!path.equals("/gui/items.png")) {
				return;
			}

			dst1 = 1;
		}

		logger.finer("addOverride(" + path + "," + overlayPath + "," + index + ")");
		Object overlays = (Map)overrides.get(Integer.valueOf(dst1));
		if(overlays == null) {
			overlays = new HashMap();
			overrides.put(Integer.valueOf(dst1), overlays);
		}

		((Map)overlays).put(overlayPath, Integer.valueOf(index));
	}

	public static void AddRecipe(ItemStack output, Object... params) {
		CraftingManager.getInstance().addRecipe(output, params);
	}

	public static void AddShapelessRecipe(ItemStack output, Object... params) {
		CraftingManager.getInstance().addShapelessRecipe(output, params);
	}

	public static void AddSmelting(int input, ItemStack output) {
		FurnaceRecipes.smelting().addSmelting(input, output);
	}

	public static void AddSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, EnumCreatureType spawnList) {
		AddSpawn((Class)entityClass, weightedProb, spawnList, (MobSpawnerBase[])null);
	}

	public static void AddSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, EnumCreatureType spawnList, MobSpawnerBase... biomes) {
		if(entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(spawnList == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else {
			if(biomes == null) {
				biomes = standardBiomes;
			}

			for(int i = 0; i < biomes.length; ++i) {
				List list = biomes[i].getSpawnableList(spawnList);
				if(list != null) {
					boolean exists = false;
					Iterator var8 = list.iterator();

					while(var8.hasNext()) {
						SpawnListEntry entry = (SpawnListEntry)var8.next();
						if(entry.field_25212_a == entityClass) {
							entry.field_25211_b = weightedProb;
							exists = true;
							break;
						}
					}

					if(!exists) {
						list.add(new SpawnListEntry(entityClass, weightedProb));
					}
				}
			}

		}
	}

	public static void AddSpawn(String entityName, int weightedProb, EnumCreatureType spawnList) {
		AddSpawn((String)entityName, weightedProb, spawnList, (MobSpawnerBase[])null);
	}

	public static void AddSpawn(String entityName, int weightedProb, EnumCreatureType spawnList, MobSpawnerBase... biomes) {
		Class entityClass = (Class)classMap.get(entityName);
		if(entityClass != null && EntityLiving.class.isAssignableFrom(entityClass)) {
			AddSpawn(entityClass, weightedProb, spawnList, biomes);
		}

	}

	public static boolean DispenseEntity(World world, double x, double y, double z, float xVel, float zVel, ItemStack item) {
		boolean result = false;

		for(Iterator iter = modList.iterator(); iter.hasNext() && !result; result = ((BaseMod)iter.next()).DispenseEntity(world, x, y, z, xVel, zVel, item)) {
		}

		return result;
	}

	public static List<BaseMod> getLoadedMods() {
		return Collections.unmodifiableList(modList);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Minecraft getMinecraftInstance() {
		if(instance == null) {
			try {
				ThreadGroup e = Thread.currentThread().getThreadGroup();
				int count = e.activeCount();
				Thread[] threads = new Thread[count];
				e.enumerate(threads);

				for(int i = 0; i < threads.length; ++i) {
					if(threads[i].getName().equals("Minecraft main thread")) {
						instance = (Minecraft)getPrivateValue(Thread.class, threads[i], "target");
						break;
					}
				}
			} catch (SecurityException var4) {
				logger.throwing("ModLoader", "getMinecraftInstance", var4);
				throw new RuntimeException(var4);
			} catch (NoSuchFieldException var5) {
				logger.throwing("ModLoader", "getMinecraftInstance", var5);
				throw new RuntimeException(var5);
			}
		}

		return instance;
	}

	public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, int fieldindex) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredFields()[fieldindex];
			e.setAccessible(true);
			return e.get(instance);
		} catch (IllegalAccessException var4) {
			logger.throwing("ModLoader", "getPrivateValue", var4);
			ThrowException("An impossible error has occured!", var4);
			return null;
		}
	}

	public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredField(field);
			e.setAccessible(true);
			return e.get(instance);
		} catch (IllegalAccessException var4) {
			logger.throwing("ModLoader", "getPrivateValue", var4);
			ThrowException("An impossible error has occured!", var4);
			return null;
		}
	}

	public static int getUniqueBlockModelID(BaseMod mod, boolean full3DItem) {
		int id = nextBlockModelID++;
		blockModels.put(Integer.valueOf(id), mod);
		blockSpecialInv.put(Integer.valueOf(id), Boolean.valueOf(full3DItem));
		return id;
	}

	public static int getUniqueEntityId() {
		return highestEntityId++;
	}

	private static int getUniqueItemSpriteIndex() {
		while(itemSpriteIndex < usedItemSprites.length) {
			if(!usedItemSprites[itemSpriteIndex]) {
				usedItemSprites[itemSpriteIndex] = true;
				return itemSpriteIndex++;
			}

			++itemSpriteIndex;
		}

		Exception e = new Exception("No more empty item sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	public static int getUniqueSpriteIndex(String path) {
		if(path.equals("/gui/items.png")) {
			return getUniqueItemSpriteIndex();
		} else if(path.equals("/terrain.png")) {
			return getUniqueTerrainSpriteIndex();
		} else {
			Exception e = new Exception("No registry for this texture: " + path);
			logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
			ThrowException(e);
			return 0;
		}
	}

	private static int getUniqueTerrainSpriteIndex() {
		while(terrainSpriteIndex < usedTerrainSprites.length) {
			if(!usedTerrainSprites[terrainSpriteIndex]) {
				usedTerrainSprites[terrainSpriteIndex] = true;
				return terrainSpriteIndex++;
			}

			++terrainSpriteIndex;
		}

		Exception e = new Exception("No more empty terrain sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	private static void init() {
		hasInit = true;
		String usedItemSpritesString = "1111111111111111111111111111111111111101111111011111111111110001111111111111111111111011111010111111100110000011111110000000001111111001100000110000000100000011000000010000001100000000000000110000000000000000000000000000000000000000000000001100000000000000";
		String usedTerrainSpritesString = "1111111111111111111111111111110111111100011111111111110001111110111111111111000011110011111111111111001111000000111111111111100011111111000010001111011110000000111011000000000011100000000000001110000000000111111000000000001101000000000001111111111111000011";

		for(int logfile = 0; logfile < 256; ++logfile) {
			usedItemSprites[logfile] = usedItemSpritesString.charAt(logfile) == 49;
			usedTerrainSprites[logfile] = usedTerrainSpritesString.charAt(logfile) == 49;
		}

		try {
			instance = (Minecraft)getPrivateValue(Minecraft.class, (Object)null, 0);
			instance.entityRenderer = new EntityRendererProxy(instance);
			classMap = (Map)getPrivateValue(EntityList.class, (Object)null, 0);
			field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
			field_blockList = Session.class.getDeclaredFields()[0];
			field_blockList.setAccessible(true);
			field_TileEntityRenderers = TileEntityRenderer.class.getDeclaredFields()[0];
			field_TileEntityRenderers.setAccessible(true);
			field_armorList = RenderPlayer.class.getDeclaredFields()[3];
			field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & -17);
			field_armorList.setAccessible(true);
			field_animList = RenderEngine.class.getDeclaredFields()[5];
			field_animList.setAccessible(true);
			Field[] var15 = MobSpawnerBase.class.getDeclaredFields();
			LinkedList e = new LinkedList();

			for(int mod = 0; mod < var15.length; ++mod) {
				if(mod != 11) {
					Class fieldType = var15[mod].getType();
					if((var15[mod].getModifiers() & 8) != 0 && fieldType.isAssignableFrom(MobSpawnerBase.class)) {
						MobSpawnerBase biome = (MobSpawnerBase)var15[mod].get((Object)null);
						e.add(biome);
					}
				}
			}

			standardBiomes = (MobSpawnerBase[])e.toArray(new MobSpawnerBase[0]);

			try {
				method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("a", new Class[]{Class.class, String.class});
			} catch (NoSuchMethodException var8) {
				method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("addMapping", new Class[]{Class.class, String.class});
			}

			method_RegisterTileEntity.setAccessible(true);

			try {
				method_RegisterEntityID = EntityList.class.getDeclaredMethod("a", new Class[]{Class.class, String.class, Integer.TYPE});
			} catch (NoSuchMethodException var7) {
				method_RegisterEntityID = EntityList.class.getDeclaredMethod("addMapping", new Class[]{Class.class, String.class, Integer.TYPE});
			}

			method_RegisterEntityID.setAccessible(true);
		} catch (SecurityException var10) {
			logger.throwing("ModLoader", "init", var10);
			ThrowException(var10);
			throw new RuntimeException(var10);
		} catch (NoSuchFieldException var11) {
			logger.throwing("ModLoader", "init", var11);
			ThrowException(var11);
			throw new RuntimeException(var11);
		} catch (NoSuchMethodException var12) {
			logger.throwing("ModLoader", "init", var12);
			ThrowException(var12);
			throw new RuntimeException(var12);
		} catch (IllegalArgumentException var13) {
			logger.throwing("ModLoader", "init", var13);
			ThrowException(var13);
			throw new RuntimeException(var13);
		} catch (IllegalAccessException var14) {
			logger.throwing("ModLoader", "init", var14);
			ThrowException(var14);
			throw new RuntimeException(var14);
		}

		File var16 = new File(Minecraft.getMinecraftDir(), "ModLoader.txt");

		try {
			logger.setLevel(Level.FINER);
			if(var16.canWrite() && logHandler == null) {
				logHandler = new FileHandler(var16.getPath());
				logHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logHandler);
			}

			logger.fine("ModLoader Beta 1.4_01 Initializing...");
			System.out.println("ModLoader Beta 1.4_01 Initializing...");
			File var17 = new File(ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			readFromClassPath(var17);
			System.out.println("Done.");
			Iterator var19 = modList.iterator();

			while(var19.hasNext()) {
				BaseMod var18 = (BaseMod)var19.next();
				var18.ModsLoaded();
			}

		} catch (Throwable var9) {
			logger.throwing("ModLoader", "init", var9);
			ThrowException("ModLoader has failed to initialize.", var9);
			if(logHandler != null) {
				logHandler.close();
			}

			throw new RuntimeException(var9);
		}
	}

	public static boolean isGUIOpen(Class<? extends GuiScreen> gui) {
		Minecraft game = getMinecraftInstance();
		return gui == null ? game.currentScreen == null : (game.currentScreen == null && gui != null ? false : gui.isInstance(game.currentScreen));
	}

	public static boolean isModLoaded(String modname) {
		Class chk = null;

		try {
			chk = Class.forName(modname);
		} catch (ClassNotFoundException var4) {
			return false;
		}

		if(chk != null) {
			Iterator var3 = modList.iterator();

			while(var3.hasNext()) {
				BaseMod mod = (BaseMod)var3.next();
				if(chk.isInstance(mod)) {
					return true;
				}
			}
		}

		return false;
	}

	public static BufferedImage loadImage(RenderEngine texCache, String path) throws Exception {
		TexturePackList pack = (TexturePackList)getPrivateValue(RenderEngine.class, texCache, 10);
		InputStream input = pack.selectedTexturePack.func_6481_a(path);
		if(input == null) {
			throw new Exception("Image not found: " + path);
		} else {
			BufferedImage image = ImageIO.read(input);
			if(image == null) {
				throw new Exception("Image not found: " + path);
			} else {
				return image;
			}
		}
	}

	public static void OnTick(Minecraft game) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		if(texPack == null || game.gameSettings.skin != texPack) {
			texturesAdded = false;
			texPack = game.gameSettings.skin;
		}

		if(!texturesAdded && game.renderEngine != null) {
			RegisterAllTextureOverrides(game.renderEngine);
			texturesAdded = true;
		}

		long newclock = 0L;
		Entry modSet;
		Iterator var4;
		if(game.theWorld != null) {
			newclock = game.theWorld.getWorldTime();
			var4 = inGameHooks.entrySet().iterator();

			label89:
			while(true) {
				do {
					if(!var4.hasNext()) {
						break label89;
					}

					modSet = (Entry)var4.next();
				} while(clock == newclock && ((Boolean)modSet.getValue()).booleanValue());

				((BaseMod)modSet.getKey()).OnTickInGame(game);
			}
		}

		if(game.currentScreen != null) {
			var4 = inGUIHooks.entrySet().iterator();

			label76:
			while(true) {
				do {
					if(!var4.hasNext()) {
						break label76;
					}

					modSet = (Entry)var4.next();
				} while(clock == newclock && ((Boolean)modSet.getValue()).booleanValue() & game.theWorld != null);

				((BaseMod)modSet.getKey()).OnTickInGUI(game, game.currentScreen);
			}
		}

		if(clock != newclock) {
			var4 = keyList.entrySet().iterator();

			label62:
			while(var4.hasNext()) {
				modSet = (Entry)var4.next();
				Iterator var6 = ((Map)modSet.getValue()).entrySet().iterator();

				while(true) {
					Entry keySet;
					boolean state;
					boolean[] keyInfo;
					boolean oldState;
					do {
						do {
							if(!var6.hasNext()) {
								continue label62;
							}

							keySet = (Entry)var6.next();
							state = Keyboard.isKeyDown(((KeyBinding)keySet.getKey()).keyCode);
							keyInfo = (boolean[])keySet.getValue();
							oldState = keyInfo[1];
							keyInfo[1] = state;
						} while(!state);
					} while(oldState && !keyInfo[0]);

					((BaseMod)modSet.getKey()).KeyboardEvent((KeyBinding)keySet.getKey());
				}
			}
		}

		clock = newclock;
	}

	public static void OpenGUI(EntityPlayer player, GuiScreen gui) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Minecraft game = getMinecraftInstance();
		if(game.thePlayer == player) {
			if(gui != null) {
				game.displayGuiScreen(gui);
			}

		}
	}

	public static void PopulateChunk(IChunkProvider generator, int chunkX, int chunkZ, World world) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Iterator var5 = modList.iterator();

		while(var5.hasNext()) {
			BaseMod mod = (BaseMod)var5.next();
			if(generator instanceof ChunkProviderGenerate) {
				mod.GenerateSurface(world, world.rand, chunkX, chunkZ);
			} else if(generator instanceof ChunkProviderHell) {
				mod.GenerateNether(world, world.rand, chunkX, chunkZ);
			}
		}

	}

	private static void readFromClassPath(File source) throws FileNotFoundException, IOException {
		logger.finer("Adding mods from " + source.getCanonicalPath());
		ClassLoader loader = ModLoader.class.getClassLoader();
		String name;
		if(source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip"))) {
			logger.finer("Zip found.");
			FileInputStream var6 = new FileInputStream(source);
			ZipInputStream var8 = new ZipInputStream(var6);
			ZipEntry var9 = null;

			while(true) {
				var9 = var8.getNextEntry();
				if(var9 == null) {
					var6.close();
					break;
				}

				name = var9.getName();
				if(!var9.isDirectory() && name.startsWith("mod_") && name.endsWith(".class")) {
					addMod(loader, name);
				}
			}
		} else if(source.isDirectory()) {
			Package pkg = ModLoader.class.getPackage();
			if(pkg != null) {
				String files = pkg.getName().replace('.', File.separatorChar);
				source = new File(source, files);
			}

			logger.finer("Directory found.");
			File[] var7 = source.listFiles();
			if(var7 != null) {
				for(int i = 0; i < var7.length; ++i) {
					name = var7[i].getName();
					if(var7[i].isFile() && name.startsWith("mod_") && name.endsWith(".class")) {
						addMod(loader, name);
					}
				}
			}
		}

	}

	public static KeyBinding[] RegisterAllKeys(KeyBinding[] originals) {
		List existingKeyList = Arrays.asList(originals);
		ArrayList combinedList = new ArrayList();
		combinedList.addAll(existingKeyList);
		Iterator var4 = keyList.values().iterator();

		while(var4.hasNext()) {
			Map keyMap = (Map)var4.next();
			combinedList.addAll(keyMap.keySet());
		}

		return (KeyBinding[])combinedList.toArray(new KeyBinding[0]);
	}

	public static void RegisterAllTextureOverrides(RenderEngine texCache) {
		animList.clear();
		Minecraft game = getMinecraftInstance();
		Iterator var3 = modList.iterator();

		while(var3.hasNext()) {
			BaseMod overlay = (BaseMod)var3.next();
			overlay.RegisterAnimation(game);
		}

		var3 = animList.iterator();

		while(var3.hasNext()) {
			TextureFX overlay1 = (TextureFX)var3.next();
			texCache.registerTextureFX(overlay1);
		}

		var3 = overrides.entrySet().iterator();

		while(var3.hasNext()) {
			Entry overlay2 = (Entry)var3.next();
			Iterator var5 = ((Map)overlay2.getValue()).entrySet().iterator();

			while(var5.hasNext()) {
				Entry overlayEntry = (Entry)var5.next();
				String overlayPath = (String)overlayEntry.getKey();
				int index = ((Integer)overlayEntry.getValue()).intValue();
				int dst = ((Integer)overlay2.getKey()).intValue();
				String dstPath = null;
				if(dst == 0) {
					dstPath = "/terrain.png";
				} else {
					if(dst != 1) {
						throw new ArrayIndexOutOfBoundsException(dst);
					}

					dstPath = "/gui/items.png";
				}

				System.out.println("Overriding " + dstPath + " with " + overlayPath + " @ " + index);

				try {
					BufferedImage e = loadImage(texCache, overlayPath);
					ModTexture anim = new ModTexture(index, dst, e);
					texCache.registerTextureFX(anim);
				} catch (Exception var12) {
					logger.throwing("ModLoader", "RegisterAllTextureOverrides", var12);
					ThrowException(var12);
					throw new RuntimeException(var12);
				}
			}
		}

	}

	public static void RegisterBlock(Block block) {
		RegisterBlock(block, (Class)null);
	}

	public static void RegisterBlock(Block block, Class<? extends ItemBlock> itemclass) {
		try {
			if(block == null) {
				throw new IllegalArgumentException("block parameter cannot be null.");
			}

			List e = (List)field_blockList.get((Object)null);
			e.add(block);
			int id = block.blockID;
			ItemBlock item = null;
			if(itemclass != null) {
				item = (ItemBlock)itemclass.getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{Integer.valueOf(id - 256)});
			} else {
				item = new ItemBlock(id - 256);
			}

			if(Block.blocksList[id] != null && Item.itemsList[id] == null) {
				Item.itemsList[id] = item;
			}
		} catch (IllegalArgumentException var5) {
			logger.throwing("ModLoader", "RegisterBlock", var5);
			ThrowException(var5);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "RegisterBlock", var6);
			ThrowException(var6);
		} catch (SecurityException var7) {
			logger.throwing("ModLoader", "RegisterBlock", var7);
			ThrowException(var7);
		} catch (InstantiationException var8) {
			logger.throwing("ModLoader", "RegisterBlock", var8);
			ThrowException(var8);
		} catch (InvocationTargetException var9) {
			logger.throwing("ModLoader", "RegisterBlock", var9);
			ThrowException(var9);
		} catch (NoSuchMethodException var10) {
			logger.throwing("ModLoader", "RegisterBlock", var10);
			ThrowException(var10);
		}

	}

	public static void RegisterEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
		try {
			method_RegisterEntityID.invoke((Object)null, new Object[]{entityClass, entityName, Integer.valueOf(id)});
		} catch (IllegalArgumentException var4) {
			logger.throwing("ModLoader", "RegisterEntityID", var4);
			ThrowException(var4);
		} catch (IllegalAccessException var5) {
			logger.throwing("ModLoader", "RegisterEntityID", var5);
			ThrowException(var5);
		} catch (InvocationTargetException var6) {
			logger.throwing("ModLoader", "RegisterEntityID", var6);
			ThrowException(var6);
		}

	}

	public static void RegisterKey(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat) {
		Object keyMap = (Map)keyList.get(mod);
		if(keyMap == null) {
			keyMap = new HashMap();
		}

		((Map)keyMap).put(keyHandler, new boolean[]{allowRepeat, false});
		keyList.put(mod, keyMap);
	}

	public static void RegisterTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		RegisterTileEntity(tileEntityClass, id, (TileEntitySpecialRenderer)null);
	}

	public static void RegisterTileEntity(Class<? extends TileEntity> tileEntityClass, String id, TileEntitySpecialRenderer renderer) {
		try {
			method_RegisterTileEntity.invoke((Object)null, new Object[]{tileEntityClass, id});
			if(renderer != null) {
				TileEntityRenderer e = TileEntityRenderer.instance;
				Map renderers = (Map)field_TileEntityRenderers.get(e);
				renderers.put(tileEntityClass, renderer);
				renderer.setTileEntityRenderer(e);
			}
		} catch (IllegalArgumentException var5) {
			logger.throwing("ModLoader", "RegisterTileEntity", var5);
			ThrowException(var5);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "RegisterTileEntity", var6);
			ThrowException(var6);
		} catch (InvocationTargetException var7) {
			logger.throwing("ModLoader", "RegisterTileEntity", var7);
			ThrowException(var7);
		}

	}

	public static void RemoveSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList) {
		RemoveSpawn((Class)entityClass, spawnList, (MobSpawnerBase[])null);
	}

	public static void RemoveSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList, MobSpawnerBase... biomes) {
		if(entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(spawnList == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else {
			if(biomes == null) {
				biomes = standardBiomes;
			}

			for(int i = 0; i < biomes.length; ++i) {
				List list = biomes[i].getSpawnableList(spawnList);
				if(list != null) {
					Iterator var6 = list.iterator();

					while(var6.hasNext()) {
						SpawnListEntry entry = (SpawnListEntry)var6.next();
						if(entry.field_25212_a == entityClass) {
							list.remove(entry);
							break;
						}
					}
				}
			}

		}
	}

	public static void RemoveSpawn(String entityName, EnumCreatureType spawnList) {
		RemoveSpawn((String)entityName, spawnList, (MobSpawnerBase[])null);
	}

	public static void RemoveSpawn(String entityName, EnumCreatureType spawnList, MobSpawnerBase... biomes) {
		Class entityClass = (Class)classMap.get(entityName);
		if(entityClass != null && EntityLiving.class.isAssignableFrom(entityClass)) {
			RemoveSpawn(entityClass, spawnList, biomes);
		}

	}

	public static boolean RenderBlockIsItemFull3D(int modelID) {
		return !blockSpecialInv.containsKey(Integer.valueOf(modelID)) ? modelID == 11 : ((Boolean)blockSpecialInv.get(Integer.valueOf(modelID))).booleanValue();
	}

	public static void RenderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID) {
		BaseMod mod = (BaseMod)blockModels.get(Integer.valueOf(modelID));
		if(mod != null) {
			mod.RenderInvBlock(renderer, block, metadata, modelID);
		}
	}

	public static boolean RenderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		BaseMod mod = (BaseMod)blockModels.get(Integer.valueOf(modelID));
		return mod == null ? false : mod.RenderWorldBlock(renderer, world, x, y, z, block, modelID);
	}

	public static void SetInGameHook(BaseMod mod, boolean enable, boolean useClock) {
		if(enable) {
			inGameHooks.put(mod, Boolean.valueOf(useClock));
		} else {
			inGameHooks.remove(mod);
		}

	}

	public static void SetInGUIHook(BaseMod mod, boolean enable, boolean useClock) {
		if(enable) {
			inGUIHooks.put(mod, Boolean.valueOf(useClock));
		} else {
			inGUIHooks.remove(mod);
		}

	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredFields()[fieldindex];
			e.setAccessible(true);
			int modifiers = field_modifiers.getInt(e);
			if((modifiers & 16) != 0) {
				field_modifiers.setInt(e, modifiers & -17);
			}

			e.set(instance, value);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "setPrivateValue", var6);
			ThrowException("An impossible error has occured!", var6);
		}

	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredField(field);
			int modifiers = field_modifiers.getInt(e);
			if((modifiers & 16) != 0) {
				field_modifiers.setInt(e, modifiers & -17);
			}

			e.setAccessible(true);
			e.set(instance, value);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "setPrivateValue", var6);
			ThrowException("An impossible error has occured!", var6);
		}

	}

	public static void ThrowException(String message, Throwable e) {
		Minecraft game = getMinecraftInstance();
		if(game != null) {
			game.displayUnexpectedThrowable(new UnexpectedThrowable(message, e));
		} else {
			throw new RuntimeException(e);
		}
	}

	private static void ThrowException(Throwable e) {
		ThrowException("Exception occured in ModLoader", e);
	}
}
