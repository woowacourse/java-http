package org.apache;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.coyote.http11.Http11Processor;

public class StaticResourceRequestHandler implements RequestHandler {

	private static final String[] CAN_HANDLE_SUFFIX = {".html", ".css", ".js", ".ico"};

	@Override
	public boolean canHandle(HttpRequest request) {
		return "GET".equals(request.getMethod()) &&
			Stream.of(CAN_HANDLE_SUFFIX).anyMatch(suffix -> request.getUri().endsWith(suffix));
	}

	@Override
	public HttpResponse handle(HttpRequest request) throws IOException {
		URL resource = Http11Processor.class.getClassLoader().getResource("static" + request.getUri());
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		String response = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		return HttpResponse.ok(request.getUri(), response);
	}
}
