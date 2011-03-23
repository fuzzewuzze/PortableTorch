package com.gmail.fuzzelogicsoftware.PortableTorch;
/* 
 * Portable Torch Plugin For MineCraft
 * Lighting code based off of TorchBurn by Ryan Carretta
 * http://forums.bukkit.org/threads/inactive-mech-torchburn-v1-0-carry-torches-rather-than-placing-them-450.6009
 */
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.CraftWorld;
import net.minecraft.server.EnumSkyBlock;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.inventory.*;
import org.bukkit.Material;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.util.config.Configuration;

public class PTorch extends JavaPlugin {
	protected static PTorch myPlugin;
	protected static int intensity = 15;
	protected static int falloff = 3;
	protected static int serverTicks = 0;
	private static boolean slowServer = true;
	private final PTorchPlayerListener playerListener = new PTorchPlayerListener();
	private final PTorchEntityListener entityListener = new PTorchEntityListener();
	private final PTorchBlockListener blockListener = new PTorchBlockListener();
	
	// used to reduce calls to lightArea(), instead of on every playermove, only when block changes.
	private static HashMap<Player, PTorchSimplePlayerLoc> playerLoc = new HashMap<Player, PTorchSimplePlayerLoc>();

	private static HashMap<Location, PTorchLightLevelOwner> prevState = new HashMap<Location, PTorchLightLevelOwner>();
	private static HashMap<Player, List<Location>> playerBlocks = new HashMap<Player, List<Location>>();
	
	
	static String maindirectory = "plugins/PortableTorch/";
	static File Properties = new File(maindirectory + "config.yml");
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		myPlugin = this;
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
		
		PTorchLoadProperties.loadMain();
		System.out.println("Portable Torch Started! LightTime:" + serverTicks);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static void removeTorch ( Player player) 
	{
		PlayerInventory inv = player.getInventory();
		if ( inv.getItemInHand().getType() == Material.TORCH ) 
		{
			int slot = inv.getHeldItemSlot();
			if ( player.getInventory().getItem(slot).getAmount() > 1 ) 
			{
				// decrement stack
				player.getInventory().getItem(slot).setDurability((short)0);
				player.getInventory().getItem(slot).setAmount(player.getInventory().getItem(slot).getAmount()-1);
			}
			else 
			{
				// last torch
				player.getInventory().removeItem(player.getInventory().getItem(slot));
			}
		}
	}
	public static void extinguish ( Player player, int slot ) 
	{
		removePlayerLoc(player);
		if ( playerBlocks.containsKey(player) ) {
			unLightarea(player);
			playerBlocks.remove(player);
		}
	}
	
	public static void extinguish ( Player player ) {
		removePlayerLoc(player);
		if ( playerBlocks.containsKey(player) ) {
			unLightarea(player);
			playerBlocks.remove(player);
		}
	}
	
	public static boolean updatePlayerLoc (Player player) {
		Location loc = player.getLocation();
		PTorchSimplePlayerLoc tbLoc = playerLoc.get(player);
		
		if ( tbLoc == null )
			return false;
		
		if ( tbLoc.equals(loc) )
			return false;
		
		tbLoc.set(loc.clone());
		return true;
	}
	
	public static boolean isLit (Player player) {
		return ( playerLoc.containsKey(player) );
	}
	
	public static void addPlayerLoc (Player player) {
		Location loc = player.getLocation();
		playerLoc.put(player, new PTorchSimplePlayerLoc(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}
	
	public static void removePlayerLoc (Player player) {
		playerLoc.remove(player);
	}
	
	public static void lightTorch ( Player player ) {
		lightArea(player, intensity, falloff);
		addPlayerLoc(player);
		removeTorch(player);
		myPlugin.getServer().getScheduler().scheduleSyncDelayedTask(myPlugin, new PTorchSchedule(player), serverTicks);
	}
	
	public static void lightArea ( Player player, int intensity, int falloff ) {
		assert ( intensity >= 0 );
		assert ( intensity <= 15 );
		assert ( falloff > 0 );
		assert ( falloff <= 15 );
		CraftWorld world = (CraftWorld)player.getWorld();
		int radius = intensity / falloff;
		int blockX = player.getLocation().getBlockX();
		int blockY = player.getLocation().getBlockY();
		int blockZ = player.getLocation().getBlockZ();
				
		// first reset all light around
		if ( playerBlocks.containsKey(player) ) {
			unLightarea(player);
			playerBlocks.remove(player);
		}
		
		List <Location> blockList = new ArrayList<Location>();
		
		for ( int x = -radius; x <= radius; x++ )
			for ( int y = -radius; y <= radius; y++ )
				for ( int z = -radius; z <= radius; z++ ) {
					int newIntensity;
					int curIntensity = world.getHandle().j(blockX+x, blockY+y, blockZ+z);
					
					if ( slowServer == true ) {
						// this is fast
						newIntensity = (intensity-(Math.abs(x)+Math.abs(y)+Math.abs(z))) < 0 ? 0 : intensity-(Math.abs(x)+Math.abs(y)+Math.abs(z));
					}
					else {
						// this is slow, but nicer
						Vector origin = new Vector(blockX, blockY, blockZ);
						Vector v = new Vector(blockX+x, blockY+y, blockZ+z);
						if ( v.isInSphere(origin, radius) ) {
							// looks like the entry is within the radius
							double distanceSq = v.distanceSquared(origin);
							newIntensity = (int)(((intensity-Math.sqrt(distanceSq)*falloff)*100+0.5)/100);
						}
						else {
							newIntensity = curIntensity;
						}
					}
					
					PTorchLightLevelOwner prevIntensity;
					Location l = new Location(world, blockX+x, blockY+y, blockZ+z);
					prevIntensity = PTorch.prevState.get(l);
					int worldIntensity = world.getHandle().j(blockX+x, blockY+y, blockZ+z);
					if ( prevIntensity != null ) {
						// this area was in the map already. see if we are brightening and if it belongs to us
						//if ( prevIntensity.getLevel() < newIntensity && !(prevIntensity.getPlayer().equals(player))) {
							// we are brightening, remove the other guy's entry and add our own 
						//	PTorch.prevState.remove(l);
						//	PTorch.prevState.put(l, new PTorchLightLevelOwner(player, worldIntensity));
					//	}
					}
					else {
						// add the current world's light level to the map
						PTorch.prevState.put(l, new PTorchLightLevelOwner(player, world.getHandle().j(blockX+x, blockY+y, blockZ+z)));
					}
					// light 'em up! 
					if ( newIntensity > worldIntensity ) {
//						if my pull request to bukkit gets accepted
//						l.getBlock().setLightLevel(newIntensity);
						world.getHandle().b(EnumSkyBlock.BLOCK, blockX+x, blockY+y, blockZ+z, newIntensity);
					}
					
					blockList.add(l);
				}	
		playerBlocks.put(player, blockList);
	}

	public static void unLightarea ( Player player ) {
		PTorchLightLevelOwner lightLevelOwner;
		for ( Location l : playerBlocks.get(player) ) {
			lightLevelOwner = prevState.get(l);
			if ( lightLevelOwner != null ) {
				if ( lightLevelOwner.getPlayer().equals(player)) {
// this is if my pull request to bukkit gets accepted
//					l.getBlock().setLightLevel(lightLevelOwner.getLevel());
					((CraftWorld)(player.getWorld())).getHandle().b(EnumSkyBlock.BLOCK, l.getBlockX(), l.getBlockY(), l.getBlockZ(), lightLevelOwner.getLevel());
					prevState.remove(l);
				}
			}
		}
	}
}