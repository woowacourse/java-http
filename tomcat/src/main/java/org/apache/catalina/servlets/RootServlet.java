package org.apache.catalina.servlets;

import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_TYPE;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class RootServlet extends Servlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpMethod httpMethod = httpRequest.getMethod();
        if (httpMethod.isEqualTo(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String content = "Hello world!";
        ResponseBody responseBody = new ResponseBody(content);

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .addHeader(CONTENT_TYPE, ContentType.parse(content))
                .addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length))
                .setResponseBody(responseBody);
    }
}
