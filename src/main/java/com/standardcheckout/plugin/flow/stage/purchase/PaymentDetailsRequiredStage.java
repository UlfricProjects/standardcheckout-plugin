package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.ChatColor;

import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.FinalStage;
import com.standardcheckout.plugin.language.Link;
import com.standardcheckout.plugin.language.Tell;

public class PaymentDetailsRequiredStage extends FinalStage { // TODO use text component for the url

	public PaymentDetailsRequiredStage(FlowContext context) {
		super(context);
	}

	@Override
	public void play() {
		context.getPlayer().ifPresent(player -> {
			StandardCheckoutPlugin plugin = StandardCheckoutPlugin.getInstance();
			Link link = new Link();
			link.setTitle(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "CLICK HERE TO COMPLETE YOUR PURCHASE");
			link.setUrl(plugin.getApiGateway() + "/?webstore=" + plugin.getWebstoreId() + "&username=" + player.getName());
			Tell.sendMessages(player, link, "");
		});

		context.flow().finish(false);
	}

	@Override
	public void close() {
	}

}
