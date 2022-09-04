package nextstep.jwp.vo;

import org.apache.coyote.http11.GetRequestMangerImpl;
import org.apache.coyote.http11.PostRequestMangerImpl;
import org.apache.coyote.http11.RequestManager;
import org.apache.coyote.http11.RequestParser;

import java.io.BufferedReader;

public enum RequestMethod {

    GET,
    POST;

    public static RequestManager selectManager(String method, RequestParser requestParser) {
        if (method.equals(GET.name())) {
            return new GetRequestMangerImpl(requestParser);
        }
        return new PostRequestMangerImpl(requestParser);
    }
}
