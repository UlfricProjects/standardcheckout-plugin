package com.standardcheckout.plugin.flow;

import java.io.Closeable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.stage.Stage;
import com.standardcheckout.plugin.flow.stage.confirmation.ConfirmationStage;
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
		private BigDecimal price;
		private String name;
		private PurchaseCallback callback;
		private String referrer;

		Builder() {
		}

		public PurchaseFlow begin(Player player) {
			Objects.requireNonNull(player, "player");
			Objects.requireNonNull(name, "name");

			if (items.isEmpty() && price == null) {
				throw new IllegalArgumentException("items or price is required");
			}

			if (!items.isEmpty() && price != null) {
				throw new IllegalArgumentException("either items or price must be specified, but not both");
			}

			return new PurchaseFlow(name, new ArrayList<>(items), price, player, callback, referrer);
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder price(BigDecimal price) {
			this.price = price == null ? null : price.setScale(2, RoundingMode.HALF_UP);
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

		public Builder referrer(String referrer) {
			this.referrer = referrer;
			return this;
		}
	}

	private final MutableFlowContext context;
	private PlayerReference player;
	private Stage stage;
	private PurchaseCallback callback;

	private PurchaseFlow(String name, List<Item> items, BigDecimal price, Player player, PurchaseCallback callback, String referrer) {
		MetadataValue metadata = new FixedMetadataValue(StandardCheckoutPlugin.getInstance(), this);
		player.setMetadata(METADATA_KEY, metadata);

		this.callback = callback;
		this.player = new PlayerReference(player);
		this.context = new MutableFlowContext(this, this.player);
		PurchaseDetails details = new PurchaseDetails();
		if (!items.isEmpty()) {
			Cart cart = new Cart();
			cart.setTitle(name);
			cart.setItems(items);
			cart.setUsername(player.getName());
			details.setCart(cart);
		}
		details.setPrice(price);
		details.setName(name);
		this.context.storeBean(details);
		this.stage = new ConfirmationStage(context);
		this.stage.play();
	}

	@Override
	public void close() {
		failIfFinished();

		stage.close();
	}

	public void finish(boolean success) {
		close();
		stage = null;

		if (callback != null) {
			if (success) {
				callback.success(player.getOffline());
			} else {
				callback.failure(player.getOffline());
			}
		}
	}

	public void resume() {
		failIfFinished();

		stage.play();
	}

	public void next() {
		failIfFinished();

		Stage stage = this.stage;
		this.stage = null;
		stage.close();
		this.stage = stage.next();
		if (this.stage != null) {
			this.stage.play();
		} else {
			finish(false);
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
