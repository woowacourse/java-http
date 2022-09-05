package org.apache.coyote.http11;

import static org.apache.coyote.http11.response.ContentType.HTML;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.CommonHttpResponse;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.LoginHttpResponse;
import org.apache.coyote.http11.response.NormalHttpResponse;

public class Controller {

	private Controller() {
	}

	public static HttpResponse run(HttpRequest request) throws IOException {
		if (request.isMatching("/")) {
			var responseBody = "Hello world!";
			return new NormalHttpResponse(responseBody, HTML);
		}

		if (request.getUrl().startsWith("/login")) {
			UserInfoLogger.info(request.getParams());
			return new LoginHttpResponse("/login.html", HTML);
		}
		return new CommonHttpResponse(request.getUrl(), ContentType.from(request.getUrl()));
	}
}
