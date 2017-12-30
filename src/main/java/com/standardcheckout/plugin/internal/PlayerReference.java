package com.standardcheckout.plugin.internal;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class PlayerReference implements Supplier<Optional<Player>> {

	private final UUID uniqueId;

	public PlayerReference(Player player) {
		this(player.getUniqueId());
	}

	public PlayerReference(UUID uniqueId) {
		Objects.requireNonNull(uniqueId, "uniqueId");

		this.uniqueId = uniqueId;
	}

	@Override
	public Optional<Player> get() {
		return Optional.ofNullable(Bukkit.getPlayer(uniqueId));
	}

	public OfflinePlayer getOffline() {
		return get().map(OfflinePlayer.class::cast).orElseGet(() -> Bukkit.getOfflinePlayer(uniqueId));
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

}
