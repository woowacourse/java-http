package nextstep.jwp.handler;

import org.apache.catalina.RequestHandler;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Cookie;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

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
		return login(account, password);
	}

	private Response login(final String account, final String password) {
		if (isInvalidInput(account, password)) {
			return Response.badRequest();
		}

		final var user = InMemoryUserRepository.findByAccount(account);
		if (user.isEmpty() || !user.get().checkPassword(password)) {
			return Response.unauthorized();
		}

		final var session = createSession(user.get());
		final var cookie = Cookie.session(session.getId());

		log.info("[LOGIN SUCCESS] account: {}", account);
		return Response.redirect(REDIRECT_LOCATION)
			.addCookie(cookie);
	}

	private boolean isInvalidInput(final String account, final String password) {
		return isBlank(account) || isBlank(password);
	}

	private boolean isBlank(final String value) {
		return value == null || value.isBlank();
	}

	private Session createSession(final User user) {
		final var session = Session.create();
		session.setAttribute("user", user);
		SessionManager.add(session);
		return session;
	}
}
