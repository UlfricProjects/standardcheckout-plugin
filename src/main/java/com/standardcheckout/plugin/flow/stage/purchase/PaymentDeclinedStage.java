package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.ChatColor;

import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.FinalStage;
import com.standardcheckout.plugin.language.Tell;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeState;

public class PaymentDeclinedStage extends FinalStage {

	public PaymentDeclinedStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		// TODO send them a link to update their payment card
		context.getPlayer().ifPresent(player -> Tell.sendMessages(player, ChatColor.RED + "Your payment card could not be charged."));

		context.flow().finish(StandardCheckoutChargeState.DECLINED);
	}

	@Override
	public void close() {
	}

}
