package nextstep.jwp.interceptor;

import org.apache.http.Request;
import org.apache.http.Response;

public interface Interceptor {

    boolean preHandle(Request request, Response response);
}
