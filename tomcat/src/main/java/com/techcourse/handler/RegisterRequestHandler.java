package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpMethod;
import com.techcourse.http.common.HttpStatus;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.common.Location;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.model.User;
import com.techcourse.util.FileUtil;
import java.util.Map;

public class RegisterRequestHandler {

    private final HttpVersion httpVersion;

    public RegisterRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpResponse handleRegisterRequest(final HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();

        if (httpMethod == HttpMethod.GET) { // 회원 가입 페이지를 보여줄 때
            String fileName = FileUtil.createFileName(httpRequest.getFilePath());
            String responseBody = FileUtil.readResource(fileName);
            return new HttpResponse(httpVersion, HttpStatus.OK, Location.empty(), ContentType.TEXT_HTML, responseBody);
        }
        if (httpMethod == HttpMethod.POST) { // 회원 가입 버튼을 누를 때
            Map<String, String> requestParams = httpRequest.getRequestParams();
            registerUser(requestParams);

            return new HttpResponse(httpVersion, HttpStatus.FOUND, new Location("/index.html"),
                    ContentType.APPLICATION_JSON, "");
        }
        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }

    private void registerUser(Map<String, String> requestParams) {
        String account = requestParams.get("account");
        String password = requestParams.get("password");
        String email = requestParams.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
