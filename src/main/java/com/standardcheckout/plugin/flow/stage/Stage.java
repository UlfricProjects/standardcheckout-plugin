package com.standardcheckout.plugin.flow.stage;

import java.io.Closeable;
import java.util.Objects;

import com.standardcheckout.plugin.flow.FlowContext;

public abstract class Stage implements Closeable {

	protected final FlowContext context;

	public Stage(FlowContext context) {
		Objects.requireNonNull(context, "context");
		this.context = context;
	}

	public abstract void play();

	public abstract Stage next();

	@Override
	public abstract void close();

}
