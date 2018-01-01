package com.standardcheckout.plugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.standardcheckout.plugin.StandardCheckoutPlugin;

public class StandardCheckoutCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		StandardCheckoutPlugin.getInstance().reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "Config reloaded.");
		return true;
	}

}
