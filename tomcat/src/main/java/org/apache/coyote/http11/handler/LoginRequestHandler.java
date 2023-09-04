package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

/**
 * TODO: JSESSIONID이 없을 때 Set-Cookie 해주도록 수정
 */
public class LoginRequestHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

	private static final String REQUEST_PATH = "/login";
	private static final String LOGIN_PAGE_PATH = "/login.html";
	private static final String REDIRECT_LOCATION = "/index.html";

	public LoginRequestHandler() {
		super(LoginRequestHandler.REQUEST_PATH);
	}

	@Override
	protected Response doGet(final Request request) {
		if (isSessionExist(request)) {
			return Response.redirect(REDIRECT_LOCATION);
		}
		return Response.ok(ResourceProvider.provide(LOGIN_PAGE_PATH), MimeType.fromPath(LOGIN_PAGE_PATH));
	}

	private boolean isSessionExist(final Request request) {
		final var sessionId = request.findSession();
		if (sessionId == null) {
			return false;
		}

		return SessionManager.findById(sessionId) != null;
	}

	@Override
	protected Response doPost(final Request request) {
		final var account = request.findBodyField("account");
		final var password = request.findBodyField("password");
		validateFields(account, password);
		return login(account, password);
	}

	private void validateFields(final String account, final String password) {
		if (account == null || password == null) {
			throw new IllegalArgumentException("필요한 정보가 없습니다.");
		}
	}

	private Response login(final String account, final String password) {
		final var optionalUser = InMemoryUserRepository.findByAccount(account);
		if (optionalUser.isEmpty()) {
			return Response.unauthorized();
		}
		final var user = optionalUser.get();
		if (!user.checkPassword(password)) {
			return Response.unauthorized();
		}

		final var cookies = Cookies.empty();
		final var session = createSession(user);
		cookies.addSession(session.getId());

		log.info("[LOGIN SUCCESS] account: {}", account);
		return Response.redirectWithCookie(REDIRECT_LOCATION, cookies);
	}

	private Session createSession(final User user) {
		final var session = Session.create();
		session.setAttribute("user", user);
		SessionManager.add(session);
		return session;
	}
}
