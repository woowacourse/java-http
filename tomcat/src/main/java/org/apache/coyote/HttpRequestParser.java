package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

public class HttpRequestParser {
    public static HttpRequest parseRequest(InputStream inputStream) throws IOException {
        return parseGetRequest(inputStream);
    }

    public static HttpRequest parseGetRequest(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String request = bufferedReader.readLine();

        final var requestStartLine = request.split(" ");
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + requestStartLine[0]))
                .method(requestStartLine[1], BodyPublishers.noBody())
                .version(getHttpVersion(requestStartLine[2]))
                .build();
    }

    private static Version getHttpVersion(String version) {
        return Version.valueOf(version.replace("/", "_").replace(".", "_"));
    }
}
