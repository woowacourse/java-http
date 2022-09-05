package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceUtil;

import nextstep.jwp.service.LoginService;

public class LoginController extends AbstractController {

	private static final String LOGIN_HTML = "login.html";
	private static final String REDIRECT_URL = "/index.html";
	private static final String UNAUTHORIZED_HTML = "401.html";

	@Override
	public void doGet(HttpRequest request, HttpResponse response) throws Exception {
		response.setStatus(HttpStatus.OK);
		response.setBody(StaticResourceUtil.getContent(LOGIN_HTML));
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) throws Exception {
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
