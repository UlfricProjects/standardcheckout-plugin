package com.standardcheckout.plugin.helper;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHelper {

	public static boolean tryToPlaySound(Player player, String sound, float volume, float pitch) {
		try {
			player.playSound(player.getLocation(), Sound.valueOf(sound.toUpperCase()), volume, pitch);
			return true;
		} catch (Exception thatsOk) {
			return false;
		}
	}

	private SoundHelper() {
	}

}
