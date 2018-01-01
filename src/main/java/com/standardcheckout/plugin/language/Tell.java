package com.standardcheckout.plugin.language;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Tell {

	private final static int CENTER_PX = 154;

	public static void sendMessages(CommandSender target, Object... messages) {
		sendFullWidth(target, '*');
		sendCenteredMessage(target, ChatColor.GREEN + "" + ChatColor.BOLD + "Standard Checkout");
		sendCenteredMessage(target, "");
		for (Object message : messages) {
			sendCenteredMessage(target, message);
		}
		sendFullWidth(target, '*');
	}

	private static void sendFullWidth(CommandSender target, char character) {
		int length = DefaultFontInfo.getDefaultFontInfo(character).getLength();
		StringBuilder message = new StringBuilder();
		for (int x = 0; x < CENTER_PX * 2; x += length) {
			message.append(character);
		}
		target.sendMessage(message.toString());
	}

	private static void sendCenteredMessage(CommandSender target, Object message) {
		if (message instanceof String) {
			target.sendMessage(withWhitespace((String) message));
		} else if (message instanceof Link) {
			Link link = (Link) message;
			try {
				TextComponent component = new TextComponent(TextComponent.fromLegacyText(withWhitespace(link.getTitle())));
				component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link.getUrl()));
				if (target instanceof Player) {
					((Player) target).spigot().sendMessage(component);
				} else {
					target.spigot().sendMessage(component);
				}
			} catch (Throwable thatsOk) {
				sendCenteredMessage(target, link.getTitle());
				sendCenteredMessage(target, ChatColor.getLastColors(link.getTitle()) + ChatColor.UNDERLINE + link.getUrl());
			}
		} else {
			throw new IllegalArgumentException(message + " is not a " + String.class + " or a " + Link.class);
		}
	}

	private static String withWhitespace(String message) {
		if (message.isEmpty()) {
			return message;
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
		return padding + message;
	}

	private Tell() {
	}

}
