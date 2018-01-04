package com.standardcheckout.plugin.flow.stage.purchase;

import com.standardcheckout.plugin.flow.FlowContext;
import com.ulfric.buycraft.sco.model.StandardCheckoutChargeState;

public class PaymentDetailsRequiredStage extends SkeletalAuthorizationRequiredStage {

	public PaymentDetailsRequiredStage(FlowContext context) {
		super(context, "CLICK HERE TO ADD PAYMENT DETAILS", StandardCheckoutChargeState.REQUIRES_PAYMENT_METHOD);
	}

}
