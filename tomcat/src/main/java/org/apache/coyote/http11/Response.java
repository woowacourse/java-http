package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.isNull;

public class Response {

    private static final int LOCATION_INDEX = 1;
    private static final String HTTP_HEADER_DELIMITER = " ";
    private static final String BASE_PATH = "static";
    private static final String ROOT_RESPONSE = "Hello world!";

    private final Path path;
    private final ContentType contentType;

    public Response(final Path path, final ContentType contentType) {
        this.path = path;
        this.contentType = contentType;
    }

    public static Response from(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        final String location = br.readLine().split(HTTP_HEADER_DELIMITER)[LOCATION_INDEX];

        final ContentType contentType = ContentType.from(location);
        final Path path = getPath(location, contentType);

        return new Response(path, contentType);
    }

    private static Path getPath(final String location, final ContentType contentType) {
        final ClassLoader classLoader = Response.class.getClassLoader();
        final String locationWithoutExtension = location.replaceAll("\\.[^.]+$", "");
        final URL resource = classLoader.getResource(BASE_PATH + locationWithoutExtension + contentType.getExtension());
        
        if (isNull(resource)) {
            throw new IllegalArgumentException("존재하지 않는 자원입니다.");
        }
        return Paths.get(resource.getPath());
    }

    public String get() throws IOException {
        final String body = makeResponseBody();
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType.toHeader(),
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String makeResponseBody() throws IOException {
        if (Files.isDirectory(path)) {
            return ROOT_RESPONSE;
        }
        return String.join("\n", Files.readAllLines(path)) + "\n";
    }
}
