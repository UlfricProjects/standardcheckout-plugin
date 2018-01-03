package com.standardcheckout.plugin.flow;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.OfflinePlayer;

public interface PurchaseCallback {

	static PurchaseCallback success(Consumer<OfflinePlayer> callback) {
		Objects.requireNonNull(callback, "callback");

		return new PurchaseCallback() {

			@Override
			public void success(OfflinePlayer player) {
				callback.accept(player);
			}

			@Override
			public void failure(OfflinePlayer player) {
			}

		};
	}

	void success(OfflinePlayer player);

	void failure(OfflinePlayer player);

	default void pluginDisabled(OfflinePlayer player) {
	}

}
