package com.standardcheckout.plugin.language;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Tell {

	private final static int CENTER_PX = 154;

	public static void sendMessages(CommandSender target, String... messages) {
		sendFullWidth(target, '=');
		sendCenteredMessage(target, ChatColor.GREEN + "" + ChatColor.BOLD + "StandardCheckout");
		sendCenteredMessage(target, "");
		for (String message : messages) {
			sendCenteredMessage(target, message);
		}
		sendFullWidth(target, '=');
	}

	private static void sendFullWidth(CommandSender target, char character) {
		int length = DefaultFontInfo.getDefaultFontInfo(character).getLength();
		int count = CENTER_PX * 2 / length;
		StringBuilder message = new StringBuilder();
		for (int x = 0; x < count; x ++) {
			message.append(character);
		}
		target.sendMessage(message.toString());
	}

	private static void sendCenteredMessage(CommandSender target, String message) {
		if (message.isEmpty()) {
			target.sendMessage(message);
			return;
		}

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char character : message.toCharArray()) {
			if (character == ChatColor.COLOR_CHAR) {
				previousCode = true;
			} else if (previousCode == true) {
				previousCode = false;
				if (character == 'l' || character == 'L') {
					isBold = true;
				} else {
					isBold = false;
				}
			} else {
				DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(character);
				messagePxSize += isBold ? fontInfo.getBoldLength() : fontInfo.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder padding = new StringBuilder();
		while (compensated < toCompensate) {
			padding.append(' ');
			compensated += spaceLength;
		}
		target.sendMessage(padding + message);
	}

	private Tell() {
	}

}
