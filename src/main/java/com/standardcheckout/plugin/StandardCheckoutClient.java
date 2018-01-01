package com.standardcheckout.plugin;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeRequest;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeResponse;
import com.ulfric.buycraft.sco.model.StandardCheckoutError;

import net.buycraft.plugin.bukkit.BuycraftPlugin;
import okhttp3.CacheControl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StandardCheckoutClient {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private final OkHttpClient client = new OkHttpClient();
	private final Gson gson = new Gson();

	public StandardCheckoutChargeResponse charge(StandardCheckoutChargeRequest request) {
		if (request.getBuycraftToken() == null && request.getPrice() == null) {
			BuycraftPlugin buycraft = JavaPlugin.getPlugin(BuycraftPlugin.class);
			request.setBuycraftToken(buycraft.getConfiguration().getServerKey());
		}

		StandardCheckoutPlugin plugin = StandardCheckoutPlugin.getInstance();

		if (request.getScoToken() == null) {
			String token = plugin.getToken();
			request.setScoToken(token);
		}

		if (request.getWebstoreId() == null) {
			String webstoreId = plugin.getWebstoreId();
			request.setWebstoreId(webstoreId);
		}

		String json = gson.toJson(request);
		Request post = new Request.Builder()
				.cacheControl(CacheControl.FORCE_NETWORK)
				.addHeader("Accept", "application/json")
				.addHeader("User-Agent", "StandardCheckout")
				.url(plugin.getApiGateway() + "/api/charge")
				.post(RequestBody.create(JSON, json))
				.build();

		try {
			Response response = client.newCall(post).execute();
			String body = response.body().string();
			return gson.fromJson(body, StandardCheckoutChargeResponse.class);
		} catch (IOException exception) {
			exception.printStackTrace(); // TODO error handling
			return internalError();
		}
	}

	private StandardCheckoutChargeResponse internalError() {
		StandardCheckoutChargeResponse response = new StandardCheckoutChargeResponse();
		response.setError(StandardCheckoutError.INTERNAL_ERROR);
		return response;
	}

}
