package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.ContentType.UTF;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.common.StatusCode.OK;
import static org.apache.coyote.http11.common.Version.HTTP_1_1;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpHeader;
import org.apache.coyote.http11.response.HttpHeaders;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusLine;

import com.sun.jdi.InternalException;

import nextstep.jwp.model.UserInfoLogger;
import nextstep.jwp.util.IOUtils;

public class LoginController implements Handler {

	@Override
	public HttpResponse handle(HttpRequest httpRequest) {

		final String fileName = "/login.html";

		try {
			UserInfoLogger.info(httpRequest.getParams());

			final String body = IOUtils.readResourceFile(fileName);
			final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, HTML.getValue(), UTF.getValue());
			final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.getBytes().length));

			return HttpResponse.of(HttpStatusLine.of(HTTP_1_1, OK),
				HttpHeaders.from(contentType, contentLength),
				body);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			throw new InternalException("서버 에러가 발생했습니다.");
		}
	}
}
