package com.standardcheckout.plugin.command;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.PurchaseCallback;
import com.standardcheckout.plugin.flow.PurchaseFlow;

public class ChargePlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 4) {
			return false;
		}

		// /chargeplayer packet item_name [$25 or id*quantity,id2,id3] command__to__run command__to__run2 give__{name}__diamond__64

		Player player = Bukkit.getPlayerExact(args[0]);
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Player " + args[0] + " not found");
			return true;
		}

		PurchaseFlow.Builder builder = PurchaseFlow.builder()
			.name(args[1].replace('_', ' '));

		String priceToParse = args[2];
		if (priceToParse.startsWith("$")) {
			priceToParse = priceToParse.substring(1);
			BigDecimal price = new BigDecimal(priceToParse).setScale(2, RoundingMode.HALF_UP);
			builder = builder.price(price);
		} else {
			for (String packge : priceToParse.split(Pattern.quote(","))) {
				String[] packgeAndQuantity = packge.split(Pattern.quote("*"));
				if (packgeAndQuantity.length > 1) {
					builder.withItem(Integer.parseInt(packgeAndQuantity[0]), Integer.parseInt(packgeAndQuantity[1]));
				} else {
					builder.withItem(Integer.parseInt(packge), 1);
				}
			}
		}

		List<String> commands = new ArrayList<>();
		for (int x = 3; x < args.length; x++) {
			commands.add(args[x]);
		}
		builder = builder.callback(new PurchaseCallback() {
			@Override
			public void success(OfflinePlayer player) {
				Server server = StandardCheckoutPlugin.getInstance().getServer();
				for (String command : commands) {
					command = command.replace("__", " ");
					command = command.replace("{name}", player.getName());
					command = command.replace("{uuid}", player.getUniqueId().toString());
					server.dispatchCommand(server.getConsoleSender(), command);
				}
			}

			@Override
			public void failure(OfflinePlayer player) {
			}
		});

		builder.begin(player);

		return true;
	}

}
