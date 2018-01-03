package com.standardcheckout.plugin;

public interface StandardCheckoutConfig {

	String getWebstoreId();

	String getApiGateway();

	String getSecret();

	boolean getCacheControlForceNetwork();

}
