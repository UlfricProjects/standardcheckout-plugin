package com.standardcheckout.plugin.flow.stage.purchase;

import com.standardcheckout.plugin.flow.FlowContext;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeState;

public class ServerAuthorizationRequiredStage extends SkeletalAuthorizationRequiredStage {

	public ServerAuthorizationRequiredStage(FlowContext context) {
		super(context, "CLICK HERE TO AUTHORIZE PAYMENTS", StandardCheckoutChargeState.REQUIRES_AUTHORIZATION);
	}

}
