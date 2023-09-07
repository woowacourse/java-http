package nextstep.jwp.handler;

import org.apache.catalina.RequestHandler;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterRequestHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(RegisterRequestHandler.class);

	private static final String REQUEST_PATH = "/register";
	private static final String PAGE_PATH = "/register.html";

	public RegisterRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	protected Response doGet(final Request request) {
		return Response.ok(ResourceProvider.provide(PAGE_PATH), MimeType.fromPath(PAGE_PATH));
	}

	@Override
	protected Response doPost(final Request request) {
		final var account = request.findBodyField("account");
		final var password = request.findBodyField("password");
		final var email = request.findBodyField("email");

		return register(account, password, email);
	}

	private Response register(final String account, final String password, final String email) {
		if (isInvalidInput(account, password, email) || isDuplicatedAccount(account)) {
			return Response.badRequest();
		}

		InMemoryUserRepository.save(new User(account, password, email));
		log.info("[REGISTER SUCCESS] account: {}", account);
		return Response.redirect("/index.html");
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
