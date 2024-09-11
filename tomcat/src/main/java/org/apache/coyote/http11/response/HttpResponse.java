package org.apache.coyote.http11.response;

import java.util.StringJoiner;
import org.apache.coyote.HttpStatusCode;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeader header;
    private ResponseBody body;

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader header) {
        this(statusCode, header, null);
    }

    public HttpResponse() {
    }

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader header, byte[] body) {
        this.statusLine = StatusLine.ofHTTP11(statusCode);
        this.header = header;
        this.body = new ResponseBody(body);
        header.setContentLength(String.valueOf(this.body.getBodyLength()));
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setHeader(ResponseHeader header) {
        this.header = header;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
        this.header.setContentLength(String.valueOf(this.body.getBodyLength()));
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

    public void setStatus(HttpStatusCode httpStatusCode) {

    }
}
