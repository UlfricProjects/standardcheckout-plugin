package com.standardcheckout.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.standardcheckout.plugin.internal.PurchaseFlowListener;

public class StandardCheckoutPlugin extends JavaPlugin {

	public static StandardCheckoutPlugin getInstance() {
		return JavaPlugin.getPlugin(StandardCheckoutPlugin.class);
	}

	private StandardCheckoutClient client;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.client = new StandardCheckoutClient();
		Bukkit.getPluginManager().registerEvents(new PurchaseFlowListener(), this);
	}

	public StandardCheckoutClient getClient() {
		return client;
	}

	public String getWebstoreId() {
		return getConfig().getString("standard-checkout-webstore");
	}

	public String getApiGateway() {
		return getConfig().getString("standard-checkout-gateway");
	}

}
