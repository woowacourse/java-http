package handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    String run(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
