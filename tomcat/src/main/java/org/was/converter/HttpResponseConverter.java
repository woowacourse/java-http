package org.was.converter;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;

public class HttpResponseConverter {

    private static final HttpResponseConverter INSTANCE = new HttpResponseConverter();

    private HttpResponseConverter() {
    }

    public static HttpResponseConverter getInstance() {
        return INSTANCE;
    }

    public void convertResponse(HttpResponse response,
                                HttpStatusCode statusCode, Map<String, String> headers, String body) {
        ResponseHeader responseHeader = createResponseHeader(headers, body);
        ResponseBody responseBody = new ResponseBody(body);
        response.setResponse(statusCode, responseHeader, responseBody);
    }

    public void convertResponse(HttpResponse response, HttpStatusCode statusCode, String body) {
        ResponseHeader responseHeader = createResponseHeader(new HashMap<>(), body);
        ResponseBody responseBody = new ResponseBody(body);
        response.setResponse(statusCode, responseHeader, responseBody);
    }
    private ResponseHeader createResponseHeader(Map<String, String> headers, String body) {
        if (body.startsWith("{") && body.endsWith("}")) {
            headers.put(HttpHeaders.CONTENT_TYPE.getHeaderName(), HttpContentType.APPLICATION_JSON.getContentType());
        } else {
            headers.put(HttpHeaders.CONTENT_TYPE.getHeaderName(), HttpContentType.TEXT_HTML.getContentType());
        }
        headers.put(HttpHeaders.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.length()));

        return new ResponseHeader(headers);
    }
}
