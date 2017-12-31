package com.standardcheckout.plugin.flow.stage.purchase;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.InventoryStage;
import com.standardcheckout.plugin.flow.stage.Stage;
import com.standardcheckout.plugin.helper.SoundHelper;

public class PurchaseSuccessStage extends InventoryStage {

	private static final ItemStack FILLER = new ItemStack(Material.STAINED_GLASS_PANE, 1);
	private static final ItemStack CHECK = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);

	static {
		addMeta(FILLER);
		addMeta(CHECK);
	}

	private static void addMeta(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "✔" + ChatColor.BOLD + " Purchase Complete");
		meta.setLore(Arrays.asList(
				"",
				ChatColor.YELLOW + "Click to receive your package"));
		item.setItemMeta(meta);
	}

	private Inventory inventory;
	private int slot;

	public PurchaseSuccessStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		Player player = context.getRequiredPlayer();
		inventory = Bukkit.createInventory(player, 54, ChatColor.BOLD + "Standard Checkout");
		add(FILLER, 8);
		add(CHECK);
		add(FILLER, 7);
		add(CHECK, 2);
		add(FILLER, 6);
		add(CHECK, 2);
		add(FILLER, 2);
		add(CHECK);
		add(FILLER, 3);
		add(CHECK, 2);
		add(FILLER, 3);
		add(CHECK, 2);
		add(FILLER);
		add(CHECK, 2);
		add(FILLER, 5);
		add(CHECK, 3);
		add(FILLER, 4);
		player.openInventory(inventory);
		SoundHelper.tryToPlaySound(player, "ENTITY_ARROW_HIT_PLAYER", 1, 1);
	}

	private void add(ItemStack item, int times) {
		for (int x = 0; x < times; x++) {
			add(item);
		}
	}

	private void add(ItemStack item) {
		inventory.setItem(slot++, item);
	}

	@Override
	public Stage next() {
		return new PackageDeliveryStage(context);
	}

	@Override
	public void close() {
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
			SoundHelper.tryToPlaySound(player, "UI_BUTTON_CLICK", 0.75F, 1);
		}
		event.getWhoClicked().closeInventory();
		context.flow().next();
	}

	@Override
	protected Inventory getInventory() {
		return inventory;
	}

}
