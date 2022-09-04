package nextstep.jwp.controller;

import java.io.IOException;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceUtil;

import nextstep.jwp.service.LoginService;

public class LoginController implements Controller {

	private static final String LOGIN_HTML = "login.html";
	private static final String REDIRECT_URL = "/index.html";
	private static final String UNAUTHORIZED_HTML = "401.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		String method = request.getMethod();
		if (HttpMethod.GET.equals(method)) {
			handleGetRequest(response);
			return;
		}
		if (HttpMethod.POST.equals(method)) {
			handlePostRequest(request, response);
		}
	}

	private void handleGetRequest(HttpResponse response) throws IOException {
		response.setStatus(HttpStatus.OK);
		response.setBody(StaticResourceUtil.getContent(LOGIN_HTML));
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}

	private void handlePostRequest(HttpRequest request, HttpResponse response) throws IOException {
		String account = request.getQueryString("account");
		String password = request.getQueryString("password");

		if (LoginService.login(account, password)) {
			response.setStatus(HttpStatus.FOUND);
			response.addHeader(HttpHeader.LOCATION, REDIRECT_URL);
			return;
		}
		response.setStatus(HttpStatus.UNAUTHORIZED);
		response.setBody(StaticResourceUtil.getContent(UNAUTHORIZED_HTML));
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}
}
