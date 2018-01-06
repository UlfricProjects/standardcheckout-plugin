package com.standardcheckout.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.standardcheckout.plugin.command.ChargePlayerCommand;
import com.standardcheckout.plugin.command.StandardCheckoutCommand;
import com.standardcheckout.plugin.internal.PurchaseFlowListener;

import net.buycraft.plugin.bukkit.tasks.SendCheckoutLink;

public class StandardCheckoutPlugin extends JavaPlugin implements StandardCheckoutConfig {

	public static StandardCheckoutPlugin getInstance() {
		return JavaPlugin.getPlugin(StandardCheckoutPlugin.class);
	}

	private StandardCheckoutClient client;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.client = new StandardCheckoutClient(this);

		getCommand("chargeplayer").setExecutor(new ChargePlayerCommand());
		getCommand("standardcheckout").setExecutor(new StandardCheckoutCommand());
		getServer().getPluginManager().registerEvents(new PurchaseFlowListener(), this);

		if (getConfig().getBoolean("override-buycraft-buy-gui")) {
			String simpleName = SendCheckoutLink.class.getSimpleName();
			if (JavaPlugin.getProvidingPlugin(SendCheckoutLink.class) == this) {
				getLogger().info("Overriding the /buy gui at " + simpleName);
			} else {
				getLogger().info("Could not override the /buy gui");
			}
		}
	}

	@Override
	public void onDisable() {
		if (client != null) {
			client.close();
		}
	}

	public StandardCheckoutClient getClient() {
		return client;
	}

	@Override
	public String getWebstoreId() {
		return getConfig().getString("webstore");
	}

	@Override
	public String getApiGateway() {
		return getConfig().getString("gateway");
	}

	@Override
	public String getSecret() {
		return getConfig().getString("secret");
	}

	@Override
	public boolean getCacheControlForceNetwork() {
		return getConfig().getBoolean("force-network");
	}

}
