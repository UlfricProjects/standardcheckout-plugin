package com.standardcheckout.plugin.flow.stage.confirmation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.MutableFlowContext;
import com.standardcheckout.plugin.flow.PurchaseDetails;
import com.standardcheckout.plugin.flow.stage.InventoryStage;
import com.standardcheckout.plugin.flow.stage.Stage;
import com.standardcheckout.plugin.flow.stage.purchase.PurchaseStage;
import com.standardcheckout.plugin.helper.SoundHelper;
import com.standardcheckout.plugin.language.Tell;
import com.ulfric.buycraft.model.Item;

import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.buycraft.plugin.data.Package;
import net.buycraft.plugin.shared.tasks.ListingUpdateTask;

public class ConfirmationStage extends InventoryStage {

	private static final String VERIFY_PURCHASE_NAME = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Verify Purchase for ";

	private Inventory inventory;

	public ConfirmationStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		Player player = context.getRequiredPlayer();
		PurchaseDetails cart = context.getBean(PurchaseDetails.class);
		BigDecimal cost = calculateCost(cart);
		String costString = BigDecimal.ZERO.compareTo(cost) == 0 ? "UNKNOWN PRICE" : NumberFormat.getCurrencyInstance().format(cost);
		inventory = Bukkit.createInventory(player, InventoryType.DISPENSER, ChatColor.BOLD + "Standard Checkout");
		ItemStack verificationItem = new ItemStack(Material.EMERALD);
		ItemMeta meta = verificationItem.getItemMeta();
		meta.setDisplayName(VERIFY_PURCHASE_NAME + costString);
		meta.setLore(Arrays.asList(
				"",
				ChatColor.YELLOW + "To confirm your purchase",
				ChatColor.YELLOW + "left-click here. To cancel",
				ChatColor.YELLOW + "press the ESC key."));
		verificationItem.setItemMeta(meta);
		inventory.setItem(4, verificationItem);
		player.openInventory(inventory);
	}

	private BigDecimal calculateCost(PurchaseDetails cart) {
		if (cart.getPrice() != null) {
			return cart.getPrice().setScale(2, RoundingMode.HALF_UP);
		}

		BigDecimal total = BigDecimal.ZERO;
		ListingUpdateTask listing = JavaPlugin.getPlugin(BuycraftPlugin.class).getListingUpdateTask();
		for (Item item : cart.getCart().getItems()) {
			Package packge = listing.getPackageById(item.getId());
			if (packge != null) {
				total = total.add(packge.getEffectivePrice().multiply(quantity(item)));
			}
		}
		return total.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal quantity(Item item) {
		return item.getQuantity() == null || item.getQuantity().equals(0) ? BigDecimal.ONE : BigDecimal.valueOf(item.getQuantity());
	}

	@Override
	public Stage next() {
		ConfirmationContext verification = context.getBean(ConfirmationContext.class);
		if (verification == null || !verification.getVerified()) {
			return null;
		}

		return new PurchaseStage(context);
	}

	@Override
	public void close() {
		context.getPlayer().ifPresent(player -> {
			ConfirmationContext verification = context.getBean(ConfirmationContext.class);
			if (verification == null || !verification.getVerified()) {
				Tell.sendMessages(player, ChatColor.YELLOW + "Your purchase was cancelled.");
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

		ItemStack item = event.getCurrentItem();
		if (item == null) {
			return;
		}

		if (event.getInventory() == null) {
			return;
		}

		if (item.getType() != Material.EMERALD) {
			return;
		}

		if (!item.hasItemMeta()) {
			return;
		}

		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName() || !meta.getDisplayName().startsWith(VERIFY_PURCHASE_NAME)) {
			return;
		}

		ConfirmationContext verification = context.getBean(ConfirmationContext.class);
		if (verification == null) {
			if (context instanceof MutableFlowContext) {
				MutableFlowContext mutable = (MutableFlowContext) context;
				verification = new ConfirmationContext();
				mutable.storeBean(verification);
			}
		}
		if (verification != null) {
			verification.setVerified(true);
		}

		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			SoundHelper.tryToPlaySound(player, "UI_BUTTON_CLICK", 0.75F, 1);
		}
		context.flow().next();
	}

	@Override
	protected Inventory getInventory() {
		return inventory;
	}

}
