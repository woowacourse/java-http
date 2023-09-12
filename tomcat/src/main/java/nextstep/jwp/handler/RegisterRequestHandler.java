package nextstep.jwp.handler;

import static org.apache.coyote.response.StatusCode.*;

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

public class RegisterRequestHandler extends AbstractHandler {

	private static final Logger log = LoggerFactory.getLogger(RegisterRequestHandler.class);

	private static final String REQUEST_PATH = "/register";
	private static final String PAGE_PATH = "/register.html";

	public RegisterRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	protected void doGet(final Request request, final Response response) {
		response.setStatusCode(StatusCode.OK);
		response.setResponseBody(ResourceProvider.provide(PAGE_PATH), MimeType.fromPath(PAGE_PATH));
	}

	@Override
	protected void doPost(final Request request, final Response response) {
		final var account = request.findBodyField("account");
		final var password = request.findBodyField("password");
		final var email = request.findBodyField("email");

		register(response, account, password, email);
	}

	private void register(final Response response, final String account, final String password,
		final String email) {
		if (isInvalidInput(account, password, email) || isDuplicatedAccount(account)) {
			response.redirect(BAD_REQUEST.getResourcePath());
			return;
		}

		final var user = new User(account, password, email);
		InMemoryUserRepository.save(user);

		final var session = createSession(user);
		final var cookie = Cookie.session(session.getId());

		log.info("[REGISTER SUCCESS] account: {}", account);
		response.redirect("/index.html");
		response.addCookie(cookie);
	}

	private Session createSession(final User user) {
		final var session = Session.create();
		session.setAttribute("account", user.getAccount());
		SessionManager.add(session);
		return session;
	}

	private boolean isInvalidInput(final String account, final String password, final String email) {
		return isBlank(account) || isBlank(password) || isBlank(email);
	}

	private boolean isBlank(final String value) {
		return value == null || value.isBlank();
	}

	private boolean isDuplicatedAccount(final String account) {
		return InMemoryUserRepository.findByAccount(account).isPresent();
	}
}
