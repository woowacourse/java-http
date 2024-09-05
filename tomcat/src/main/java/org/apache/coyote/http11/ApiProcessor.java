package org.apache.coyote.http11;

import static org.apache.coyote.http11.MethodType.GET;
import static org.apache.coyote.http11.MethodType.POST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiProcessor {

    private static final Logger log = LoggerFactory.getLogger(ApiProcessor.class);

    private final PageProcessor pageProcessor;

    public ApiProcessor() {
        this.pageProcessor = new PageProcessor();
    }

    public void process(Socket connection, String requestPath, MethodType methodType, Map<String, String> requestBody) throws IOException {
        OutputStream outputStream = connection.getOutputStream();

        String[] splitPath = requestPath.split("\\?");
        String requestUri = splitPath[0];

        if (requestUri.equals("/login")) {
            if (methodType == POST) {
                processLogin(outputStream, requestBody);
                return;
            }
            if (methodType == GET) {
                processLoginPage(outputStream);
                return;
            }
        }

        if (requestUri.equals("/register")) {
            if (methodType == POST) {
                processRegisterPost(outputStream, requestBody);
            }
            if (methodType == GET) {
                pageProcessor.process(outputStream, "register");
                return;
            }
        }
    }

    private void processRegisterPost(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        pageProcessor.processWithHttpStatus(outputStream, "index", HttpStatus.CREATED);
    }

    private Map<String, String> findParameterEntries(String requestPath) {
        String[] queryParameters = findQueryParameters(requestPath);
        Map<String, String> parameterMap = new HashMap<>();
        for (String queryParameter : queryParameters) {
            String[] queryParameterEntry = queryParameter.split("=");
            parameterMap.put(queryParameterEntry[0], queryParameterEntry[1]);
        }
        return parameterMap;
    }

    private String[] findQueryParameters(String requestPath) {
        String[] splitPath = requestPath.split("\\?");
        return splitPath[1].split("&");
    }

    private void processLogin(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 User입니다."));

        if (user.checkPassword(password)) {
            loginSuccess(outputStream);
            return;
        }
        loginFail(outputStream);
    }

    private void processLoginPage(OutputStream outputStream) throws IOException {
        pageProcessor.process(outputStream, "login");

    }

    private void loginSuccess(OutputStream outputStream) throws IOException {
        pageProcessor.processWithHttpStatus(outputStream, "index", HttpStatus.FOUND);
    }

    private void loginFail(OutputStream outputStream) throws IOException {
        pageProcessor.processWithHttpStatus(outputStream, "401", HttpStatus.UNAUTHORIZED);
    }
}
