package com.standardcheckout.plugin.flow.stage;

import java.util.Objects;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.standardcheckout.plugin.flow.FlowContext;

public abstract class InventoryStage extends Stage {

	public InventoryStage(FlowContext context) {
		super(context);
	}

	public void closeInventory(InventoryCloseEvent event) {
		context.flow().close();
	}

	public abstract void clickInventory(InventoryClickEvent event);

	protected boolean compareInventories(Inventory o1, Inventory o2) {
		if (o1 == o2) {
			return true;
		}

		if (o1 == null || o2 == null) {
			return false;
		}

		return o1.getType() == o2.getType() && Objects.equals(o1.getTitle(), o2.getTitle());
	}

	protected abstract Inventory getInventory();

	protected boolean isInventoryOpen(HumanEntity entity) {
		return compareInventories(entity.getOpenInventory().getTopInventory(), getInventory());
	}

}
