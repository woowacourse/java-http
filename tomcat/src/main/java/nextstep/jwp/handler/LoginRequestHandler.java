package nextstep.jwp.handler;

import static org.apache.coyote.response.StatusCode.*;

import java.util.Optional;

import org.apache.catalina.AbstractHandler;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Cookie;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginRequestHandler extends AbstractHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

	private static final String REQUEST_PATH = "/login";
	private static final String LOGIN_PAGE_PATH = "/login.html";
	private static final String REDIRECT_LOCATION = "/index.html";

	public LoginRequestHandler() {
		super(LoginRequestHandler.REQUEST_PATH);
	}

	@Override
	protected void doGet(final Request request, final Response response) {
		findAccountInSession(request)
			.ifPresentOrElse(
				account -> {
					logSuccess(account);
					response.redirect(REDIRECT_LOCATION);
				},
				() -> {
					response.setStatusCode(StatusCode.OK);
					response.setResponseBody(ResourceProvider.provide(LOGIN_PAGE_PATH),
						MimeType.fromPath(LOGIN_PAGE_PATH));
				});
	}

	private void logSuccess(final String account) {
		log.info("[LOGIN SUCCESS] account: {}", account);
	}

	private Optional<String> findAccountInSession(final Request request) {
		final var sessionId = request.findSessionId();
		if (sessionId.isEmpty()) {
			return Optional.empty();
		}

		final var session = SessionManager.findById(sessionId.get());
		if (session == null) {
			return Optional.empty();
		}

		final var account = session.getAttribute("account");
		if (account == null) {
			return Optional.empty();
		}

		return Optional.of(account);
	}

	@Override
	protected void doPost(final Request request, final Response response) {
		final var account = request.findBodyField("account");
		final var password = request.findBodyField("password");
		login(response, account, password);
	}

	private void login(final Response response, final String account, final String password) {
		if (isInvalidInput(account, password)) {
			response.redirect(BAD_REQUEST.getResourcePath());
			return;
		}

		final var user = InMemoryUserRepository.findByAccount(account);
		if (user.isEmpty() || !user.get().checkPassword(password)) {
			response.redirect(UNAUTHORIZED.getResourcePath());
			return;
		}

		final var session = createSession(user.get());
		final var cookie = Cookie.session(session.getId());

		logSuccess(account);
		response.redirect(REDIRECT_LOCATION);
		response.addCookie(cookie);
	}

	private boolean isInvalidInput(final String account, final String password) {
		return isBlank(account) || isBlank(password);
	}

	private boolean isBlank(final String value) {
		return value == null || value.isBlank();
	}

	private Session createSession(final User user) {
		final var session = Session.create();
		session.setAttribute("account", user.getAccount());
		SessionManager.add(session);
		return session;
	}
}
