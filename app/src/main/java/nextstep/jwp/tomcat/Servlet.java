package nextstep.jwp.tomcat;

import java.io.IOException;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Servlet {

    protected String requestMappingUri;
    protected Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        if (httpRequest.getHttpMethod().equals("GET")) {
            doGet(httpRequest, httpResponse);
            return;
        }

        if (httpRequest.getHttpMethod().equals("POST")) {
            doPost(httpRequest, httpResponse);
        }

    }

    public boolean isSameRequestMapping(String requestMapping) {
        return requestMapping.startsWith(this.requestMappingUri);
    }

    public void doGet(HttpRequest httpRequest, HttpResponse response) throws IOException {

    }

    public void doPost(HttpRequest httpRequest, HttpResponse response) {

    }

}
