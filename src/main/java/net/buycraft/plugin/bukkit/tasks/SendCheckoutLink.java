package net.buycraft.plugin.bukkit.tasks;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.standardcheckout.plugin.flow.PurchaseFlow;

import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.buycraft.plugin.client.ApiException;
import net.buycraft.plugin.data.responses.CheckoutUrlResponse;

/**
 * Override the BuycraftX plugin's SendCheckoutLink functionality
 * Upstream: https://github.com/BuycraftPlugin/BuycraftX/blob/master/bukkit/src/main/java/net/buycraft/plugin/bukkit/tasks/SendCheckoutLink.java
 */
public class SendCheckoutLink implements Runnable {

	private BuycraftPlugin plugin;
	private int id;
	private Player player;
	private boolean isCategory;
	private CommandSender sender = null;

	public SendCheckoutLink(BuycraftPlugin plugin, int id, Player player) {
		this.plugin = plugin;
		this.id = id;
		this.player = player;
		this.isCategory = false;
		this.sender = null;
	}

	public SendCheckoutLink(BuycraftPlugin plugin, int id, Player player, boolean isCategory, CommandSender sender) {
		this.plugin = plugin;
		this.id = id;
		this.player = player;
		this.isCategory = isCategory;
		this.sender = sender;
	}

	@Override
	public void run() {
		if (isCategory) {
			CheckoutUrlResponse response;
			try {
				response = plugin.getApiClient().getCategoryUri(player.getName(), id);
			} catch (IOException | ApiException e) {
				if (sender == null) {
					player.sendMessage(ChatColor.RED + plugin.getI18n().get("cant_check_out") + " " + e.getMessage());
				} else {
					sender.sendMessage(ChatColor.RED + plugin.getI18n().get("cant_check_out") + " " + e.getMessage());
				}
				return;
			}

			player.sendMessage(ChatColor.STRIKETHROUGH + "                                            ");
			player.sendMessage(ChatColor.GREEN + plugin.getI18n().get("to_view_this_category"));
			player.sendMessage(ChatColor.BLUE + ChatColor.UNDERLINE.toString() + response.getUrl());
			player.sendMessage(ChatColor.STRIKETHROUGH + "                                            ");
			return;
		}

		Player player = player();
		if (player == null) {
			sender.sendMessage(ChatColor.RED + plugin.getI18n().get("cant_check_out") + " console cannot buy things");
			return;
		}

		PurchaseFlow.builder()
			.name("Buycraft Package #" + id) // TODO use actual package name
			.withItem(id)
			.begin(player);
	}

	private Player player() {
		if (player != null) {
			return player;
		}

		if (sender instanceof Player) {
			return (Player) sender;
		}

		return null;
	}

}