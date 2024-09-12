package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.Session;
import org.apache.coyote.http11.HttpCookie;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String CHARSET = StandardCharsets.UTF_8.name();

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

    public void setBody(byte[] body) {
        this.body = new ResponseBody(body);
        this.header.setContentLength(String.valueOf(this.body.getBodyLength()));
    }

    public void setStatus(HttpStatusCode httpStatusCode) {
        statusLine.setStatusCode(httpStatusCode);
    }

    public boolean existsSession() {
        return header.existsSession();
    }

    public Session getSession() {
        HttpCookie cookies = header.getCookies();
        return new Session(cookies.getJsessionid());
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner(CRLF);

        stringJoiner.add(statusLine.getReponseString());
        stringJoiner.add(header.toHeaderString());
        stringJoiner.add(CRLF);

        byte[] headerBytes = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);
        if (!body.isEmpty()) {
            return copyBytes(headerBytes);
        }
        return headerBytes;
    }

    private byte[] copyBytes(byte[] headerBytes) {
        byte[] response = new byte[headerBytes.length + body.getBodyLength()];
        System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
        System.arraycopy(body.getBody(), 0, response, headerBytes.length, body.getBodyLength());
        return response;
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
