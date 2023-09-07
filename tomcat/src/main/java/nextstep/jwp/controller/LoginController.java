package nextstep.jwp.controller;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.http11.exception.EmptyBodyException;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.handler.HttpController;
import org.apache.coyote.http11.handler.HttpHandle;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
	public HttpResponse handleTo(final HttpRequest request) {
		final HttpMethod httpMethod = request.getHttpMethod();
		return HANDLE_MAP.get(httpMethod)
			.handle(request);
	}

	private static HttpResponse servingStaticResource(final HttpRequest request) {
		return request.getJSessionId()
			.map(LoginController::indexHtmlRedirect)
			.orElseGet(LoginController::loginHtmlResponse);
	}

	private static HttpResponse indexHtmlRedirect(final String jSessionId) {
		final Session session = SessionManager.findSession(jSessionId);
		final User user = (User)session.getAttributes(SESSION_USER_KEY);
		//user가 없는 경우 예외처리 고민하기
		log.info(user.toString());
		return HttpResponse.redirect(LOGIN_SUCCESS_LOCATION);
	}

	private static HttpResponse loginHtmlResponse() {
		final URL url = LoginController.class.getClassLoader()
			.getResource(LOGIN_STATIC_RESOURCE_FILE_PATH);
		return StaticResourceResolver.resolve(url);
	}

	private static HttpResponse loginProcess(final HttpRequest request) {
		final Map<String, String> parsedQuery = request.getBody()
			.map(QueryParser::parse)
			.orElseThrow(EmptyBodyException::new);
		final String account = parsedQuery.get(ACCOUNT_PARAM_KEY);
		return InMemoryUserRepository.findByAccount(account)
			.map(user -> checkPasswordProcess(parsedQuery.get(PASSWORD_PARAM_KEY), user, request))
			.orElseThrow(UnauthorizedException::new);
	}

	private static HttpResponse checkPasswordProcess(final String password, final User user,
		final HttpRequest request) {
		if (user.checkPassword(password)) {
			log.info(user.toString());
			return loginSuccessResponse(user, request);
		}
		throw new UnauthorizedException();
	}

	private static HttpResponse loginSuccessResponse(final User user, final HttpRequest request) {
		final HttpResponse response = HttpResponse.redirect(LOGIN_SUCCESS_LOCATION);
		if (!request.isExistJSessionId()) {
			issueJSessionId(user, response);
		}
		return response;
	}

	private static void issueJSessionId(final User user, final HttpResponse response) {
		final String jSessionId = UUID.randomUUID().toString();
		final Session session = new Session(jSessionId);
		session.setAttributes(SESSION_USER_KEY, user);
		SessionManager.add(session);
		response.addSetCookie(JSESSIONID_KEY + jSessionId);
	}
}
