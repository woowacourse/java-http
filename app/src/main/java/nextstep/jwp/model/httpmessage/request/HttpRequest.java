package nextstep.jwp.model.httpmessage.request;

import nextstep.jwp.model.httpmessage.common.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static nextstep.jwp.model.httpmessage.common.ContentType.FORM;
import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_LENGTH;
import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_TYPE;

public class HttpRequest {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final RequestHeader headers;
    private RequestBody requestBody;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line = br.readLine();
        requestLine = new RequestLine(line);

        headers = new RequestHeader();
        while (!StringUtils.isEmpty(line)) {
            line = br.readLine();
            if (!StringUtils.isEmptyOrWhitespace(line)) {
                LOG.debug("Request header : {}", line);
                String[] split = line.split(": ");
                headers.add(split[0].trim(), split[1].trim());
            }
        }

        if (headers.containsKey(CONTENT_LENGTH)) {
            int length = headers.getContentLength();
            LOG.debug("Request content-length : {}", length);

            char[] buffer = readBody(br, length);
            requestBody = new RequestBody(new String(buffer));
        }
    }

    private char[] readBody(BufferedReader br, int length) throws IOException {
        char[] buffer = new char[length];
        br.read(buffer, 0, length);
        return buffer;
    }

    public String getQueryParam(String param) {
        return requestLine.getParameter(param);
    }

    public String getRequestURI() {
        String path = requestLine.getPath();
        return ContentType.of(path)
                .map(type -> path.replace(type.suffix(), ""))
                .orElseGet(requestLine::getPath);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getParameter(String param) {
        if (isFormPost()) {
            return requestBody.getParameter(param);
        }
        throw new IllegalStateException("form 형식이 아닙니다.");
    }

    private boolean isFormPost() {
        String contentType = headers.getContentType();
        return !Objects.isNull(contentType) && contentType.contains(FORM.value());
    }

    public String getHeader(String header) {
        return headers.getHeader(header);
    }

    public String getRequestBody() {
        return Objects.requireNonNull(requestBody.getMessage());
    }

    public int getContentLength() {
        return headers.getContentLength();
    }
}
