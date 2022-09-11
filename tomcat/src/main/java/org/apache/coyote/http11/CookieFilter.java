package org.apache.coyote.http11;

import static nextstep.jwp.model.UserService.findUserBySession;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class CookieFilter {

	private static final String LOGIN = "login";
	private static final String LOGIN_HTML = "/login.html";
	private static final String SUCCEED_REDIRECT_URL = "/index.html";

	public static boolean doFilter(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
		if (request.getUrl().contains(LOGIN) && request.getParams().isEmpty() && request.hasCookie()) {
			return loginBySession(request, response);
		}
		return false;
	}

	private static boolean loginBySession(HttpRequest request, HttpResponse response) throws
		IOException,
		URISyntaxException {
		if (findUserBySession(request)) {
			response.setResponseBody(SUCCEED_REDIRECT_URL);
			response.setFoundHttpStatusLine();
			response.setRedirectUrl(SUCCEED_REDIRECT_URL);
			response.setOKHeader(ContentType.from(SUCCEED_REDIRECT_URL));
			return true;
		}
		response.setResponseBody(LOGIN_HTML);
		response.setOkHttpStatusLine();
		response.setOKHeader(ContentType.from(LOGIN_HTML));
		return true;
	}
}
