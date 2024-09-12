package org.apache;

import java.io.IOException;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

	boolean canHandle(HttpRequest request);

	HttpResponse handle(HttpRequest httpRequest) throws IOException;
}
