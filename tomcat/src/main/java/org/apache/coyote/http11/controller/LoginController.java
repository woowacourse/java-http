package org.apache.coyote.http11.controller;

import static nextstep.jwp.model.UserService.findUserBySession;
import static org.apache.coyote.http11.response.HttpResponseFactory.getFoundHttpResponse;
import static org.apache.coyote.http11.response.HttpResponseFactory.getLoginHttpResponse;
import static org.apache.coyote.http11.response.HttpResponseFactory.getOKHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.sun.jdi.InternalException;

import nextstep.jwp.model.User;
import nextstep.jwp.model.UserService;

public class LoginController implements Handler {

	@Override
	public HttpResponse handle(HttpRequest httpRequest) {

		final String fileName = "/login.html";
		final String succeedRedirectUrl = "/index.html";
		final String failedRedirectUrl = "/401.html";

		try {
			if (httpRequest.getParams().isEmpty() && !httpRequest.hasCookie()) {
				return getOKHttpResponse(fileName);
			}
			if (httpRequest.getParams().isEmpty() && httpRequest.hasCookie()) {
				if (findUserBySession(httpRequest)) {
					return getFoundHttpResponse(succeedRedirectUrl);
				}
				return getOKHttpResponse(fileName);
			}
			final User succeedLoginUser = UserService.login(httpRequest.getParams());
			if (succeedLoginUser != null) {
				return getLoginHttpResponse(succeedRedirectUrl, succeedLoginUser);
			}
			return getFoundHttpResponse(failedRedirectUrl);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			throw new InternalException("서버 에러가 발생했습니다.");
		}
	}

}
