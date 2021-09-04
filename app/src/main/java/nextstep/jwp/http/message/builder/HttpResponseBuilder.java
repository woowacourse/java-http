package nextstep.jwp.http.message.builder;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.common.HttpUri;
import nextstep.jwp.http.common.HttpVersion;
import nextstep.jwp.http.common.MediaType;
import nextstep.jwp.http.file.FileExtension;
import nextstep.jwp.http.file.StaticFile;
import nextstep.jwp.http.file.StaticFileFinder;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.http.message.response.ResponseHeader;
import nextstep.jwp.http.message.response.StatusLine;

public class HttpResponseBuilder {

    private static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP_1_1;

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
        return new HttpResponseBuilder(
                new StatusLine(DEFAULT_HTTP_VERSION, HttpStatusCode.OK)
        );
    }

    public static HttpResponseBuilder ok(String body) {
        return ok().body(body);
    }

    public static HttpResponseBuilder redirectTemporarily(String redirectUrl) {
        return new HttpResponseBuilder(
                new StatusLine(DEFAULT_HTTP_VERSION, HttpStatusCode.FOUND)
        ).location(redirectUrl);
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
        this.responseBody = new MessageBody(bytes);
        return this;
    }

    public HttpResponseBuilder body(String body) {
        this.responseBody = new MessageBody(body);
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
        int length = new MessageBody(body).bodyLength();
        return contentLength(length);
    }

    public HttpResponseBuilder contentLength(int contentLength) {
        this.responseHeader.putContentLength(contentLength);
        return this;
    }
}
