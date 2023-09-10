package org.apache.coyote.response;

import org.apache.coyote.common.FileType;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.PathUrl;
import org.apache.coyote.http11.FileUtil;
import org.apache.coyote.response.header.*;

public class HttpResponse {

    private final ResponseStartLine responseStartLine;
    private final ResponseHeader responseHeader;
    private String responseBody;

    private HttpResponse(final ResponseStartLine responseStartLine, final ResponseHeader responseHeader) {
        this.responseStartLine = responseStartLine;
        this.responseHeader = responseHeader;
         this.responseBody =  "";
    }

    public static HttpResponse create(final HttpVersion httpVersion) {
        return new HttpResponse(
                ResponseStartLine.from(httpVersion),
                ResponseHeader.create()
        );
    }

    public void setRedirect(final String redirectPath) {
        final Location location = new Location(redirectPath);
        responseHeader.add(location);
    }

    public void addCookie(final String cookie) {
        final SetCookie setCookie = new SetCookie(cookie);
        responseHeader.add(setCookie);
    }

    public void setFileAsBody(final PathUrl requestUrl, final String contentType) {
        responseBody = FileUtil.getResource(this, requestUrl);
        responseHeader.add(new ContentType(contentType));
        responseHeader.add(new ContentLength(responseBody.getBytes().length));
    }

    public void setStringAsBody(final String string) {
        responseBody = string;
        responseHeader.add(new ContentType(FileType.TEXT.getContentType()));
        responseHeader.add(new ContentLength(responseBody.getBytes().length));
    }

    public void setViewPathAsBody(final String viewPath) {
        responseBody = FileUtil.getResourceFromViewPath(this, viewPath);
        responseHeader.add(new ContentLength(responseBody.getBytes().length));
        responseHeader.add(new ContentType(FileType.HTML.getContentType()));
    }

    public void setStatus(final ResponseStatus responseStatus) {
        responseStartLine.setStatus(responseStatus);
    }

    public void setViewPathAsBodyAndSetStatus(final String viewPath, final ResponseStatus responseStatus) {
        setViewPathAsBody(viewPath);
        setStatus(responseStatus);
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                responseStartLine + " ",
                responseHeader + " ",
                "",
                responseBody);
    }
}
