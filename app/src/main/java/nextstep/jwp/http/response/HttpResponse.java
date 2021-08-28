package nextstep.jwp.http.response;

import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpProtocol;
import nextstep.jwp.resource.FileType;

public class HttpResponse {

    private final HttpProtocol protocol;
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final HttpResponseBody responseBody;

    public HttpResponse(HttpProtocol protocol, HttpStatus status, HttpHeaders headers,
                        HttpResponseBody responseBody) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(HttpProtocol protocol, HttpStatus status, FileType fileType,
                                  String responseBody) {
        HttpHeaders httpHeaders = new HttpHeaders();
        ContentType contentType = ContentType.findByFileType(fileType);
        httpHeaders.add("Content-Type", contentType.getText());
        httpHeaders.add("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new HttpResponse(protocol, status, httpHeaders,
            new TextHttpResponseBody(responseBody)
        );
    }

    public static HttpResponse ok(HttpProtocol protocol, FileType fileType, String responseBody) {
        return HttpResponse.of(protocol, HttpStatus.OK, fileType, responseBody);
    }

    public HttpProtocol protocol() {
        return protocol;
    }

    public HttpStatus status() {
        return status;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public HttpResponseBody responseBody() {
        return responseBody;
    }

    public String protocolName() {
        return protocol.getProtocolName();
    }

    public int statusCode() {
        return status.getCode();
    }

    public String statusName() {
        return status.getMessage();
    }
}
