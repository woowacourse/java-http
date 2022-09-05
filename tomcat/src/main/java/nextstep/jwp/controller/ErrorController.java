package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceUtil;

public class ErrorController extends AbstractController {

	private static final String NOT_FOUND_HTML = "404.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		String responseBody = StaticResourceUtil.getContent(NOT_FOUND_HTML);
		response.setStatus(HttpStatus.NOT_FOUND);
		response.setBody(responseBody);
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}
}
