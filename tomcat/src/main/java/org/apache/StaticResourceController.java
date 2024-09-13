package org.apache;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StatusLine;

public class StaticResourceController extends AbstractController {

	private static final String[] CAN_HANDLE_SUFFIX = {".html", ".css", ".js", ".ico"};

	@Override
	public boolean canHandle(HttpRequest request) {
		return request.hasMethod(HttpMethod.GET) &&
			Stream.of(CAN_HANDLE_SUFFIX).anyMatch(suffix -> request.getUri().endsWith(suffix));
	}

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
		throw new IllegalArgumentException("cannot request post request to StaticResourceController");
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
		super.doGet(request, response);
		URL resource = Http11Processor.class.getClassLoader().getResource("static" + request.getUri());
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		response.setResponseBody(Files.readAllBytes(path));
		response.setContentType(request.getUri());
		response.setContentLength();
		response.setStatusLine(StatusLine.from(HttpStatus.OK));
	}
}
