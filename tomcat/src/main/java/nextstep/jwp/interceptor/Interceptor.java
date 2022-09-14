package nextstep.jwp.interceptor;

import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;

public interface Interceptor {

    boolean preHandle(Request request, Response response);
}
