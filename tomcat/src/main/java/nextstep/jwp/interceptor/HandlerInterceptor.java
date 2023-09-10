package nextstep.jwp.interceptor;

import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public interface HandlerInterceptor {

    boolean preHandle(HttpRequest request, HttpResponse response, Controller controller) throws IOException;
}
