package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.ChatColor;

import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.FinalStage;
import com.standardcheckout.plugin.language.Tell;

public class PaymentDetailsRequiredStage extends FinalStage {

	public PaymentDetailsRequiredStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		context.getPlayer().ifPresent(player -> {
			StandardCheckoutPlugin plugin = StandardCheckoutPlugin.getInstance();
			Tell.sendMessages(player,
					ChatColor.LIGHT_PURPLE + "OPEN LINK TO COMPLETE YOUR PURCHASE",
					ChatColor.AQUA + "" + ChatColor.UNDERLINE + plugin.getApiGateway() + "/?webstore=" + plugin.getWebstoreId() + "?username=" + player.getName());
		});

		context.flow().finish(false);
	}

	@Override
	public void close() {
	}

}
