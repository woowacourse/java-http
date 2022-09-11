package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.sun.jdi.InternalException;

import nextstep.jwp.util.Parser;

public class ResourceController implements Handler {
	@Override
	public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
		final String path = httpRequest.getUrl();
		final String fileName = Parser.convertResourceFileName(path);

		try {
			httpResponse.setOkResponse(fileName);

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			throw new InternalException("서버 에러가 발생했습니다.");
		}
	}
}
