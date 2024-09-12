package org.apache.coyote.http11.response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

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
        addHeader("Location", url);
    }

    public void getStaticResource(String url) {
        try {
            final Path path = findPath(url);
            byte[] fileBytes = Files.readAllBytes(path);
            String contentType = URLConnection.guessContentTypeFromName(path.toString());

            setStatusCode(StatusCode.OK);
            addHeader("Content-Type", contentType + ";charset=utf-8");
            addHeader("Content-Length", String.valueOf(fileBytes.length));
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

    private Path findPath(String requestURL) throws FileNotFoundException, URISyntaxException {
        if (!requestURL.contains(".")) {
            requestURL += ".html";
        }

        URL resource = getClass().getClassLoader().getResource("static" + requestURL);
        if (resource == null) {
            throw new FileNotFoundException();
        }

        return Path.of(resource.toURI());
    }
}
