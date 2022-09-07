package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;

public class ErrorController extends AbstractController {

	private static final String NOT_FOUND_HTML = "404.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		handleHtml(HttpStatus.NOT_FOUND, NOT_FOUND_HTML, response);
	}
}
