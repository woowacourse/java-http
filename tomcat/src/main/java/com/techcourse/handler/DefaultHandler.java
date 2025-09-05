package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.OK;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestHandler;
import org.apache.coyote.HttpResponse;

public class DefaultHandler implements HttpRequestHandler {

    private static final String STATIC_FILE_PATH_PREFIX = "static";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    @Override
    public void handleGet(HttpRequest request, HttpResponse response) {
        final Optional<String> contentOpt = getResourceContent(STATIC_FILE_PATH_PREFIX + request.getPath());
        if (contentOpt.isEmpty()) {
            throw new NoSuchElementException("해당 경로에 파일이 존재하지 않습니다: " + request.getPath());
        }

        final String mimeType = getMimeTypeOrDefault(request.getPath());
        final String content = contentOpt.get();
        response.setStatus(OK);
        response.setContentType(mimeType + ";charset=utf-8");
        response.setBody(content);
    }

    private String getMimeTypeOrDefault(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        if (mimeType == null) {
            mimeType = DEFAULT_CONTENT_TYPE;
        }
        return mimeType;
    }

    private Optional<String> getResourceContent(String resourcePath) {
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return Optional.empty();
            }

            return Optional.of(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}
