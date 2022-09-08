package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.response.HttpResponseFactory.getFoundHttpResponse;
import static org.apache.coyote.http11.response.HttpResponseFactory.getOKHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.sun.jdi.InternalException;

import nextstep.jwp.model.UserService;
import nextstep.jwp.util.Parser;

public class RegisterController implements Handler {
	@Override
	public HttpResponse handle(HttpRequest httpRequest) {

		final String fileName = "/register.html";
		final String succeedRedirectUrl = "/index.html";
		final String fileType = Parser.parseFileType(fileName);

		try {
			if (httpRequest.getRequestBody().isEmpty()) {
				return getOKHttpResponse(fileName);
			}

			if (httpRequest.requestPOST()) {
				UserService.register(httpRequest.getRequestBody());
				getFoundHttpResponse(succeedRedirectUrl);
			}

			return getOKHttpResponse(fileName, ContentType.from(fileType).getValue());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			throw new InternalException("서버 에러가 발생했습니다.");
		}
	}
}
