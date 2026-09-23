package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.session.Session;

public class HttpResponse {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOCATION = "Location";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;
    private ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
            new ReasonPhrase("OK"));
    private HttpHeaders httpHeaders = new HttpHeaders();
    private HttpBody httpBody = new HttpBody("");

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send() throws IOException {
        setContentLength();
        outputStream.write(buildResponse().getBytes());
        outputStream.flush();
    }

    public void sendRedirect(String redirectUrl) throws IOException {
        responseLine = new ResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                new ReasonPhrase("Found"));
        setLocation(redirectUrl);
        send();
    }

    public void setHttpBody(HttpBody body) {
        this.httpBody = body;
    }

    public void setSession(Session session) {
        httpHeaders.put(SET_COOKIE, JSESSIONID + "=" + session.getId());
    }

    public void setLocation(String location) {
        httpHeaders.put(LOCATION, location);
    }

    public void setContentType(ContentType contentType) {
        httpHeaders.put(CONTENT_TYPE, contentType.getWithUTF8Encoding());
    }

    private void setContentLength() {
        httpHeaders.put(CONTENT_LENGTH, String.valueOf(httpBody.getLength()));
    }

    private String buildResponse() {
        StringBuilder response = new StringBuilder();

        response.append(responseLine.getHttpVersion().getValue()).append(' ')
                .append(responseLine.getHttpStatusCode().getValue()).append(' ')
                .append(responseLine.getReasonPhrase().getValue()).append(' ')
                .append(CRLF);

        for (String name : httpHeaders.getHeaders().keySet()) {
            response.append(name)
                    .append(": ")
                    .append(httpHeaders.getHeaders().get(name)).append(' ')
                    .append(CRLF);
        }

        response.append(CRLF);
        response.append(httpBody.getValue());

        return response.toString();
    }
}
