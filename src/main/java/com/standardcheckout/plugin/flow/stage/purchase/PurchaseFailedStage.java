package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.ChatColor;

import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.FinalStage;
import com.standardcheckout.plugin.language.Tell;

public class PurchaseFailedStage extends FinalStage {

	public PurchaseFailedStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		// TODO send them a link to update their payment card
		context.getPlayer().ifPresent(player -> Tell.sendMessages(player, ChatColor.RED + "Your payment card could not be charged."));

		context.flow().finish(false);
	}

	@Override
	public void close() {
	}

}
