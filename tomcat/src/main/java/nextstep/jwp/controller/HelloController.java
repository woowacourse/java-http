package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.header.ContentType;
import org.apache.coyote.http11.http.header.HttpHeader;

public class HelloController extends AbstractController {

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) {
		response.setStatus(HttpStatus.OK);
		response.setBody("Hello world!");
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}
}
