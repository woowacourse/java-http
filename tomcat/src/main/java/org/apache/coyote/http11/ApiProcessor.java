package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ResourceFileLoader;

public class ApiProcessor {

    private static final Logger log = LoggerFactory.getLogger(ApiProcessor.class);

    public void process(Socket connection, String requestPath) throws IOException {
        final var outputStream = connection.getOutputStream();

        if (requestPath.startsWith("/login")) {
            String[] splitPath = requestPath.split("\\?");
            String requestUri = splitPath[0];
            if (requestUri.equals("/login")) {
                String contentType = "text/html";
                String responseBody = ResourceFileLoader.loadFileToString("static/login.html");
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
            }

            //쿼리처리
            String[] queryParameters = splitPath[1].split("&");
            Map<String, String> parameterMap = new HashMap<>();
            for (int i = 0; i < queryParameters.length; i++) {
                String[] queryParameterEntry = queryParameters[i].split("=");
                parameterMap.put(queryParameterEntry[0], queryParameterEntry[1]);
            }

            // User 찾기
            String account = parameterMap.get("account");
            String password = parameterMap.get("password");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 User입니다."));
            if(user.checkPassword(password)){
                log.info(user.toString());
            }

        }
        if (requestPath.equals("?")) {

        }
    }
}
