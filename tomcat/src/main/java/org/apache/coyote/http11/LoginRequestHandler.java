package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public boolean support(final RequestStartLine requestStartLine) {
        return requestStartLine.requestMethod() == RequestMethod.GET && requestStartLine.requestUrl()
                .startsWith("/login");
    }

    @Override
    public String response(final RequestStartLine requestStartLine) {
        Map<String, String> getQueryParameters = getQueryParameters(requestStartLine);

        User user = InMemoryUserRepository.findByAccount(getQueryParameters.get("account")).get();
        if (user.checkPassword(getQueryParameters.get("password"))) {
            log.info("user = {}", user);
        }

        URL resource = getClass().getClassLoader().getResource("static/login.html");
        Path resourcePath = Path.of(resource.getPath());
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes));
    }

    private static Map<String, String> getQueryParameters(final RequestStartLine requestStartLine) {
        String requestUrl = requestStartLine.requestUrl();
        int index = requestUrl.indexOf("?");
        String queryStrings = requestUrl.substring(index + 1);
        Map<String, String> queryStringMap = new HashMap<>();
        for (String queryString : queryStrings.split("&")) {
            String[] strings = queryString.split("=");
            String key = strings[0];
            String value = strings[1];
            queryStringMap.put(key, value);
        }
        return queryStringMap;
    }
}
