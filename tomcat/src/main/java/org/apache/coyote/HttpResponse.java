package org.apache.coyote;

import org.apache.coyote.header.HttpHeaders;

public class HttpResponse {

    private ProtocolVersion version;
    private HttpStatus status;
    private HttpHeaders headers;
    private String body;

    public HttpResponse() {
        this.headers = new HttpHeaders();
        this.body = "";
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void setVersion(ProtocolVersion version) {
        this.version = version;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String toString() {
        return String.join("\r\n",
                version.value() + " " + status.code() + " " + status.message() + " ",
                headers.toString(),
                "",
                body);
    }
}
