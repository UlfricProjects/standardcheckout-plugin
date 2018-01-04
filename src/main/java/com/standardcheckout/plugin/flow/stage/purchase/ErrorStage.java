package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.ChatColor;

import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.FinalStage;
import com.standardcheckout.plugin.language.Tell;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeState;

public class ErrorStage extends FinalStage {

	public ErrorStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		context.getPlayer().ifPresent(player -> Tell.sendMessages(player, ChatColor.RED + "An error prevented us from processing your payment"));

		context.flow().finish(StandardCheckoutChargeState.CANCELLED);
	}

	@Override
	public void close() {
	}

}
