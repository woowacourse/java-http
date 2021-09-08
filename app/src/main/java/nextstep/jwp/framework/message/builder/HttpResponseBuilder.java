package nextstep.jwp.framework.message.builder;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.common.HttpUri;
import nextstep.jwp.framework.common.HttpVersion;
import nextstep.jwp.framework.common.MediaType;
import nextstep.jwp.framework.file.FileExtension;
import nextstep.jwp.framework.file.StaticFile;
import nextstep.jwp.framework.file.StaticFileFinder;
import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import nextstep.jwp.framework.message.response.ResponseHeader;
import nextstep.jwp.framework.message.response.StatusLine;

public class HttpResponseBuilder {

    private static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP_1_1;
    private static final String JSESSIONID = "JSESSIONID";

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private MessageBody responseBody;

    private HttpResponseBuilder(StatusLine statusLine) {
        this(statusLine, new ResponseHeader(), MessageBody.empty());
    }

    private HttpResponseBuilder(StatusLine statusLine, ResponseHeader responseHeader, MessageBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder status(HttpStatusCode httpStatusCode) {
        return new HttpResponseBuilder(
                new StatusLine(DEFAULT_HTTP_VERSION, httpStatusCode)
        );
    }

    public static HttpResponseBuilder ok() {
        return status(HttpStatusCode.OK);
    }

    public static HttpResponseBuilder ok(String body) {
        return ok().body(body);
    }

    public static HttpResponseBuilder redirectTemporarily(String redirectUrl) {
        return status(HttpStatusCode.FOUND)
                .location(redirectUrl);
    }

    public static HttpResponseBuilder staticResource(String path) {
        HttpUri httpUri = new HttpUri(path);
        StaticFileFinder staticFileFinder = new StaticFileFinder(httpUri.removedQueryStringPath());
        StaticFile staticFile = staticFileFinder.find();
        FileExtension fileExtension = staticFile.fileExtension();
        return ok().contentType(MediaType.from(fileExtension))
                .contentLength(staticFile.byteLength())
                .body(staticFile.toBytes());
    }

    public HttpResponseMessage build() {
        return new HttpResponseMessage(statusLine, responseHeader, responseBody);
    }

    public HttpResponseBuilder body(byte[] bytes) {
        this.responseBody = MessageBody.from(bytes);
        return this;
    }

    public HttpResponseBuilder body(String body) {
        this.responseBody = MessageBody.from(body);
        return this;
    }

    public HttpResponseBuilder header(String key, String value) {
        this.responseHeader.put(key, value);
        return this;
    }

    public HttpResponseBuilder location(String uri) {
        this.responseHeader.putLocation(uri);
        return this;
    }

    public HttpResponseBuilder contentType(MediaType contentType) {
        this.responseHeader.putContentType(contentType);
        return this;
    }

    public HttpResponseBuilder contentLength(String body) {
        int length = MessageBody.from(body).bodyLength();
        return contentLength(length);
    }

    public HttpResponseBuilder contentLength(int contentLength) {
        this.responseHeader.putContentLength(contentLength);
        return this;
    }

    public HttpResponseBuilder setCookie(String name, String value) {
        this.responseHeader.putSetCookie(name, value);
        return this;
    }

    public HttpResponseBuilder setSessionCookie(String sessionId) {
        setCookie(JSESSIONID, sessionId);
        return this;
    }
}
