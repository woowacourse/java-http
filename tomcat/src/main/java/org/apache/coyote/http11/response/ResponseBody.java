package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import nextstep.jwp.exception.ResourceNotFoundException;

public class ResponseBody {

    private static final String STATIC_PATH = "static/";

    private final String value;

    private ResponseBody(String value) {
        this.value = value;
    }

    public static ResponseBody of(ResponseEntity responseEntity, ResponseHeaders responseHeaders) {
        if (responseEntity.getHttpStatus().isRedirect()) {
            return new ResponseBody("");
        }
        String processResponseBody = processResponseBody(responseEntity.getResponseBody());
        responseHeaders.setContentLength(processResponseBody.getBytes().length);
        return new ResponseBody(processResponseBody);
    }

    private static String processResponseBody(String response) {
        if (doesNeedViewFile(response)) {
            return readFile(response);
        }
        return response;
    }

    private static boolean doesNeedViewFile(String response) {
        return response.contains(".");
    }

    private static String readFile(String fileName) {
        final byte[] lines;
        try {
            final URI fileUri = Objects.requireNonNull(getResourceUrl(fileName)).toURI();
            lines = Files.readAllBytes(Paths.get(fileUri));
        } catch (URISyntaxException e) {
            throw new ResourceNotFoundException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(lines);
    }

    private static URL getResourceUrl(String fileName) {
        return ResponseBody.class.getClassLoader().getResource(STATIC_PATH + fileName);
    }

    public String value() {
        return value;
    }
}
