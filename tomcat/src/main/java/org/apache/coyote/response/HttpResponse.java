package org.apache.coyote.response;

import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.protocolVersion.ProtocolVersion;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.body.ResponseBody;
import org.apache.coyote.response.header.ResponseHeader;
import org.apache.coyote.response.responseLine.ResponseLine;
import org.apache.coyote.util.ContentType;
import org.apache.coyote.util.HttpStatus;
import org.apache.coyote.util.ResourceFinder;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String SPACE = " ";
    private static final String BLANK = "";
    private static final String CRLF = "\r\n";

    private final ResponseLine responseLine;
    private final ResponseHeader header;
    private final ResponseBody responseBody;
    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.responseLine = new ResponseLine(ProtocolVersion.ofHTTP1());
        this.header = new ResponseHeader();
        this.responseBody = new ResponseBody();
        this.outputStream = outputStream;
    }

    public void setCookies(String sessionId) {
        header.addCookie(HttpCookie.ofJSessionId(sessionId).combineCookie());
    }

    public byte[] combineResponseToBytes() {
        String response = String.join(CRLF,
                responseLine.toCombinedResponse() + SPACE,
                header.toCombinedHeader(),
                BLANK,
                responseBody.getBody());

        return response.getBytes();
    }

    public void sendError(HttpStatus httpStatus) {
        sendRedirect(httpStatus.getStatusCode() + ContentType.HTML.getExtension());
    }

    public void sendRedirect(String location) {
        header.addLocation(location);
        responseLine.setHttpStatus(HttpStatus.FOUND);

        flushBuffer();
    }

    public void sendStaticResourceResponse(HttpRequest httpRequest, HttpStatus httpStatus) {
        String resource = findResource(httpRequest);

        setBody(resource, httpStatus, httpRequest.findContentType(), resource.getBytes().length);
        flushBuffer();
    }

    private String findResource(HttpRequest httpRequest) {
        String resource = "";
        try {
            resource = ResourceFinder.findBy(httpRequest.getResourcePath());
        } catch (IllegalArgumentException e) {
            sendError(HttpStatus.NOT_FOUND);
        }
        return resource;
    }

    public void sendDefaultResponse() {
        setBody(DEFAULT_BODY, HttpStatus.OK, ContentType.PLAIN, DEFAULT_BODY.getBytes().length);
        flushBuffer();
    }

    private void flushBuffer() {
        try {
            outputStream.write(combineResponseToBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void setBody(String body, HttpStatus httpStatus, ContentType contentType, int length) {
        responseBody.setBody(body);
        responseLine.setHttpStatus(httpStatus);
        header.addContentLengthAndType(contentType, length);
    }
}
