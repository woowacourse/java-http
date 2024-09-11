package org.was.converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.was.Controller.ResponseResult;
import org.was.view.View;
import org.was.view.ViewResolver;

public class HttpResponseConverter {

    private static final HttpResponseConverter INSTANCE = new HttpResponseConverter();

    private HttpResponseConverter() {
    }

    public static HttpResponseConverter getInstance() {
        return INSTANCE;
    }

    public void convert(HttpResponse response, ViewResolver viewResolver, ResponseResult responseResult)
            throws IOException {
        HttpStatusCode statusCode = responseResult.getStatusCode();
        ResponseBody responseBody = createResponseBody(viewResolver, responseResult);
        ResponseHeader responseHeader = createResponseHeader(responseResult.getHeaders(), responseBody);

        response.setResponse(statusCode, responseHeader, responseBody);
    }

    public void convert(HttpResponse response, HttpStatusCode statusCode, String body) {
        ResponseBody responseBody = new ResponseBody(body);
        ResponseHeader responseHeader = createResponseHeader(new HashMap<>(), responseBody);

        response.setResponse(statusCode, responseHeader, responseBody);
    }

    private ResponseBody createResponseBody(ViewResolver viewResolver, ResponseResult responseResult)
            throws IOException {
        String path = responseResult.getPath();
        String body = responseResult.getBody();

        if (path != null && body == null) {
            View view = viewResolver.getView(path);
            return new ResponseBody(view.getContent());
        } else if (body != null) {
            return new ResponseBody(body);
        }
        return new ResponseBody(null);
    }

    private ResponseHeader createResponseHeader(Map<String, String> headers, ResponseBody responseBody) {
        String bodyContent = responseBody.getContent();

        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE.getName())) {
            if (bodyContent != null && bodyContent.startsWith("{") && bodyContent.endsWith("}")) {
                headers.put(HttpHeaders.CONTENT_TYPE.getName(),
                        HttpContentType.APPLICATION_JSON.getContentType());
            } else {
                headers.put(HttpHeaders.CONTENT_TYPE.getName(), HttpContentType.TEXT_HTML.getContentType());
            }
        }

        if (bodyContent != null) {
            headers.put(HttpHeaders.CONTENT_LENGTH.getName(), String.valueOf(bodyContent.length()));
        }

        return new ResponseHeader(headers);
    }
}
