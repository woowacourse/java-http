package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.http11.ContentType.*;
import static org.apache.coyote.http11.Header.*;
import static org.apache.coyote.http11.StatusCode.*;

public class Response {

    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";
    private static final String SPACE_CRLF = " \r\n";
    private static final String COLON = ": ";
    private static final String HELLO_WORLD = "Hello world!";
    private static final String ROOT = "/";
    private final RequestReader requestReader;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;
    private String responseFormat;

    public Response(RequestReader requestReader) throws IOException {
        this.requestReader = requestReader;
        setResponse();
    }

    private void setResponse() throws IOException {
        String resource = requestReader.read();
        String accept = requestReader.getAccept();

        if (TEXT_HTML.in(accept)) {
            read(TEXT_HTML, resource);
        } else if (TEXT_CSS.in(accept)) {
            read(TEXT_CSS, resource);
        } else if (APPLICATION_JAVASCRIPT.in(accept)) {
            read(APPLICATION_JAVASCRIPT, resource);
        } else {
            read(ALL, resource);
        }
    }

    public void read(ContentType contentType, String resource) throws IOException {
        body = getBody(resource);
        String typeValue = contentType.getValue();
        if (contentType.equals(TEXT_CSS) || contentType.equals(TEXT_HTML)) {
            typeValue += ";charset=utf-8";
        }
        join(typeValue, body);
    }

    private void join(String typeValue, String body) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(requestReader.getProtocol()).append(SPACE).append(OK.status()).append(SPACE_CRLF);
        addBaseHeaders(typeValue);
        headers.forEach((key, value) -> stringBuilder.append(key).append(COLON).append(value).append(SPACE_CRLF));
        stringBuilder.append(CRLF).append(body);

        responseFormat = stringBuilder.toString();
    }

    private void addBaseHeaders(String typeValue){
        headers.put(CONTENT_TYPE.getName(), typeValue);
        headers.put(CONTENT_LENGTH.getName(), String.valueOf(body.getBytes().length));
    }

    private static String getBody(String resource) throws IOException {
        if (resource.equals(ROOT)) {
            return HELLO_WORLD;
        }
        RequestUrl requestUrl = new RequestUrl(resource);
        URL url = requestUrl.getUrl();

        Path path = new File(Objects.requireNonNull(url).getFile()).toPath();
        return new String(Files.readAllBytes(path));
    }

    public byte[] getResponse() {
        return responseFormat.getBytes();
    }
}
