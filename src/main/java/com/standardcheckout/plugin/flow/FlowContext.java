package com.standardcheckout.plugin.flow;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface FlowContext {

	PurchaseFlow flow();

	<T> T getBean(Class<T> type);

	Optional<Player> getPlayer();

	default Player getRequiredPlayer() {
		return getPlayer().orElseThrow(() -> new IllegalStateException("The player is not online"));
	}

	UUID getPlayerId();

}
