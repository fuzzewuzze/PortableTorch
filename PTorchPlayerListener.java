package com.gmail.fuzzelogicsoftware.PortableTorch;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.event.player.*;

class PTorchPlayerListener extends PlayerListener {
	public PTorchPlayerListener () { }
	
	@Override
	public void onPlayerMove (PlayerMoveEvent event) {
			Player player = event.getPlayer();
			if(event.getTo().getBlock().getType() == Material.WATER)
			{
				if(PTorch.isLit(event.getPlayer()))
				{
					PTorch.extinguish(event.getPlayer());
				}
				return;
			}
			else
			{
				if ( PTorch.updatePlayerLoc(player) ) {
					PTorch.lightArea(player, PTorch.intensity, PTorch.falloff); // values for a torch
				}
			}
	}
	
	@Override
	public void onPlayerItem(PlayerItemEvent event)
	{
		if(event.getItem().getType() == Material.TORCH)
		{
			if(PTorch.isLit(event.getPlayer()) == false)
			{
				PlayerInventory inv = event.getPlayer().getInventory();
				if ( inv.getItemInHand().getType() == Material.TORCH ) 
				{
					PTorch.lightTorch(event.getPlayer());
				}
			}
		}
	}
	
	@Override
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
	{
		if(PTorch.isLit(event.getPlayer()))
		{
			PTorch.extinguish(event.getPlayer());	
		}
	}
	/*
	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event)
	{
		if(event.getAnimationType() == PlayerAnimationType.ARM_SWING)
		{
			if ( PTorch.isLit(event.getPlayer()) ) {
				// player already has lit torch
			}
			PlayerInventory inv = event.getPlayer().getInventory();
			if ( inv.getItemInHand().getType() == Material.TORCH ) 
			{
					PTorch.lightTorch(event.getPlayer());
			}
		}
	}
	*/
	@Override
	public void onPlayerQuit (PlayerEvent event) {
		if ( PTorch.isLit(event.getPlayer()) ) {
			PTorch.extinguish(event.getPlayer());
		}
	}
}