package org.apache.catalina.servlets;

import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestURI;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class StaticResourceServlet extends Servlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        RequestURI requestURI = httpRequest.getRequestURI();
        String absolutePath = requestURI.absolutePath();

        String content = findResourceWithPath(absolutePath);
        ResponseBody responseBody = new ResponseBody(content);

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .addHeader(CONTENT_TYPE, ContentType.parse(absolutePath))
                .addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length))
                .setResponseBody(responseBody);
    }
}
