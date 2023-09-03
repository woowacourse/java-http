package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;

public class LoginRequestHandler implements RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

	private static final String REQUEST_PATH = "/login";
	private static final String LOGIN_PAGE_PATH = "/login.html";

	@Override
	public boolean canHandle(HttpRequest request) {
		return request.hasPath(REQUEST_PATH);
	}

	@Override
	public HttpResponse handle(final HttpRequest request) {
		final var account = request.findQueryParam("account");
		final var password = request.findQueryParam("password");

		if (account != null && password != null) {
			findUser(account, password);
		}

		final var responseBody = ResourceProvider.provide(LOGIN_PAGE_PATH);
		return HttpResponse.ok(responseBody, MimeType.fromPath(LOGIN_PAGE_PATH));
	}

	private void findUser(final String account, final String password) {
		InMemoryUserRepository.findByAccount(account)
			.ifPresent(user -> {
				if (user.checkPassword(password)) {
					log.info(user.toString());
				}
			});
	}
}
