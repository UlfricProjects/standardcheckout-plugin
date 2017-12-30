package com.standardcheckout.plugin.flow;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.stage.Stage;
import com.standardcheckout.plugin.flow.stage.verification.VerificationStage;
import com.standardcheckout.plugin.internal.PlayerReference;
import com.standardcheckout.plugin.model.Cart;
import com.ulfric.buycraft.model.Item;

public class PurchaseFlow implements Closeable {

	private static final String METADATA_KEY = "standardcheckout_flow";

	public static PurchaseFlow currentFlow(HumanEntity player) {
		for (MetadataValue metadata : player.getMetadata(METADATA_KEY)) {
			Object value = metadata.value();
			if (value instanceof PurchaseFlow) {
				return (PurchaseFlow) value;
			}
		}

		return null;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private final List<Item> items = new ArrayList<>();
		private String name;
		private PurchaseCallback callback;

		Builder() {
		}

		public PurchaseFlow begin(Player player) {
			Objects.requireNonNull(player, "player");
			Objects.requireNonNull(name, "name");

			if (items.isEmpty()) {
				throw new IllegalArgumentException("Items are required");
			}

			return new PurchaseFlow(name, new ArrayList<>(items), player, callback);
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder withItem(Item item) {
			Objects.requireNonNull(item, "item");

			items.add(item);
			return this;
		}

		public Builder withItem(Integer id, int quantity) {
			Item item = new Item();
			item.setId(id);
			item.setQuantity(quantity);
			return withItem(item);
		}

		public Builder callback(PurchaseCallback callback) {
			this.callback = callback;
			return this;
		}
	}

	private final MutableFlowContext context;
	private PlayerReference player;
	private Stage stage;
	private PurchaseCallback callback;

	private PurchaseFlow(String name, List<Item> items, Player player, PurchaseCallback callback) {
		MetadataValue metadata = new FixedMetadataValue(StandardCheckoutPlugin.getInstance(), this);
		player.setMetadata(METADATA_KEY, metadata);

		this.player = new PlayerReference(player);
		this.context = new MutableFlowContext(this, this.player);
		Cart cart = new Cart();
		cart.setTitle(name);
		cart.setItems(items);
		cart.setUsername(player.getName());
		this.context.storeBean(cart);
		this.stage = new VerificationStage(context);
		this.stage.play();
	}

	@Override
	public void close() {
		failIfFinished();

		stage.close();
	}

	public void finish(boolean success) {
		if (callback != null) {
			if (success) {
				callback.success(player.getOffline());
			} else {
				callback.failure(player.getOffline());
			}
		}

		close();
		stage = null;
	}

	public void resume() {
		failIfFinished();

		stage.play();
	}

	public void next() {
		failIfFinished();

		stage = stage.next();
		if (stage != null) {
			stage.play();
		}
	}

	public void prematureEnd() {
		close();

		if (callback != null) {
			callback.pluginDisabled(player.getOffline());
		}
	}

	private void failIfFinished() {
		if (isFinished()) {
			throw new IllegalStateException("Flow is finished");
		}
	}

	public boolean isFinished() {
		return stage == null;
	}

	public Stage getStage() {
		return stage;
	}

}
