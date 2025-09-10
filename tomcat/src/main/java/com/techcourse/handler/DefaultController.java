package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.OK;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.apache.coyote.HttpHeaderName;

public class DefaultController extends AbstractController {

    private static final String STATIC_FILE_PATH_PREFIX = "static";
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    @Override
    public void doGet(ServletRequest request, ServletResponse response) {
        final String content = getResourceContent(STATIC_FILE_PATH_PREFIX + request.getPath().getValue())
                .orElseThrow(() -> new NoSuchElementException("해당 경로에 파일이 존재하지 않습니다: " + request.getPath().getValue()));

        final String mimeType = getMimeTypeOrDefault(request.getPath().getValue());

        buildSuccessResponse(response, mimeType, content);
    }

    @Override
    public void doPost(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("POST 요청은 지원하지 않습니다.");
    }

    private static void buildSuccessResponse(ServletResponse response, String mimeType, String content) {
        response.setStatus(OK);
        response.setHeader(HttpHeaderName.CONTENT_TYPE.getValue(), mimeType + ";charset=utf-8");
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
