package nextstep.jwp.vo;

import nextstep.jwp.model.Request;
import org.apache.coyote.http11.GetRequestMangerImpl;
import org.apache.coyote.http11.PostRequestMangerImpl;
import org.apache.coyote.http11.RequestManager;

public enum RequestMethod {

    GET,
    POST;

    public static RequestManager selectManager(Request request) {
        if (request.getRequestMethod() == GET) {
            return new GetRequestMangerImpl(request);
        }
        return new PostRequestMangerImpl(request);
    }
}
