package nextstep.jwp.servlets;

import static org.apache.coyote.http11.PagePathMapper.INDEX_PAGE;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_TYPE;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;
import java.io.IOException;

public class IndexServlet extends HttpServlet {

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
