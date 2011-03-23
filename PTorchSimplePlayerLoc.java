package com.gmail.fuzzelogicsoftware.PortableTorch;

import org.bukkit.Location;

public class PTorchSimplePlayerLoc {
	private int x;
	private int y;
	private int z;
	
	PTorchSimplePlayerLoc(int newX, int newY, int newZ) {
		this.x = newX;
		this.y = newY;
		this.z = newZ;
	}
	
	public boolean equals (Location loc) {
		return (loc.getBlockX() == x && loc.getBlockY() == y & loc.getBlockZ() == z);
	}
	
	public void set(Location loc) {
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
	}
}