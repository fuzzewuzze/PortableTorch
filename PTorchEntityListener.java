package com.gmail.fuzzelogicsoftware.PortableTorch;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;


class PTorchEntityListener extends EntityListener {

	@Override
	public void onEntityDeath (EntityDeathEvent event) {
		if ( event.getEntity() instanceof Player ) {
			if ( PTorch.isLit((Player)event.getEntity())) {
					PTorch.extinguish((Player)event.getEntity());
			}
		}
	}
}