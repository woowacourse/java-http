package org.apache.catalina.mvc;

import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {
    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String BAD_REQUEST_PAGE = "/400.html";

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            return doGet(request);
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            return doPost(request);
        }
        log.warn("지원되지 않는 HTTP 메서드입니다.");
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.BAD_REQUEST),
                DEFAULT_CONTENT_TYPE,
                FileReader.loadFileContent(BAD_REQUEST_PAGE));
    }
}
