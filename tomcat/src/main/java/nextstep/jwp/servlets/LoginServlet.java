package nextstep.jwp.servlets;

import static org.apache.coyote.http11.PagePathMapper.*;
import static org.apache.coyote.http11.message.HttpHeaders.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.database.InMemorySessionRepository;
import nextstep.jwp.database.InMemoryUserRepository;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.Cookie;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestBody;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class LoginServlet extends Servlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod httpMethod = httpRequest.getMethod();
        if (httpMethod.isEqualTo(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        }
        if (httpMethod.isEqualTo(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Headers requestHeaders = httpRequest.getHeaders();
        Cookie cookie = requestHeaders.getCookie();

        if (cookie.hasKey("JSESSIONID")) {
            responseForLoggedIn(httpRequest, httpResponse);
            return;
        }
        responseForNotLoggedIn(httpRequest, httpResponse);
    }

    private void responseForLoggedIn(HttpRequest httpRequest, HttpResponse httpResponse) {
        String absolutePath = INDEX_PAGE.path();

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.FOUND)
                .addHeader(LOCATION, absolutePath)
                .setResponseBody(ResponseBody.ofEmpty());
    }

    private void responseForNotLoggedIn(HttpRequest httpRequest,
                                        HttpResponse httpResponse) throws IOException {
        String absolutePath = LOGIN_PAGE.path();
        String content = findResourceWithPath(absolutePath);
        ResponseBody responseBody = new ResponseBody(content);

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .addHeader(CONTENT_TYPE, ContentType.parse(absolutePath))
                .addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length))
                .setResponseBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.getBody();
        Map<String, String> formData = requestBody.getAsFormData();

        String account = formData.get("account");
        String password = formData.get("password");

        if (InMemoryUserRepository.hasSameCredential(account, password)) {
            responseWhenLoginSuccess(httpRequest, httpResponse);
            return;
        }
        responseWhenLoginFail(httpRequest, httpResponse);
    }

    private void responseWhenLoginSuccess(HttpRequest httpRequest, HttpResponse httpResponse) {
        UUID sessionId = saveSession(httpRequest);
        String absolutePath = INDEX_PAGE.path();

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.FOUND)
                .addHeader(LOCATION, absolutePath)
                .addHeader(SET_COOKIE, "JSESSIONID=" + sessionId)
                .setResponseBody(ResponseBody.ofEmpty());
    }

    private void responseWhenLoginFail(HttpRequest httpRequest, HttpResponse httpResponse) {
        String absolutePath = UNAUTHORIZED_PAGE.path();

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.FOUND)
                .addHeader(LOCATION, absolutePath)
                .setResponseBody(ResponseBody.ofEmpty());
    }

    private UUID saveSession(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getBody();
        Map<String, String> formData = requestBody.getAsFormData();

        String account = formData.get("account");
        String password = formData.get("password");

        Long userId = InMemoryUserRepository.getIdByCredentials(account, password);
        return InMemorySessionRepository.save(userId);
    }
}
