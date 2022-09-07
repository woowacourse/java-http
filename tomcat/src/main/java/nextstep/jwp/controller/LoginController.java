package nextstep.jwp.controller;

import static org.apache.coyote.http11.http.session.Session.*;

import java.util.Optional;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.session.Session;

import nextstep.jwp.exception.InvalidLoginException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;

public class LoginController extends AbstractController {

	private static final String LOGIN_HTML = "login.html";
	private static final String UNAUTHORIZED_HTML = "401.html";
	private static final String LOGIN_USER = "user";

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) {
		Session session = request.getSession();
		Optional<Object> sessionAttribute = session.getAttribute(LOGIN_USER);

		sessionAttribute.ifPresentOrElse(
			user -> handleRedirect(response),
			() -> handleHtml(HttpStatus.OK, LOGIN_HTML, response)
		);
	}

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
		String account = request.getQueryString("account");
		String password = request.getQueryString("password");

		try {
			User user = LoginService.login(account, password);
			handleRedirect(response);
			addLoginSession(request, response, user);
		} catch (InvalidLoginException e) {
			handleHtml(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_HTML, response);
		}
	}

	private void addLoginSession(HttpRequest request, HttpResponse response, User user) {
		Session session = request.getSession();
		session.setAttribute(LOGIN_USER, user);
		response.addCookie(JSESSIONID, session.getId());
	}
}
