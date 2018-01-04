package com.standardcheckout.plugin.flow;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.OfflinePlayer;

import com.ulfric.buycraft.sco.model.StandardCheckoutChargeState;

@FunctionalInterface
public interface PurchaseCallback {

	static PurchaseCallback success(Consumer<OfflinePlayer> callback) {
		Objects.requireNonNull(callback, "callback");

		return (state, player) -> {
			if (state == StandardCheckoutChargeState.SUCCESS) {
				callback.accept(player);
			}
		};
	}

	void handle(StandardCheckoutChargeState state, OfflinePlayer player);

	default void pluginDisabled(OfflinePlayer player) {
	}

}
