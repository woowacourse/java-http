package com.techcourse.web.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.apache.coyote.http11.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.Resource;
import com.techcourse.web.util.FormUrlEncodedParser;
import com.techcourse.web.util.JsessionIdGenerator;
import com.techcourse.web.util.LoginChecker;
import com.techcourse.web.util.ResourceLoader;

public class LoginController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	private static final LoginController instance = new LoginController();
	private static final String LOGIN_PATH = "/login";

	private LoginController() {
	}

	public static LoginController getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequest request) {
		HttpRequestLine requestLine = request.getRequestLine();
		return requestLine.getRequestPath().startsWith(LOGIN_PATH);
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) throws IOException {
		if (LoginChecker.isLoggedIn(request)) {
			redirect(response, "/index.html");
			return;
		}

		Resource resource = ResourceLoader.getInstance().loadResource("/login.html");
		response.setStatusCode(HttpStatusCode.OK);
		response.setBody(resource.getContentType(), resource.getContent());
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		Map<String, String> body = FormUrlEncodedParser.parse(request.getRequestBody());
		User user = findUser(body);

		if (isUserNotExist(user, body.get("password"))) {
			redirect(response, "/401.html");
			return;
		}

		completeLogin(response, user);
	}

	private User findUser(Map<String, String> body) {
		String account = body.get("account");

		return InMemoryUserRepository.findByAccount(account).orElse(null);
	}

	private void completeLogin(HttpResponse response, User user) {
		String sessionId = JsessionIdGenerator.generate();
		response.addHeader(HttpHeader.SET_COOKIE.getName(), HttpCookie.JSESSIONID + "=" + sessionId);
		SessionManager.createSession(sessionId, user);

		redirect(response, "/index.html");
	}

	private boolean isUserNotExist(User user, String password) {
		if (user == null) {
			log.info("user is not exist");
			return true;
		}
		return !user.checkPassword(password);
	}
}
