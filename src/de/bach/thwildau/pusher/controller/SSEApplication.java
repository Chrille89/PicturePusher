package de.bach.thwildau.pusher.controller;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("services")
public class SSEApplication extends ResourceConfig {
	public SSEApplication() {
		super(Pusher.class, SseFeature.class);
	}
}
