package com.standardcheckout.buycraft;

import java.math.BigDecimal;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.ulfric.buycraft.model.Item;

import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.buycraft.plugin.shared.tasks.ListingUpdateTask;

public class BuycraftHook {

	public static Optional<Plugin> getBuycraftPlugin() {
		return Optional.ofNullable(Bukkit.getPluginManager().getPlugin("BuycraftX"));
	}

	public static Optional<String> getBuycraftSecret() {
		return getBuycraftPlugin().map(plugin -> {
			try {
				BuycraftPlugin buycraft = (BuycraftPlugin) plugin;
				return buycraft.getConfiguration().getServerKey();
			} catch (Throwable error) {
				return null;
			}
		});
	}

	public static Optional<BigDecimal> getFinalPrice(Item item) {
		return getBuycraftPlugin().map(plugin -> {
			try {
				BuycraftPlugin buycraft = (BuycraftPlugin) plugin;
				ListingUpdateTask listing = buycraft.getListingUpdateTask();
				net.buycraft.plugin.data.Package packge = listing.getPackageById(item.getId());
				if (packge != null) {
					return packge.getEffectivePrice().multiply(quantity(item));
				}
			} catch (Throwable error) {
			}

			return null;
		});
	}

	private static BigDecimal quantity(Item item) {
		return item.getQuantity() == null || item.getQuantity().equals(0) ? BigDecimal.ONE : BigDecimal.valueOf(item.getQuantity());
	}

}
