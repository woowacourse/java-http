package nextstep.jwp.interceptor;

import nextstep.jwp.handler.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public interface HandlerInterceptor {

    boolean preHandle(HttpRequest request, HttpResponse response, Handler handler) throws IOException;
}
