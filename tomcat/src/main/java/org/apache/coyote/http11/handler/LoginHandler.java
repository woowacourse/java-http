package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatusCode.OK;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatusLine;

public class LoginHandler implements RequestHandler {

    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String uri = httpRequest.getStartLine().getHttpRequestUri().getUri();

        if (uri.contains("?")) {
            Map<String, String> queryParms = parseQueryParms(uri);
            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(queryParms.get("account"));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (user.checkPassword(queryParms.get("password"))) {
                    System.out.println(user);
                }
            }
        }
        return getLoginPage(httpRequest);
    }

    private HttpResponse getLoginPage(final HttpRequest httpRequest) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                httpRequest.getStartLine().getHttpVersion(), OK);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        HttpResponseBody body = HttpResponseBody.from(responseBody);

        return new HttpResponse(statusLine, httpResponseHeader, body);
    }

    public Map<String, String> parseQueryParms(final String requestUri) {
        Map<String, String> queryParms = new HashMap<>();

        int queryParamStartIndex = requestUri.indexOf("?");
        String queryString = requestUri.substring(queryParamStartIndex + 1);

        Arrays.stream(queryString.split("&"))
                .forEach(query -> queryParms.put(query.split("=")[0], query.split("=")[1]));

        return queryParms;
    }
}
