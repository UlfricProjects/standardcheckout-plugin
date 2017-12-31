package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.ChatColor;

import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.FinalStage;
import com.standardcheckout.plugin.language.Tell;

public class PackageDeliveryStage extends FinalStage {

	public PackageDeliveryStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		context.getPlayer().ifPresent(player -> Tell.sendMessages(player, ChatColor.LIGHT_PURPLE + "Your purchase was completed!"));

		context.flow().finish(true);
	}

	@Override
	public void close() {
	}

}
