package nextstep.jwp.controller;

import java.io.IOException;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.header.ContentType;
import org.apache.coyote.http11.http.header.HttpHeader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceUtil;

import nextstep.jwp.service.UserService;

public class RegisterController extends AbstractController {

	private static final String REGISTER_HTML = "register.html";
	private static final String BAD_REQUEST_HTML = "400.html";
	private static final String REDIRECT_URL = "/index.html";

	@Override
	public void doGet(HttpRequest request, HttpResponse response) throws Exception {
		response.setStatus(HttpStatus.OK);
		response.setBody(StaticResourceUtil.getContent(REGISTER_HTML));
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) throws Exception {
		String account = request.getQueryString("account");
		String password = request.getQueryString("password");
		String email = request.getQueryString("email");
		boolean registerResult = UserService.register(account, password, email);

		if (!registerResult) {
			handleRegisterFail(response);
			return;
		}
		response.setStatus(HttpStatus.FOUND);
		response.addHeader(HttpHeader.LOCATION, REDIRECT_URL);
	}

	private void handleRegisterFail(HttpResponse response) throws IOException {
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setBody(StaticResourceUtil.getContent(BAD_REQUEST_HTML));
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}
}
