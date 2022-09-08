package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.response.HttpResponseFactory.getOKHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.sun.jdi.InternalException;

import nextstep.jwp.util.Parser;

public class ResourceController implements Handler {
	@Override
	public HttpResponse handle(HttpRequest httpRequest) {

		final String path = httpRequest.getUrl();
		final String fileName = Parser.convertResourceFileName(path);
		final String fileType = Parser.parseFileType(fileName);

		try {
			return getOKHttpResponse(fileName, ContentType.from(fileType).getValue());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			throw new InternalException("서버 에러가 발생했습니다.");
		}
	}
}
