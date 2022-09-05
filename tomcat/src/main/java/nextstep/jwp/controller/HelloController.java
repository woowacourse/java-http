package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;

public class HelloController extends AbstractController {

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		response.setStatus(HttpStatus.OK);
		response.setBody("Hello world!");
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}
}
