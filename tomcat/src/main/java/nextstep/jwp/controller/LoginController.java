package nextstep.jwp.controller;

import static org.apache.coyote.http11.headers.MimeType.*;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.exception.EmptyBodyException;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.handler.HttpController;
import org.apache.coyote.http11.handler.HttpHandle;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.QueryParser;
import org.apache.coyote.http11.util.StaticResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController implements HttpController {

	private static final Map<HttpMethod, HttpHandle> HANDLE_MAP = Map.of(
		HttpMethod.GET, LoginController::servingStaticResource,
		HttpMethod.POST, LoginController::loginProcess
	);
	private static final String END_POINT = "/login";
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	private static final String ACCOUNT_PARAM_KEY = "account";
	private static final String PASSWORD_PARAM_KEY = "password";
	private static final String LOGIN_SUCCESS_LOCATION = "/index.html";
	private static final String LOGIN_STATIC_RESOURCE_FILE_PATH = "static/login.html";
	private static final String SESSION_USER_KEY = "user";
	private static final String JSESSIONID_KEY = "JSESSIONID=";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.equalPath(END_POINT)
			&& HANDLE_MAP.containsKey(request.getHttpMethod());
	}

	@Override
	public void handleTo(final HttpRequest request, final HttpResponse response) {
		final HttpMethod httpMethod = request.getHttpMethod();
		HANDLE_MAP.get(httpMethod)
			.handle(request, response);
	}

	private static void servingStaticResource(final HttpRequest request, final HttpResponse response) {
		request.getJSessionId()
			.ifPresentOrElse(
				id -> indexHtmlRedirect(id, response),
				() -> loginHtmlResponse(response)
			);
	}

	private static void indexHtmlRedirect(final String jSessionId, final HttpResponse response) {
		final Session session = SessionManager.findSession(jSessionId);
		Optional.ofNullable((User)session.getAttributes(SESSION_USER_KEY))
			.ifPresentOrElse(
				user -> response.redirect(LOGIN_SUCCESS_LOCATION),
				() -> loginHtmlResponse(response)
			);
	}

	private static void loginHtmlResponse(final HttpResponse response) {
		final URL url = LoginController.class.getClassLoader()
			.getResource(LOGIN_STATIC_RESOURCE_FILE_PATH);
		final String body = StaticResourceResolver.resolve(url);
		response.setResponse(HttpStatusCode.OK_200, body, HTML);
	}

	private static void loginProcess(final HttpRequest request, final HttpResponse response) {
		final Map<String, String> parsedQuery = request.getBody()
			.map(QueryParser::parse)
			.orElseThrow(EmptyBodyException::new);
		final String account = parsedQuery.get(ACCOUNT_PARAM_KEY);
		final User user = InMemoryUserRepository.findByAccount(account)
			.orElseThrow(UnauthorizedException::new);
		checkPasswordProcess(parsedQuery.get(PASSWORD_PARAM_KEY), user, request, response);
	}

	private static void checkPasswordProcess(final String password, final User user,
		final HttpRequest request, final HttpResponse response) {
		if (!user.checkPassword(password)) {
			throw new UnauthorizedException();
		}
		log.info(user.toString());
		loginSuccessResponse(user, request, response);
	}

	private static void loginSuccessResponse(final User user, final HttpRequest request, final HttpResponse response) {
		response.redirect(LOGIN_SUCCESS_LOCATION);
		if (!request.isExistJSessionId()) {
			issueJSessionId(user, response);
		}
	}

	private static void issueJSessionId(final User user, final HttpResponse response) {
		final String jSessionId = UUID.randomUUID().toString();
		final Session session = new Session(jSessionId);
		session.setAttributes(SESSION_USER_KEY, user);
		SessionManager.add(session);
		response.addSetCookie(JSESSIONID_KEY + jSessionId);
	}
}
