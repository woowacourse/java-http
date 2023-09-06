package servlet.response;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpHeaderConsts;

public class HttpResponse {

    private final List<HeaderDto> headerDtos = new ArrayList<>();
    private final List<HttpCookie> cookies = new ArrayList<>();
    private HttpStatusCode statusCode = HttpStatusCode.OK;
    private ContentType contentType = ContentType.TEXT_HTML;
    private String body = HttpConsts.BLANK;

    public void sendRedirect(final String redirectPath) {
        headerDtos.add(new HeaderDto(HttpHeaderConsts.LOCATION, redirectPath));
        statusCode = HttpStatusCode.FOUND;
    }

    public void addCookie(final HttpCookie cookie) {
        cookies.add(cookie);
    }

    public void setStatusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }

    public void setHeader(final String name, final String value) {
        headerDtos.add(new HeaderDto(name, value));
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public List<HeaderDto> headerDtos() {
        return headerDtos;
    }

    public List<HttpCookie> cookies() {
        return cookies;
    }

    public HttpStatusCode statusCode() {
        return statusCode;
    }

    public ContentType contentType() {
        return contentType;
    }

    public String body() {
        return body;
    }
}
