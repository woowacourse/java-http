package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterRequestHandler implements RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(RegisterRequestHandler.class);

	private static final String REQUEST_PATH = "/register";
	private static final String REGISTER_PAGE_PATH = "/register.html";

	@Override
	public boolean canHandle(final Request request) {
		return request.hasPath(REQUEST_PATH);
	}

	@Override
	public Response handle(final Request request) {
		if (request.hasMethod(HttpMethod.GET)) {
			return doGet(request);
		} else if (request.hasMethod(HttpMethod.POST)) {
			return doPost(request);
		}
		return Response.notFound();
	}

	private Response doGet(final Request request) {
		return Response.ok(ResourceProvider.provide(REGISTER_PAGE_PATH), MimeType.fromPath(REGISTER_PAGE_PATH));
	}

	private Response doPost(final Request request) {
		final var account = request.findBodyField("account");
		final var password = request.findBodyField("password");
		final var email = request.findBodyField("email");

		validateFields(account, password, email);

		return register(account, password, email);
	}

	private void validateFields(final String account, final String password, final String email) {
		if (account == null || password == null || email == null) {
			throw new IllegalArgumentException("필요한 정보가 없습니다.");
		}
	}

	private Response register(final String account, final String password, final String email) {
		InMemoryUserRepository.findByAccount(account)
			.ifPresent(user -> {
				throw new IllegalArgumentException("이미 존재하는 계정입니다.");
			});

		InMemoryUserRepository.save(new User(account, password, email));
		log.info("[REGISTER SUCCESS] account: {}", account);
		return Response.redirect("/index.html");
	}
}
