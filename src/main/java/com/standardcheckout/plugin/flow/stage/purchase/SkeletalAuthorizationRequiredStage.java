package com.standardcheckout.plugin.flow.stage.purchase;

import org.bukkit.ChatColor;

import com.standardcheckout.plugin.StandardCheckoutPlugin;
import com.standardcheckout.plugin.flow.FlowContext;
import com.standardcheckout.plugin.flow.stage.FinalStage;
import com.standardcheckout.plugin.language.Link;
import com.standardcheckout.plugin.language.Tell;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeState;

public class SkeletalAuthorizationRequiredStage extends FinalStage {

	private final String authorizationMessage;
	private final StandardCheckoutChargeState state;

	public SkeletalAuthorizationRequiredStage(FlowContext context, String authorizationMessage, StandardCheckoutChargeState state) {
		super(context);
		this.authorizationMessage = authorizationMessage;
		this.state = state;
	}

	@Override
	public final void play() {
		context.getPlayer().ifPresent(player -> {
			StandardCheckoutPlugin plugin = StandardCheckoutPlugin.getInstance();
			Link link = new Link();
			link.setTitle(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + authorizationMessage);
			link.setUrl(plugin.getApiGateway() + "/?webstore=" + plugin.getWebstoreId() + "&username=" + player.getName());
			Tell.sendMessages(player, link, "");
		});

		context.flow().finish(state);
	}

	@Override
	public final void close() {
	}

}
