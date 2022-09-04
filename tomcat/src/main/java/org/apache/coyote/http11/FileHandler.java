package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatus.OK;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

        return new ResponseEntity(OK, Files.probeContentType(path), new String(fileBytes));
    }

    public static ResponseEntity createErrorFileResponse(final HttpStatus httpStatus) throws IOException {
        final URL url = FileHandler.class.getClassLoader()
                .getResource("static/" + httpStatus.getStatusCode() + ".html");
        final Path path = Path.of(url.getPath());
        final byte[] fileBytes = Files.readAllBytes(path);

        return new ResponseEntity(httpStatus, Files.probeContentType(path), new String(fileBytes));
    }
}
