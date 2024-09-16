package org.apache.coyote.http11.response;

import org.apache.coyote.http11.MimeType;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class HttpResponse {

    private static final int INDEX_ZERO = 0;
    private static final String CRLF = "\r\n";

    private final StatusLine statusLine;
    private final ResponseHeader requestHeader;
    private ResponseBody responseBody;

    public HttpResponse() {
        this(null, new ResponseHeader(), null);
    }

    public HttpResponse(HttpStatus httpStatus, ResponseHeader responseHeader, byte[] contents) {
        this.statusLine = StatusLine.of11(httpStatus);
        this.requestHeader = responseHeader;
        this.responseBody = new ResponseBody(contents);

        setContentLength(responseBody.getLength());
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner(CRLF);

        stringJoiner.add(statusLine.getContents());
        stringJoiner.add(requestHeader.getContents());
        stringJoiner.add(CRLF);

        byte[] headerBytes = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);

        if (responseBody.isEmpty()) {
            return headerBytes;
        }

        return createFullResponse(headerBytes);
    }

    private byte[] createFullResponse(byte[] headerBytes) {
        byte[] response = new byte[headerBytes.length + responseBody.getLength()];

        System.arraycopy(headerBytes, INDEX_ZERO, response, INDEX_ZERO, headerBytes.length);
        System.arraycopy(responseBody.getValues(), INDEX_ZERO, response, headerBytes.length, responseBody.getLength());

        return response;
    }

    public void setStatus(HttpStatus httpStatus) {
        statusLine.setHttpStatus(httpStatus);
    }

    public void setContentLength(int contentLength) {
        requestHeader.setContentLength(String.valueOf(contentLength));
    }

    public void setMimeType(MimeType mimeType) {
        requestHeader.setContentType(mimeType);
    }

    public void setCookie(String cookie) {
        requestHeader.setCookie(cookie);
    }

    public void setLocation(String location) {
        requestHeader.setLocation(location);
    }

    public void setResponseBody(byte[] values) {
        this.responseBody = new ResponseBody(values);
        setContentLength(responseBody.getLength());
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeader getRequestHeader() {
        return requestHeader;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }
}
