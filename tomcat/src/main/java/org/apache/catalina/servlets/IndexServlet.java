package org.apache.catalina.servlets;

import static org.apache.coyote.http11.PagePathMapper.INDEX_PAGE;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class IndexServlet extends Servlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod httpMethod = httpRequest.getMethod();
        if (httpMethod.isEqualTo(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String absolutePath = INDEX_PAGE.path();
        String content = findResourceWithPath(absolutePath);
        ResponseBody responseBody = new ResponseBody(content);

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .addHeader(CONTENT_TYPE, ContentType.parse(absolutePath))
                .addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length))
                .setResponseBody(responseBody);
    }
}
