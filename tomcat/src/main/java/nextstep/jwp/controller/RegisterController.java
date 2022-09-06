package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;

import nextstep.jwp.exception.AccountDuplicateException;
import nextstep.jwp.service.UserService;

public class RegisterController extends AbstractController {

	private static final String REGISTER_HTML = "register.html";
	private static final String BAD_REQUEST_HTML = "400.html";

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		handleHtml(HttpStatus.OK, REGISTER_HTML, response);
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		String account = request.getQueryString("account");
		String password = request.getQueryString("password");
		String email = request.getQueryString("email");

		try {
			UserService.register(account, password, email);
			handleRedirect(response);
		} catch (AccountDuplicateException e) {
			handleHtml(HttpStatus.BAD_REQUEST, BAD_REQUEST_HTML, response);
		}
	}
}
