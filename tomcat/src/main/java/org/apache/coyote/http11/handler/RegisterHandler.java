package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.request.HttpRequestMethod.GET;
import static org.apache.coyote.http11.request.HttpRequestMethod.POST;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseHeaderType.LOCATION;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatusLine;

public class RegisterHandler implements RequestHandler {
    
    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        HttpRequestMethod httpMethod = httpRequest.getStartLine().getHttpMethod();

        if (httpMethod == POST) {
            String requestBody = httpRequest.getBody().getBody();
            Map<String, String> accountInfo = parseParms(requestBody);
            User user = new User(accountInfo.get("account"),
                    accountInfo.get("password"),
                    accountInfo.get("email"));

            InMemoryUserRepository.save(user);

            final URL resource = getClass().getClassLoader().getResource("static" + "/index.html");

            File file = new File(resource.getFile());

            var responseBody = new String(Files.readAllBytes(file.toPath()));

            HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                    httpRequest.getStartLine().getHttpVersion(), FOUND);

            HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
            httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
            httpResponseHeader.add(LOCATION, "/index.html");

            HttpResponseBody body = HttpResponseBody.from(responseBody);

            return new HttpResponse(statusLine, httpResponseHeader, body);
        }

        if (httpMethod == GET) {
            final URL resource = getClass().getClassLoader().getResource("static" + "/register.html");

            File file = new File(resource.getFile());

            var responseBody = new String(Files.readAllBytes(file.toPath()));

            HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                    httpRequest.getStartLine().getHttpVersion(), OK);

            HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
            httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

            HttpResponseBody body = HttpResponseBody.from(responseBody);

            return new HttpResponse(statusLine, httpResponseHeader, body);
        }

        return getNotFoundPage(httpRequest);
    }
    private HttpResponse getNotFoundPage(final HttpRequest httpRequest) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                httpRequest.getStartLine().getHttpVersion(), NOT_FOUND);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        HttpResponseBody body = HttpResponseBody.from(responseBody);

        return new HttpResponse(statusLine, httpResponseHeader, body);
    }

    public Map<String, String> parseParms(final String body) {
        Map<String, String> parms = new HashMap<>();

        Arrays.stream(body.split("&"))
                .forEach(value -> parms.put(value.split("=")[0], value.split("=")[1]));

        return parms;
    }
}
