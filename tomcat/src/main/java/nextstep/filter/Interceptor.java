package nextstep.filter;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Interceptor {

    boolean preHandle(final HttpRequest httpRequest, final HttpResponse httpResponse);

    boolean support(final HttpRequest httpRequest);
}
