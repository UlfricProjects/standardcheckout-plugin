package com.standardcheckout.plugin;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.standardcheckout.plugin.flow.PurchaseCallback;
import com.standardcheckout.plugin.flow.PurchaseFlow;

public class Demo implements Listener {

	public static void main(String[] args) {
		BigDecimal cost = BigDecimal.valueOf(5);
		System.out.println(cost.compareTo(BigDecimal.valueOf(500)));
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		Player player = event.getPlayer();

		PurchaseFlow.builder().name("Diamonds").withItem(2673015, 1) // assumes you have a buycraft package with an id of
																	// '1'
																	// this is how price is calculated. additionally,
																	// any
																	// commands on the buycraft package will be run on
																	// buycraft's
																	// time via an automatic manual payment
				.callback(new PurchaseCallback() {
					@Override
					public void success(OfflinePlayer player) {
						Player online = player.getPlayer();
						if (online == null) {
							// you are responsible for handling this & making sure a player
							// gets their items. the chances of this being true are pretty slim,
							// but it could happen
							Bukkit.getLogger().info("player is offline");
							return;
						}

						Bukkit.getLogger().info("purchase success");
						online.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 64));
					}

					@Override
					public void failure(OfflinePlayer player) {
						Bukkit.getLogger().info("purchase failure");
						// The plugin will handle messaging automatically you
						// can add some special failure logic here if you so desire
					}
				}).begin(player);
	}

}
