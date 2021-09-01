package nextstep.jwp.model.web.response;

import nextstep.jwp.model.web.Headers;
import nextstep.jwp.model.web.StatusCode;

import java.util.Map;

public class CustomHttpResponse {

    private StatusLine statusLine;
    private Headers headers;
    private ResponseBody responseBody;

    public void setStatusLine(StatusCode status, String versionOfProtocol) {
        statusLine = new StatusLine(versionOfProtocol, status.getStatusCode(), status.getStatusMessage());
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = new Headers(headers);
    }

    public void setResponseBody(String body) {
        this.responseBody = new ResponseBody(body);
    }

    public byte[] getBodyBytes() {
        return String.join("\r\n",
                statusLine.asString(),
                headers.asString(),
                "",
                responseBody.getBody()).getBytes();
    }
}


/*
    public static String found(String redirectUri) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: http://localhost:8080/"+ redirectUri +" ",
                "",
                "");
    }

    public static String ok(String contentType, String fileSource) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + fileSource.getBytes().length + " ",
                "",
                fileSource);
    }

    public static String notFound(String redirectUri) {
        return String.join("\r\n",
                "HTTP/1.1 404 NOT FOUND ",
                "Location: http://localhost:8080/"+ redirectUri +" ",
                "",
                "");
    }
 */