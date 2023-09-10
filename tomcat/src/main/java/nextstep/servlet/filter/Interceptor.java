package nextstep.servlet.filter;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Interceptor {

    boolean preHandle(HttpRequest request, HttpResponse response);
}
