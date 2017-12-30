package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import com.standardcheckout.plugin.StandardCheckoutClient;
import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.MutableFlowContext;
import com.standardcheckout.plugin.flow.stage.InventoryStage;
import com.standardcheckout.plugin.flow.stage.Stage;
import com.standardcheckout.plugin.language.Tell;
import com.standardcheckout.plugin.model.Cart;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeRequest;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeResponse;
import com.ulfric.buycraft.sco.model.StandardCheckoutError;

public class PurchaseStage extends InventoryStage {

	private static final ItemStack VISUAL_ITEM_1;
	private static final ItemStack VISUAL_ITEM_2;

	static {
		VISUAL_ITEM_1 = visualItem(5);
		VISUAL_ITEM_2 = visualItem(13);
	}

	private static ItemStack visualItem(int damage) {
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) damage);
		ItemMeta visualMeta1 = item.getItemMeta();
		visualMeta1.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Completing your purchase...");
		item.setItemMeta(visualMeta1);
		return item;
	}

	private Inventory inventory;
	private BukkitTask visualTask;
	private BukkitTask purchaseTask;
	private int visualSlot;
	private boolean alternate;

	public PurchaseStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		StandardCheckoutPlugin plugin = StandardCheckoutPlugin.getInstance();

		Player player = context.getRequiredPlayer();
		inventory = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Standard Checkout");
		for (int x = 0; x < inventory.getSize(); x++) {
			inventory.setItem(x, VISUAL_ITEM_2);
		}
		player.openInventory(inventory);
		visualTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::playVisual, 4L, 10L);

		if (purchaseTask == null) {
			purchaseTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, this::chargeUser, 2L);
		}
	}

	private void playVisual() {
		inventory.setItem(visualSlot, alternate ? VISUAL_ITEM_2 : VISUAL_ITEM_1);
		if (++visualSlot == inventory.getSize()) {
			visualSlot = 0;
			alternate = !alternate;
		}
	}

	private void chargeUser() {
		StandardCheckoutPlugin plugin = StandardCheckoutPlugin.getInstance();
		StandardCheckoutClient client = plugin.getClient();

		Cart cart = context.getBean(Cart.class);
		StandardCheckoutChargeRequest request = new StandardCheckoutChargeRequest();
		request.setCart(cart);
		request.setItemName(cart.getTitle());
		request.setPurchaser(context.getPlayerId());

		StandardCheckoutChargeResponse response = client.charge(request);
		plugin.getServer().getScheduler().runTask(plugin, () -> handleResponse(response));
	}

	private void handleResponse(StandardCheckoutChargeResponse response) {
		if (context instanceof MutableFlowContext) {
			MutableFlowContext context = (MutableFlowContext) this.context;
			context.storeBean(response);
		}

		context.flow().next();
	}

	@Override
	public Stage next() {
		context.getPlayer().ifPresent(player -> {
			if (isInventoryOpen(player)) {
				player.closeInventory(); // TODO idea - giant checkmark as part of complete inside a chest instead
			}
		});

		StandardCheckoutChargeResponse response = context.getBean(StandardCheckoutChargeResponse.class);
		if (response.getState()) {
			return new PurchaseCompleteStage(context);
		}

		if (response.getError() != null) {
			if (response.getError() == StandardCheckoutError.PAYMENT_FAILED) {
				return new PurchaseFailedStage(context);
			}
			new RuntimeException("StandardCheckout error " + response.getError()).printStackTrace(); // TODO proper error handling
			return new ErrorStage(context);
		}

		return new PaymentDetailsRequiredStage(context);
	}

	@Override
	public void close() {
		visualTask.cancel();
		visualSlot = 0;

		context.getPlayer().ifPresent(player -> {
			if (context.getBean(StandardCheckoutChargeResponse.class) == null) {
				Tell.sendMessages(player, ChatColor.YELLOW + "We'll let you know in chat when your", ChatColor.YELLOW + "purchase is completed.");
			}

			if (isInventoryOpen(player)) {
				player.closeInventory();
			}
		});
	}

	@Override
	public void clickInventory(InventoryClickEvent event) {
		if (!isInventoryOpen(event.getWhoClicked())) {
			return;
		}

		event.setCancelled(true);
		event.setResult(Result.DENY);

		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			StandardCheckoutPlugin plugin = StandardCheckoutPlugin.getInstance();
			plugin.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
		}
	}

	@Override
	protected Inventory getInventory() {
		return inventory;
	}

}
