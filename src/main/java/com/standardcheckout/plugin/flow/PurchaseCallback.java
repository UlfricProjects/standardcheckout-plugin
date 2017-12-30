package com.standardcheckout.plugin.flow;

import org.bukkit.OfflinePlayer;

public interface PurchaseCallback {

	void success(OfflinePlayer player);

	void failure(OfflinePlayer player);

	default void pluginDisabled(OfflinePlayer player) {
	}

}
