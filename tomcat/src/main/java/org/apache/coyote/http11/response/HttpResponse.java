package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.HttpHeader;

public class HttpResponse {

    private static final String comma = "\\.";
    private static final String LOCATION = "Location";
    private static final String CRLF = "\r\n";
    private static final String FOUND = "Found";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    private StatusLine statusLine;
    private HttpHeader httpHeader;
    private String body;


    public HttpResponse() {
        this.httpHeader = new HttpHeader();
    }

    public void setRoot(String protocol) {
        body = "Hello world!";
        statusLine = new StatusLine(protocol, "200", "OK");
        httpHeader.putHeader(CONTENT_TYPE, TEXT_HTML_CHARSET_UTF_8);
        httpHeader.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void setResource(String protocol, String url) throws IOException {
        URL path = getClass().getClassLoader().getResource("static" + url);
        body = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
        statusLine = new StatusLine(protocol, "200", "OK");

        httpHeader.putContentType(getExtension(url));
        httpHeader.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void setNotFound(String protocol) throws IOException {
        URL path = getClass().getClassLoader().getResource("static" + "/404.html");
        body = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
        statusLine = new StatusLine(protocol, "404", "Not Found");

        httpHeader.putHeader(CONTENT_TYPE, TEXT_HTML_CHARSET_UTF_8);
        httpHeader.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void setLoginFail(String protocol) throws IOException {
        URL path = getClass().getClassLoader().getResource("static" + "/500.html");
        body = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
        statusLine = new StatusLine(protocol, "500", "Internal Server Error");

        httpHeader.putHeader(CONTENT_TYPE, TEXT_HTML_CHARSET_UTF_8);
        httpHeader.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void setLoginPage(String protocol, String url) throws IOException {
        URL path = getClass().getClassLoader().getResource("static" + url + ".html");
        body = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
        statusLine = new StatusLine(protocol, "200", "OK");
        httpHeader.putHeader(CONTENT_TYPE, TEXT_HTML_CHARSET_UTF_8);
        httpHeader.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void successLogin(String protocol) {
        statusLine = new StatusLine(protocol, "302", FOUND);
        httpHeader.putHeader(LOCATION, "/index.html");
    }

    public void failLogin(String protocol) {
        statusLine = new StatusLine(protocol, "302", FOUND);
        httpHeader.putHeader(LOCATION, "/401.html");
    }

    public void putHeader(String key, String value) {
        httpHeader.putHeader(key, value);
    }

    public String getExtension(String url) {
        String[] types = url.split(comma);
        return types[1];
    }

    @Override
    public String toString() {
        return String.join(CRLF,
                statusLine.getStatusLine(),
                httpHeader.getHttpHeader(),
                body);
    }
}
