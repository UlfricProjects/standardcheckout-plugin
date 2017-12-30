package com.standardcheckout.plugin.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.standardcheckout.plugin.internal.PlayerReference;

public class MutableFlowContext implements FlowContext {

	private final PurchaseFlow flow;
	private final PlayerReference player;
	private final Map<Class<?>, Object> beans = new HashMap<>();

	public MutableFlowContext(PurchaseFlow flow, PlayerReference player) {
		Objects.requireNonNull(flow, "flow");
		Objects.requireNonNull(player, "player");

		this.flow = flow;
		this.player = player;
	}

	@Override
	public <T> T getBean(Class<T> type) {
		return type.cast(beans.get(type));
	}

	@Override
	public Optional<Player> getPlayer() {
		return player.get();
	}

	public void storeBean(Object bean) {
		beans.put(bean.getClass(), bean);
	}

	@Override
	public UUID getPlayerId() {
		return player.getUniqueId();
	}

	@Override
	public PurchaseFlow flow() {
		return flow;
	}

}
