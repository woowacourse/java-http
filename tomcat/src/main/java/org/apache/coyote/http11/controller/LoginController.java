package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.sun.jdi.InternalException;

import nextstep.jwp.model.User;
import nextstep.jwp.model.UserService;

public class LoginController implements Handler {

	private static final String SUCCEED_REDIRECT_URL = "/index.html";
	private static final String FAILED_REDIRECT_URL = "/401.html";
	private static final String LOGIN_HTML_URL = "/login.html";

	@Override
	public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
		try {
			if (isRequestLoginPage(httpRequest)) {
				httpResponse.setOkResponse(LOGIN_HTML_URL);
				return;
			}
			final User succeedLoginUser = UserService.login(httpRequest.getParams());

			if (isSucceedLogin(succeedLoginUser)) {
				httpResponse.setSessionAndCookieWithOkResponse(succeedLoginUser, SUCCEED_REDIRECT_URL);
				return;
			}
			httpResponse.setFoundResponse(FAILED_REDIRECT_URL);

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			throw new InternalException("서버 에러가 발생했습니다.");
		}
	}

	private boolean isSucceedLogin(User succeedLoginUser) {
		return succeedLoginUser != null;
	}

	private boolean isRequestLoginPage(HttpRequest httpRequest) {
		return httpRequest.getParams().isEmpty() && !httpRequest.hasCookie();
	}
}
