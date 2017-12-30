package com.standardcheckout.plugin.internal;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.PurchaseFlow;
import com.standardcheckout.plugin.flow.stage.InventoryStage;
import com.standardcheckout.plugin.flow.stage.Stage;

public class PurchaseFlowListener implements Listener {

	@EventHandler
	public void on(InventoryCloseEvent event) {
		useStage(event.getPlayer(), stage -> {
			if (stage instanceof InventoryStage) {
				InventoryStage inventoryStage = (InventoryStage) stage;
				inventoryStage.closeInventory(event);
			}
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void on(InventoryClickEvent event) {
		useStage(event.getWhoClicked(), stage -> {
			if (stage instanceof InventoryStage) {
				InventoryStage inventoryStage = (InventoryStage) stage;
				inventoryStage.clickInventory(event);
			}
		});
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		useFlow(event.getPlayer(), PurchaseFlow::close);
	}

	@EventHandler
	public void on(PluginDisableEvent event) {
		if (event.getPlugin() instanceof StandardCheckoutPlugin) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				useFlow(player, PurchaseFlow::prematureEnd);
			}
		}
	}

	private void useStage(HumanEntity player, Consumer<Stage> callback) {
		useFlow(player, flow -> {
			Stage stage = flow.getStage();
			if (stage != null) {
				callback.accept(stage);
			}
		});
	}

	private void useFlow(HumanEntity player, Consumer<PurchaseFlow> callback) {
		PurchaseFlow flow = PurchaseFlow.currentFlow(player);
		if (flow != null && !flow.isFinished()) {
			callback.accept(flow);
		}
	}

}
