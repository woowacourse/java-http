package com.techcourse.web.handler;

import java.io.IOException;
import java.util.Optional;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.request.HttpRequestQuery;
import org.apache.coyote.http11.http.request.HttpRequestUrl;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.util.ResourceLoader;

public class LoginHandler implements Handler {

	private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
	private static final LoginHandler instance = new LoginHandler();
	private static final String LOGIN_PATH = "/login";

	private LoginHandler() {
	}

	public static LoginHandler getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequestLine requestLine) {
		return requestLine.getRequestPath().startsWith(LOGIN_PATH);
	}

	@Override
	public HttpResponse handle(HttpRequest request) throws IOException {
		HttpRequestLine requestLine = request.getRequestLine();
		HttpRequestUrl requestUrl = request.getRequestLine().getHttpRequestUrl();
		HttpMethod method = requestLine.getMethod();

		if (method == HttpMethod.GET && LOGIN_PATH.equals(requestUrl.getRequestUrl())) {
			return loadLoginPage();
		}
		if (method == HttpMethod.GET && requestUrl.isQueryExist()) {
			return login(request);
		}

		return notFoundResponse();
	}

	private HttpResponse loadLoginPage() throws IOException {
		HttpResponseBody body = ResourceLoader.getInstance().loadResource("/login.html");
		return HttpResponse.ok(new HttpResponseHeader(), body);
	}

	private HttpResponse login(HttpRequest request) {
		HttpRequestQuery query = request.getRequestLine().getQuery();
		String account = query.getValue("account");
		String password = query.getValue("password");

		HttpResponseHeader responseHeader = new HttpResponseHeader();
		responseHeader.addHeader("Location", getRedirectLocation(account, password));

		return HttpResponse.redirect(responseHeader);
	}

	private String getRedirectLocation(String account, String password) {
		if (isUserNotExist(account, password)) {
			return "/401.html";
		}
		return "/index.html";
	}

	private boolean isUserNotExist(String account, String password) {
		if (account == null) {
			log.info("account is required");
			return true;
		}
		if (password == null) {
			log.info("password is required");
			return true;
		}

		Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
		return userOptional.map(user -> !user.checkPassword(password)).orElse(true);
	}

	private static HttpResponse notFoundResponse() throws IOException {
		HttpResponseBody notFoundPage = ResourceLoader.getInstance().loadResource("/404.html");
		return HttpResponse.notFound(new HttpResponseHeader(), notFoundPage);
	}
}
