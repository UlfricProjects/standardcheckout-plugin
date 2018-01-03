package com.standardcheckout.plugin.flow.stage.purchase;

import com.standardcheckout.plugin.flow.FlowContext;

public class AccountEnableRequiredStage extends SkeletalAuthorizationRequiredStage {

	public AccountEnableRequiredStage(FlowContext context) {
		super(context, "CLICK HERE TO REENABLE YOUR PAYMENTS");
	}

}
