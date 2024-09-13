package org.apache.coyote.http11.response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.handler.StaticResourceHandler;

public class HttpResponse {

    private StatusCode statusCode;
    private final HttpResponseHeader header;
    private byte[] body;

    public HttpResponse(StatusCode statusCode) {
        this.statusCode = statusCode;
        this.header = new HttpResponseHeader();
    }

    public HttpResponse() {
        this.header = new HttpResponseHeader();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void redirect(String url) {
        setStatusCode(StatusCode.FOUND);
        addHeader(HttpHeaders.LOCATION, url);
    }

    public void getStaticResource(String url) {
        try {
            StaticResourceHandler handler = new StaticResourceHandler(url);
            byte[] fileBytes = handler.getResource();
            String contentType = handler.getContentType();

            setStatusCode(StatusCode.OK);
            addHeader(HttpHeaders.CONTENT_TYPE, contentType + HttpHeaders.CHARSET_UTF_8);
            addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileBytes.length));
            setBody(fileBytes);

        } catch (FileNotFoundException | URISyntaxException e) {
            redirect("/404.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] buildResponse() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage())
                .append(" \r\n");
        response.append(header.buildResponse());
        response.append("\r\n");
        if (body != null) {
            response.append(new String(body));
        }

        return response.toString().getBytes();
    }
}
