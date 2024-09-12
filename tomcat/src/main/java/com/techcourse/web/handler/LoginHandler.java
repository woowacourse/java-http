package com.techcourse.web.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestHeader;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.request.HttpRequestQuery;
import org.apache.coyote.http11.http.request.HttpRequestUrl;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;
import org.apache.coyote.http11.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.util.FormUrlEncodedParser;
import com.techcourse.web.util.JsessionIdGenerator;
import com.techcourse.web.util.LoginChecker;
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
			return loadLoginPage(request);
		}
		if (method == HttpMethod.POST) {
			return login(request);
		}

		return notFoundResponse();
	}

	private HttpResponse loadLoginPage(HttpRequest request) throws IOException {
		if (LoginChecker.isLoggedIn(request)) {
			return redirect(new HttpResponseHeader(), "/index.html");
		}
		HttpResponseBody body = ResourceLoader.getInstance().loadResource("/login.html");
		return HttpResponse.ok(new HttpResponseHeader(), body);
	}

	private HttpResponse login(HttpRequest request) {
		Map<String, String> body = FormUrlEncodedParser.parse(request.getRequestBody());
		String account = body.get("account");
		String password = body.get("password");

		if (isUserNotExist(account, password)) {
			return redirect(new HttpResponseHeader(), "/401.html");
		}

		User user = InMemoryUserRepository.findByAccount(account).get();
		HttpResponseHeader responseHeader = createResponseHeader(request, user);

		return redirect(responseHeader, "/index.html");
	}

	private HttpResponseHeader createResponseHeader(HttpRequest request, User user) {
		HttpResponseHeader header = new HttpResponseHeader();
		String sessionId = addJSessionId(request, header);
		SessionManager.createSession(sessionId, user);

		return header;
	}

	private String addJSessionId(HttpRequest request, HttpResponseHeader responseHeader) {
		HttpRequestHeader header = request.getHeaders();
		HttpCookie httpCookie = header.getHttpCookie();
		if (isNotExistJsessionid(httpCookie)) {
			String sessionId = JsessionIdGenerator.generate();
			responseHeader.addJSessionId(sessionId);
			return sessionId;
		}
		return httpCookie.getJsessionid();
	}

	private boolean isNotExistJsessionid(HttpCookie httpCookie) {
		return httpCookie == null || !httpCookie.hasJsessionId();
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
}
