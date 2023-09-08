package nextstep.org.apache.coyote.http11.servlet;

import nextstep.org.apache.coyote.http11.Http11Request;
import nextstep.org.apache.coyote.http11.Http11Response;
import nextstep.org.apache.coyote.http11.Status;

public abstract class AbstractServlet implements Servlet{

    @Override
    public void Service(Http11Request request, Http11Response response) throws Exception {
        String method = request.getMethod();

        if (method.equals("GET")) {
            doGet(request, response);
        } else if (method.equals("POST")) {
            doPost(request, response);
        } else {
            response.setStatus(Status.NOT_IMPLEMENTED);
        }
    }

    protected void doPost(Http11Request request, Http11Response response) throws Exception {}

    protected void doGet(Http11Request request, Http11Response response) throws  Exception {}
}
