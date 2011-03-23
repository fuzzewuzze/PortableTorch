package com.gmail.fuzzelogicsoftware.PortableTorch;

import org.bukkit.entity.Player;

public class PTorchLightLevelOwner {
	private Player owner;
	private Integer level;
	
	PTorchLightLevelOwner(Player newOwner, Integer newLevel) {
		owner = newOwner;
		level = newLevel;
	}
	
	public Player getPlayer() { return owner; }
	public Integer getLevel() { return level; }
}