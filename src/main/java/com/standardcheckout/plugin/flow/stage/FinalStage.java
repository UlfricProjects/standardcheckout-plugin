package com.standardcheckout.plugin.flow.stage;

import com.standardcheckout.plugin.flow.FlowContext;

public abstract class FinalStage extends Stage {

	public FinalStage(FlowContext context) {
		super(context);
	}

	@Override
	public Stage next() {
		return null;
	}

}
