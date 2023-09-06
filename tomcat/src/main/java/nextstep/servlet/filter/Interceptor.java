package nextstep.servlet.filter;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Interceptor {

    boolean preHandle(HttpRequest request, HttpResponse response);
}
