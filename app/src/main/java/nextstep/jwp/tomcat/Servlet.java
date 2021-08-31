package nextstep.jwp.tomcat;

import java.io.IOException;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.http.reponse.HttpResponse;
import nextstep.jwp.http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Servlet {

    protected final String requestMappingUri;
    protected Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public Servlet(String requestMappingUri) {
        this.requestMappingUri = requestMappingUri;
    }

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        if (httpRequest.isGetRequest()) {
            doGet(httpRequest, httpResponse);
            return;
        }

        if (httpRequest.isPostRequest()) {
            doPost(httpRequest, httpResponse);
        }

    }

    public boolean isSameRequestMapping(String requestMapping) {
        return requestMapping.startsWith(this.requestMappingUri);
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

}
