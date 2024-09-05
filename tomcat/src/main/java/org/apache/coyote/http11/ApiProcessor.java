package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ResourceFileLoader;

public class ApiProcessor {

    private static final Logger log = LoggerFactory.getLogger(ApiProcessor.class);

    private final PageProcessor pageProcessor;

    public ApiProcessor() {
        this.pageProcessor = new PageProcessor();
    }

    public void process(Socket connection, String requestPath) throws IOException {
        OutputStream outputStream = connection.getOutputStream();

        String[] splitPath = requestPath.split("\\?");
        String requestUri = splitPath[0];

        if (requestUri.equals("/login")) {
            if (requestPath.contains("?")) {
                processLoginQuery(outputStream, requestPath);
                return;
            }
            processLoginPage(outputStream);
        }
    }

    private void processLoginQuery(OutputStream outputStream, String requestPath) throws IOException {
        String[] splitPath = requestPath.split("\\?");
        String[] queryParameters = splitPath[1].split("&");
        Map<String, String> parameterMap = new HashMap<>();
        for (String queryParameter : queryParameters) {
            String[] queryParameterEntry = queryParameter.split("=");
            parameterMap.put(queryParameterEntry[0], queryParameterEntry[1]);
        }

        String account = parameterMap.get("account");
        String password = parameterMap.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 User입니다."));

        if (user.checkPassword(password)) {
            loginSuccess(outputStream);
            return;
        }
        loginFail(outputStream);
    }

    private void processLoginPage(OutputStream outputStream) throws IOException {
        pageProcessor.process(outputStream,"login");

    }

    private void loginSuccess(OutputStream outputStream) throws IOException {
        String contentType = "text/html";
        String responseBody = ResourceFileLoader.loadFileToString("static/index.html");

        final var response = String.join("\r\n",
                "HTTP/1.1 302 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void loginFail(OutputStream outputStream) throws IOException {

        String contentType = "text/html";
        String responseBody = ResourceFileLoader.loadFileToString("static/401.html");

        final var response = String.join("\r\n",
                "HTTP/1.1 401 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
