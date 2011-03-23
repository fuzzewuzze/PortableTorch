package com.gmail.fuzzelogicsoftware.PortableTorch;

import org.bukkit.Material;
import org.bukkit.entity.Player;


class PTorchSchedule implements Runnable {
	Player player;
	
	PTorchSchedule(Player newPlayer) { this.player = newPlayer; }
	
	@Override
	public void run() {
		
		/*
		if ( PTorch.isLit(player) ) 
		{
			player.
			if(PTorch.serverTicks >= 32)
			{
				PTorch.extinguish(player);
			}
			if ( player.getInventory().getItemInHand().getType() == Material.TORCH ) {
				player.getInventory().getItemInHand().setDurability((short)(player.getInventory().getItemInHand().getDurability()+1));
				if ( player.getInventory().getItemInHand().getDurability() >= 32 ) {
					PTorch.extinguish(player);
					// remove the torch from the player's inventory and return light levels
				}
				else {
					PTorch.myPlugin.getServer().getScheduler().scheduleSyncDelayedTask(PTorch.myPlugin, new PTorchSchedule(player), 40);
				} 	
			}
		}*/
		if(PTorch.isLit(player))
			PTorch.extinguish(player);
	}
}