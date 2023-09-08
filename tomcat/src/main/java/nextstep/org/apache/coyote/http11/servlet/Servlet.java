package nextstep.org.apache.coyote.http11.servlet;

import nextstep.org.apache.coyote.http11.Http11Request;
import nextstep.org.apache.coyote.http11.Http11Response;

public interface Servlet {

    void service(Http11Request request, Http11Response response) throws Exception;
}
