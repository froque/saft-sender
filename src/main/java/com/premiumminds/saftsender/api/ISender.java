package com.premiumminds.saftsender.api;

import java.io.OutputStream;

public interface ISender {

	public void send(Arguments arguments, OutputStream stream);
}
