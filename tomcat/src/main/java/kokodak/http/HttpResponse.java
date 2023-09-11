package kokodak.http;

import static kokodak.Constants.BLANK;
import static kokodak.Constants.COLON;
import static kokodak.Constants.CRLF;
import static kokodak.http.HeaderConstants.CONTENT_LENGTH;
import static kokodak.http.HeaderConstants.CONTENT_TYPE;
import static kokodak.http.HeaderConstants.LOCATION;
import static kokodak.http.HeaderConstants.SET_COOKIE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpVersion httpVersion;

    private HttpStatusCode httpStatusCode;

    private Map<String, String> header;

    private String body;

    public HttpResponse() {
        this(HttpVersion.HTTP11,
             HttpStatusCode.OK,
             new HashMap<>(),
             "");
    }

    public HttpResponse(final HttpVersion httpVersion,
                        final HttpStatusCode httpStatusCode,
                        final Map<String, String> header,
                        final String body) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.header = header;
        this.body = body;
    }

    public String generateResponseMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        generateStatusLine(stringBuilder);
        generateHeader(stringBuilder);
        generateBody(stringBuilder);
        return stringBuilder.toString();
    }

    private void generateStatusLine(final StringBuilder stringBuilder) {
        stringBuilder.append(httpVersion.getValue())
                     .append(BLANK.getValue())
                     .append(httpStatusCode.getStatusCode())
                     .append(BLANK.getValue())
                     .append(httpStatusCode.getStatusMessage())
                     .append(BLANK.getValue())
                     .append(CRLF.getValue());
    }

    private void generateHeader(final StringBuilder stringBuilder) {
        header.forEach((key, value) -> stringBuilder.append(key)
                                                    .append(COLON.getValue())
                                                    .append(BLANK.getValue())
                                                    .append(value)
                                                    .append(BLANK.getValue())
                                                    .append(CRLF.getValue()));
        stringBuilder.append(CRLF.getValue());
    }

    private void generateBody(final StringBuilder stringBuilder) {
        stringBuilder.append(body);
    }

    public void header(final String key, final String value) {
        header.put(key, value);
    }

    public void redirect(final String redirectPath) {
        setHttpStatusCode(HttpStatusCode.FOUND);
        header(LOCATION, redirectPath);
    }

    public void cookie(final String value) {
        header(SET_COOKIE, value);
    }

    public void notFound() throws IOException {
        final String fileName = "static/404.html";
        setBody(fileName, "");
    }

    public void setHttpVersion(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHttpStatusCode(final HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setBody(final String fileName, final String acceptHeader) throws IOException {
        final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
        final File file = new File(resourceUrl.getPath());
        final String responseBody = new String(Files.readAllBytes(file.toPath()));
        final String contentType = ContentType.from(fileName, acceptHeader);
        header(CONTENT_TYPE, contentType);
        header(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        this.body = responseBody;
    }

    public void setBody(final String body) {
        header(CONTENT_TYPE, ContentType.TEXT_HTML.getContentType());
        header(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.body = body;
    }
}
