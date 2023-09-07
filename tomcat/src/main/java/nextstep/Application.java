package nextstep;

import java.util.Set;

import org.apache.catalina.HandlerMapping;
import org.apache.catalina.startup.Tomcat;

import nextstep.jwp.handler.BaseRequestHandler;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import nextstep.jwp.handler.StaticContentRequestHandler;

public class Application {

	public static void main(String[] args) {

		final var handlers = Set.of(
			new BaseRequestHandler(),
			new LoginRequestHandler(),
			new RegisterRequestHandler()
		);
		final var defaultHandler = new StaticContentRequestHandler();
		final var handlerMapping = new HandlerMapping(handlers, defaultHandler);

		final var tomcat = new Tomcat(handlerMapping);
		tomcat.start();
	}
}
