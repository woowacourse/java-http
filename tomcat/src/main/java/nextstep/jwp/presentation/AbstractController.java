package nextstep.jwp.presentation;

import nextstep.jwp.exception.NotSupportHttpMethodException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        } else if (request.getHttpMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        throw new NotSupportHttpMethodException();

    }

    protected void doPost(Http11Request request, Http11Response response) throws Exception {
    }

    protected void doGet(Http11Request request, Http11Response response) throws Exception {
    }
}
