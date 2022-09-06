package org.apache.coyote.http11.response.file;

import static org.apache.coyote.http11.HttpStatus.OK;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.ServletResponseEntity;
import org.apache.coyote.http11.response.ResponseEntity;

public class FileHandler {

    public static boolean isStaticFileResource(final String resource) {
        final URL url = FileHandler.class.getClassLoader().getResource("static" + resource);
        return url != null;
    }

    public static ResponseEntity createFileResponse(final String resource) throws IOException {
        final URL url = FileHandler.class.getClassLoader().getResource("static" + resource);
        final Path path = Path.of(url.getPath());
        final byte[] fileBytes = Files.readAllBytes(path);

        return new ResponseEntity(OK, getFileHttpHeader(path), new String(fileBytes));
    }

    private static HttpHeader getFileHttpHeader(final Path path) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", Files.probeContentType(path));

        return new HttpHeader(headers);
    }

    public static ResponseEntity createFileResponse(final ServletResponseEntity response) throws IOException {
        final URL url = FileHandler.class.getClassLoader().getResource("static" + response.getResource());
        final Path path = Path.of(url.getPath());
        final byte[] fileBytes = Files.readAllBytes(path);

        return new ResponseEntity(response.getHttpStatus(), getFileHttpHeader(path), new String(fileBytes));
    }

    public static ResponseEntity createErrorFileResponse(final HttpStatus httpStatus) throws IOException {
        final URL url = FileHandler.class.getClassLoader()
                .getResource("static/" + httpStatus.getStatusCode() + ".html");
        final Path path = Path.of(url.getPath());
        final byte[] fileBytes = Files.readAllBytes(path);

        return new ResponseEntity(httpStatus, getFileHttpHeader(path), new String(fileBytes));
    }
}
