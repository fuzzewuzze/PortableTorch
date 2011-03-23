package com.gmail.fuzzelogicsoftware.PortableTorch;

import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;


class PTorchBlockListener extends BlockListener {
	@Override
	public void onBlockPlace (BlockPlaceEvent event) {
		if(event.getBlock().getType() == Material.TORCH)
		{
			PTorch.extinguish(event.getPlayer());
			return;
		}
		/*
		if ( PTorch.isLit(event.getPlayer()))
		{
			PTorch.updatePlayerLoc(event.getPlayer());
			PTorch.lightArea(event.getPlayer(), PTorch.intensity, PTorch.falloff); // values for a torch
			//event.setCancelled(true);
		}
		*/
	}
}