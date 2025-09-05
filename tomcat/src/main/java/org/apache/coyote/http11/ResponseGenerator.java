package org.apache.coyote.http11;

import com.techcourse.Application;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.constant.RequestLine;
import org.apache.coyote.http11.constant.ResourcePath;
import org.apache.coyote.util.StreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(ResponseGenerator.class);

    private ResponseGenerator() {

    }

    public static HttpResponse generateResponse(HttpRequest httpRequest) {
        final RequestLine requestLine = httpRequest.requestLine();
        final ResourcePath resourcePath = requestLine.resourcePath();
        if (resourcePath.isStaticResource()) {
            final HttpStatus statusCode = HttpStatus.OK;
            final String body = readFile(resourcePath.value());
            return new HttpResponse(statusCode, resourcePath.extractContentType(), body);
        } else if (resourcePath.isQueryString()) {
            final String resourcePathValue = resourcePath.value();
            final int startIndex = resourcePathValue.indexOf("?");
            final String path = resourcePathValue.substring(0, startIndex);
            final String queryString = resourcePathValue.substring(startIndex + 1);
            final Map<String, String> parsedQueryString = parseQueryString(queryString);
            processRequestByQueryString(path, parsedQueryString);
            final HttpStatus statusCode = HttpStatus.OK;
            final ContentType contentType = ContentType.HTML;
            final String body = readFile(path + ".html");
            return new HttpResponse(statusCode, contentType, body);
        } else if (resourcePath.value().equals("/")) {
            final HttpStatus statusCode = HttpStatus.OK;
            final ContentType contentType = ContentType.HTML;
            final String body = readFile("index.html");
            return new HttpResponse(statusCode, contentType, body);
        } else {
            /*
                TODO: 정적파일이 아닌 경우 로직을 실행시키고 응답을 반환한다.
             */
        }
        return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.TEXT, null);
    }

    private static void processRequestByQueryString(String path, Map<String, String> queryStrings) {
        if (path.equals("/login")) {
            final User user = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
            if (!user.checkPassword(queryStrings.get("password"))) {
                throw new IllegalArgumentException("올바르지 않은 패스워드입니다.");
            }
            log.info(String.format("user: %s", user));
        }
    }

    private static Map<String, String> parseQueryString(String queryString) {
        final Map<String, String> parsed = new HashMap<>();
        final String[] fields = queryString.split("&");
        for (String field : fields) {
            final int delimiterIndex = field.indexOf("=");
            final String fieldName = field.substring(0, delimiterIndex);
            final String fieldValue = field.substring(delimiterIndex + 1);
            parsed.put(fieldName, fieldValue);
        }
        return parsed;
    }

    private static String readFile(String path) {
        final String resourcePath = String.format("static/%s", path);
        try (InputStream resourceAsStream = Application.class.getClassLoader().getResourceAsStream(resourcePath)) {
            return StreamReader.readFile(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
