package org.apache.coyote.http11.controller;

import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;

public class HandlerMapping {
	private final Map<Path, Controller> controllers;

	public HandlerMapping() {
		this.controllers = Map.of(
			new Path("/login"), new LoginController(),
			new Path("/register"), new RegisterController());
	}

	public Controller getController(HttpRequest request) {
		Path path = request.getPath();
		return controllers.getOrDefault(path, new StaticController());
	}
}
