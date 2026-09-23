package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queryParameters = getQuerySeparate(request.getRequestBody());
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(queryParameters.get("account"));
        if (foundUser.isPresent()) {
            log.info("회원가입 실패! 아이디 : {}", queryParameters.get("account"));
            getRedirectResponse(request, response, "/register.html", getContentType(request.getPath()));
            return;
        }
        User user = new User(queryParameters.get("account"), queryParameters.get("password"),
                queryParameters.get("email"));
        InMemoryUserRepository.save(user);
        getRedirectResponse(request, response, "/index.html", getContentType(request.getPath()));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String resource = getStaticResource(request.getPath());
        getOkResponse(request, response, resource);
    }

    private Map<String, String> getQuerySeparate(String requestUri) {
        Map<String, String> queryMap = new HashMap<>();
        int index = requestUri.indexOf("?");
        String queryString = requestUri.substring(index + 1);
        String[] queryParameters = queryString.split("&");
        for (String parameter : queryParameters) {
            String[] queryParameter = parameter.split("=", -1);
            queryMap.put(queryParameter[0], queryParameter[1]);
        }
        return queryMap;
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }

    private void getRedirectResponse(HttpRequest httpRequest, HttpResponse httpResponse, String location,
                                     String contentType) {
        httpResponse.setVersion(httpRequest.getVersion());
        httpResponse.setStatusCode(302);
        httpResponse.setReasonPhrase("Found");
        httpResponse.addHeader("Location", location);
        httpResponse.addHeader("Content-Type", contentType);
        httpResponse.addHeader("Content-Length", String.valueOf(contentType.length()));
    }

    @Nullable
    private String getStaticResource(String requestUri) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUri);
        if (url == null) {
            requestUri = requestUri + ".html";
            url = getClass().getClassLoader().getResource("static" + requestUri);
        }
        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private void getOkResponse(HttpRequest httpRequest, HttpResponse httpResponse, String responseBody) {
        httpResponse.setVersion(httpRequest.getVersion());
        httpResponse.setStatusCode(200);
        httpResponse.setReasonPhrase("OK");
        httpResponse.setResponseBody(responseBody);
        httpResponse.addHeader("Content-Type", getContentType(httpRequest.getPath()));
        httpResponse.addHeader("Content-Length", responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
    }
}
