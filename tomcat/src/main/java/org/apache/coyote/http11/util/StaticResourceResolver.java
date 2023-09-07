package org.apache.coyote.http11.util;

import static org.apache.coyote.http11.headers.MimeType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.exception.StaticResourceReadException;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceResolver {

	private StaticResourceResolver() {

	}

	public static HttpResponse resolve(final URL url) {
		try {
			final String body = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
			return new HttpResponse(
				OK_200,
				body,
				HttpHeaders.of(body, HTML)
			);
		} catch (final IOException e) {
			throw new StaticResourceReadException(e);
		}
	}
}
