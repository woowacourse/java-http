package org.apache;

import java.io.IOException;

import org.apache.HttpResponse;

public interface RequestHandler {

	boolean canHandle(HttpRequest request);

	HttpResponse handle(HttpRequest httpRequest) throws IOException;
}
