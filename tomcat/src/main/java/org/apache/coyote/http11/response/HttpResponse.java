package org.apache.coyote.http11.response;

import java.util.StringJoiner;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeader header;
    private ResponseBody body;

    public HttpResponse() {
        this(null, new ResponseHeader(), null);
    }

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader header, byte[] body) {
        this.statusLine = StatusLine.ofHTTP11(statusCode);
        this.header = header;
        this.body = new ResponseBody(body);
        header.setContentLength(String.valueOf(this.body.getBodyLength()));
    }

    public void setMimeType(MimeType mimeType) {
        header.setContentType(mimeType);
    }

    public void setLocation(String location) {
        header.setLocation(location);
    }

    public void setCookie(String cookie) {
        header.setCookie(cookie);
    }

    public void setBody(ResponseBody body) {
        this.body = body;
        this.header.setContentLength(String.valueOf(this.body.getBodyLength()));
    }

    public void setStatus(HttpStatusCode httpStatusCode) {
        statusLine.setStatusCode(httpStatusCode);
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");

        stringJoiner.add(statusLine.getReponseString());
        stringJoiner.add(header.toHeaderString());
        stringJoiner.add("\r\n");

        byte[] headerBytes = stringJoiner.toString().getBytes();
        if (!body.isEmpty()) {
            byte[] response = new byte[headerBytes.length + body.getBodyLength()];

            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(body.getBody(), 0, response, headerBytes.length, body.getBodyLength());

            return response;
        }
        return headerBytes;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}
