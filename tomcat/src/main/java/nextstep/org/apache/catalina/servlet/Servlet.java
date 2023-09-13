package nextstep.org.apache.catalina.servlet;

import nextstep.org.apache.coyote.http11.request.Http11Request;
import nextstep.org.apache.coyote.http11.response.Http11Response;

public interface Servlet {

    void service(Http11Request request, Http11Response response) throws Exception;
}
