package org.apache.catalina.servlets;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import jakarta.servlet.AbstractController;

public class StaticResourceController extends AbstractController {

	private static final String[] CAN_HANDLE_EXTENSION = {".html", ".css", ".js", ".ico"};
	public static final int EXTENSION_BEGIN_INDEX = 1;

	@Override
	public boolean canHandle(HttpRequest request) {
		return request.hasMethod(HttpMethod.GET) &&
			Stream.of(CAN_HANDLE_EXTENSION).anyMatch(suffix -> request.getUri().endsWith(suffix));
	}

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
		throw new IllegalArgumentException("cannot request post request to StaticResourceController");
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
		URL resource = getClass().getClassLoader().getResource("static" + request.getUri());
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		String extension = getExtension(request);
		response.setContentType(ContentType.getByExtension(extension));
		response.ok(Files.readAllBytes(path));
	}

	private String getExtension(HttpRequest request) {
		return Stream.of(CAN_HANDLE_EXTENSION)
			.filter(suffix -> request.getUri().endsWith(suffix))
			.findFirst()
			.map(extension -> extension.substring(EXTENSION_BEGIN_INDEX))
			.orElseThrow(() -> new IllegalArgumentException("cannot find extension"));
	}
}
