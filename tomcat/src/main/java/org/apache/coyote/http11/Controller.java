package org.apache.coyote.http11;

public interface Controller {

    String run(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
