package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class RequestURL {
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final String ROOT_URL = "/";

    private final HttpMethod method;
    private final String url;
    private final String version;
    private final String extension;

    private RequestURL(final HttpMethod method, final String url, final String version, final String extension) {
        this.method = method;
        this.url = url;
        this.version = version;
        this.extension = extension;
    }

    public static RequestURL from(String line) {
        validate(line);

        final String[] splitedLine = line.split(" ");
        return new RequestURL(getMethod(splitedLine[METHOD_INDEX]), splitedLine[URL_INDEX],
                splitedLine[VERSION_INDEX], getExtension(splitedLine[URL_INDEX]));
    }

    private static void validate(final String line) {
        if (line.isBlank() && line.isEmpty()) {
            throw new IllegalArgumentException("잘못된 RequestURL입니다.");
        }
    }

    public String getResource() throws IOException {
        if (url.equals(ROOT_URL)) {
            return "Hello world!";
        }
        return findResource();
    }

    private String findResource() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        return new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));
    }

    private static String getExtension(final String url) {
        final String[] splitedUrl = url.split("\\.");
        if(splitedUrl.length>1) {
            return splitedUrl[splitedUrl.length - 1];
        }
        return "html";
    }

    private static HttpMethod getMethod(final String method) {
        return HttpMethod.valueOf(method);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getExtension() {
        return extension;
    }
}
