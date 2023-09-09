package nextstep.org.apache.coyote.http11.servlet;

import nextstep.org.apache.coyote.http11.Http11Request;
import nextstep.org.apache.coyote.http11.Http11Response;
import nextstep.org.apache.coyote.http11.Status;

public class DefaultServlet extends AbstractServlet {

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        response.setStatus(Status.NOT_IMPLEMENTED);
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        responseWithBody(request, response);
    }
}
