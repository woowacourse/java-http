package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            Map<String, String> requestHeaders = new HashMap<>();
            List<String> additionalResponseHeaders = new ArrayList<>();
            String statusCode = "200 OK";

            /**
             * HTTP Request Header 읽기
             */
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String requestStartLine = bufferedReader.readLine();
            String currentLine = bufferedReader.readLine();
            while (currentLine != null && !currentLine.isBlank()) {
                String[] requestHeaderEntry = currentLine.split(":");
                requestHeaders.put(requestHeaderEntry[0], requestHeaderEntry[1].trim());
                currentLine = bufferedReader.readLine();
            }

            /**
             * HTTP Request Body 읽기
             */
            String requestBody = "";
            String rawContentLength = requestHeaders.get("Content-Length");
            if (rawContentLength != null) {
                int contentLength = Integer.parseInt(rawContentLength);
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            /**
             * Request HTTP Method, URI 추출
             */
            String[] splitRequestStartLine = requestStartLine.split(" ");
            String requestMethod = splitRequestStartLine[0];
            String requestUri = splitRequestStartLine[1];
            String filePath = requestUri;

            /**
             * path와 queryString 분리
             */
            String inputQueryString = "";
            int queryStringIndex = requestUri.indexOf("?");
            if (queryStringIndex >= 0) {
                filePath = requestUri.substring(0, queryStringIndex);
                inputQueryString = requestUri.substring(queryStringIndex)
                        .replace("?", "");
            }

            /**
             * queryString 파싱
             */
            Map<String, String> queryParameters = new HashMap<>();
            Arrays.stream(inputQueryString.split("&"))
                    .forEach(queryParameterEntry -> {
                        String[] split = queryParameterEntry.split("=");
                        if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                            queryParameters.put(split[0], split[1]);
                        }
                    });

//            /**
//             * queryString을 통해 회원 조회
//             */
//            String queryStringInputAccount = queryParameters.get("account");
//            String queryStringInputPassword = queryParameters.get("password");
//
//            if (queryStringInputAccount != null && queryStringInputPassword != null) {
//                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(queryStringInputAccount);
//                if (optionalUser.isEmpty()) {
//                    statusCode = "401 Unauthorized";
//                    filePath = "/401.html";
//                }
//                if (optionalUser.isPresent()) {
//                    User user = optionalUser.get();
//                    if (user.checkPassword(queryStringInputPassword)) {
//                        log.info(user.toString());
//                        additionalResponseHeaders.add("Location: /index.html");
//                        statusCode = "302 Found";
//                    }
//                }
//            }

            /**
             * application/x-www-form-urlencoded 타입의 Request Body 파싱
             */
            Map<String, String> requestBodyFields = new HashMap<>();
            Arrays.stream(requestBody.split("&"))
                    .forEach(requestBodyField -> {
                        String[] split = requestBodyField.split("=");
                        if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                            requestBodyFields.put(split[0], split[1]);
                        }
                    });

            /**
             * 로그인 - application/x-www-form-urlencoded 타입의 Request Body를 통해 회원 조회
             */
            if (filePath.equals("/login")) {
                String requestBodyInputAccount = requestBodyFields.get("account");
                String requestBodyInputPassword = requestBodyFields.get("password");

                if (requestBodyInputAccount != null && requestBodyInputPassword != null) {
                    Optional<User> optionalUser = InMemoryUserRepository.findByAccount(requestBodyInputAccount);
                    if (optionalUser.isEmpty()) {
                        statusCode = "401 Unauthorized";
                        filePath = "/401.html";
                    }
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        if (user.checkPassword(requestBodyInputPassword)) {
                            log.info(user.toString());
                            additionalResponseHeaders.add("Location: /index.html");
                            statusCode = "302 Found";
                        }
                    }
                }
            }

            /**
             * 회원가입 - application/x-www-form-urlencoded 타입의 Request Body를 통해 회원 등록
             */
            if (filePath.equals("/register")) {
                String bodyInputAccount = requestBodyFields.get("account");
                String bodyInputPassword = requestBodyFields.get("password");
                String bodyInputEmail = requestBodyFields.get("email");

                if (bodyInputAccount != null && bodyInputPassword != null && bodyInputEmail != null) {
                    Optional<User> optionalUser = InMemoryUserRepository.findByAccount(bodyInputAccount);
                    if (optionalUser.isEmpty()) {
                        User user = new User(bodyInputAccount, bodyInputPassword, bodyInputEmail);
                        InMemoryUserRepository.save(user);

                        log.info(user.toString());
                        additionalResponseHeaders.add("Location: /index.html");
                        statusCode = "302 Found";
                    }
                    if (optionalUser.isPresent()) {
                        statusCode = "400 Bad Request";
                    }
                }
            }

            /**
             * 파일 확장자 추출
             */
            String fileExtension = "html";
            int fileExtensionIndex = filePath.indexOf(".");
            if (fileExtensionIndex < 0) {
                filePath = filePath + "." + fileExtension;
            }
            if (fileExtensionIndex >= 0) {
                fileExtension = filePath.substring(fileExtensionIndex)
                        .replace(".", "");
            }
            String responseContentType = "text/" + fileExtension;

            /**
             * Request URI에 해당하는 파일 찾기
             */
            URL resource = getClass().getClassLoader().getResource("static" + filePath);
            String responseBody = "Hello world!";

            /**
             * 요청받은 파일이 존재하는 경우에만 파일을 읽어 응답.
             * 만약 파일이 존재하지 않거나, 디렉토리인 경우 기본 메시지 응답
             */
            if (resource != null && !filePath.endsWith("/")) {
                byte[] fileContents = Files.readAllBytes(new File(resource.getFile()).toPath());
                responseBody = new String(fileContents);
            }

            /**
             * 응답 헤더 구성
             */
            String responseHeader = String.join("\r\n",
                    "HTTP/1.1 " + statusCode + " ",
                    "Content-Type: " + responseContentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " "
            );
            for (String additionalHeader : additionalResponseHeaders) {
                responseHeader = String.join("\r\n", responseHeader, additionalHeader + " ");
            }

            /**
             * Header 및 Body를 통해 HTTP Response Message 구성
             */
            String response = String.join("\r\n",
                    responseHeader,
                    "",
                    responseBody
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
