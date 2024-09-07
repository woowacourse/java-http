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
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, User> sessionStorage = new HashMap<>();

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
            String responseBody = "Hello world!";   // 기본 응답 body
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
            String requestMethod = splitRequestStartLine[0].trim();
            String requestUri = splitRequestStartLine[1].trim();
            String path = requestUri;

            /**
             * path와 queryString 분리
             */
            String inputQueryString = "";
            int queryStringIndex = requestUri.indexOf("?");
            if (queryStringIndex >= 0) {
                path = requestUri.substring(0, queryStringIndex);
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

            /**
             * application/x-www-form-urlencoded 타입의 Request Body 파싱
             */
            Map<String, String> requestBodyFields = new HashMap<>();
            Arrays.stream(requestBody.split("&"))
                    .forEach(requestBodyField -> {
                        String[] split = requestBodyField.split("=");
                        if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                            requestBodyFields.put(split[0], split[1].trim());
                        }
                    });

            /**
             * Cookie 파싱
             */
            Map<String, String> cookies = new HashMap<>();
            String rawCookie = requestHeaders.get("Cookie");
            if (rawCookie != null) {
                String[] cookieEntries = rawCookie.split(";");
                for (String cookieEntry : cookieEntries) {
                    String[] split = cookieEntry.split("=");
                    if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                        cookies.put(split[0].trim(), split[1].trim());
                    }
                }
            }

            /**
             * 로그인 페이지 요청 처리
             */
            if ("GET".equals(requestMethod) && "/login".equals(path)) {
                path = "/login.html";

                /**
                 * JSESSIONID가 쿠키에 존재하는 경우
                 * 즉 이미 로그인을 완료한 상태인 경우, 바로 index.html로 리다이렉트
                 */
                if (cookies.get("JSESSIONID") != null) {
                    User user = sessionStorage.get(cookies.get("JSESSIONID"));
                    if (user != null) {
                        log.info(user.toString());
                        additionalResponseHeaders.add("Location: /index.html");
                        statusCode = "302 Found";
                    }
                }
            }

            /**
             * 회원가입 페이지 요청 처리
             */
            if ("GET".equals(requestMethod) && "/register".equals(path)) {
                path = "/register.html";

                /**
                 * JSESSIONID가 유효한 경우
                 * 즉 이미 로그인을 완료한 상태인 경우, 바로 index.html로 리다이렉트
                 */
                if (cookies.get("JSESSIONID") != null) {
                    User user = sessionStorage.get(cookies.get("JSESSIONID"));
                    if (user != null) {
                        log.info(user.toString());
                        additionalResponseHeaders.add("Location: /index.html");
                        statusCode = "302 Found";
                    }
                }
            }

            /**
             * 로그인 API - application/x-www-form-urlencoded 타입의 Request Body를 통해 회원 조회
             */
            if ("POST".equals(requestMethod) && "/login".equals(path)) {
                String requestBodyInputAccount = requestBodyFields.get("account");
                String requestBodyInputPassword = requestBodyFields.get("password");

                if (requestBodyInputAccount != null && requestBodyInputPassword != null) {
                    Optional<User> optionalUser = InMemoryUserRepository.findByAccount(requestBodyInputAccount);

                    /**
                     * 로그인에 성공한 경우 세션ID 발급 + index.html으로 리다이렉트
                     * 로그인에 실패한 경우 (올바르지 않은 Password) 401 코드 + 401.html 응답
                     */
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        if (user.checkPassword(requestBodyInputPassword)) {
                            String sessionId = UUID.randomUUID().toString();
                            sessionStorage.put(sessionId, user);
                            additionalResponseHeaders.add("Set-Cookie: JSESSIONID=" + sessionId);

                            log.info(user.toString());
                            additionalResponseHeaders.add("Location: /index.html");
                            statusCode = "302 Found";
                        }
                        if (!user.checkPassword(requestBodyInputPassword)) {
                            statusCode = "401 Unauthorized";
                            path = "/401.html";
                        }
                    }

                    /**
                     * 로그인에 실패한 경우 (존재하지 않는 ID) 401 코드 + 401.html 응답
                     */
                    if (optionalUser.isEmpty()) {
                        statusCode = "401 Unauthorized";
                        path = "/401.html";
                    }
                }
            }

            /**
             * 회원가입 API - application/x-www-form-urlencoded 타입의 Request Body를 통해 회원 등록
             */
            if ("POST".equals(requestMethod) && "/register".equals(path)) {
                String bodyInputAccount = requestBodyFields.get("account");
                String bodyInputPassword = requestBodyFields.get("password");
                String bodyInputEmail = requestBodyFields.get("email");

                if (bodyInputAccount != null && bodyInputPassword != null && bodyInputEmail != null) {
                    Optional<User> optionalUser = InMemoryUserRepository.findByAccount(bodyInputAccount);

                    /**
                     * 회원가입에 성공한 경우
                     * 세션 ID 발급 및 세션 저장소에 저장 + 쿠키로 세션Id 응답 + index.html로 리다이렉트
                     */
                    if (optionalUser.isEmpty()) {
                        User user = new User(bodyInputAccount, bodyInputPassword, bodyInputEmail);
                        InMemoryUserRepository.save(user);

                        String sessionId = UUID.randomUUID().toString();
                        sessionStorage.put(sessionId, user);
                        additionalResponseHeaders.add("Set-Cookie: JSESSIONID=" + sessionId);

                        log.info(user.toString());
                        additionalResponseHeaders.add("Location: /index.html");
                        statusCode = "302 Found";
                    }

                    /**
                     * 회원가입에 실패한 경우 (이미 같은 아이디를 갖는 회원이 존재하는 경우) 400 코드 + 재시도할 수 있도록 회원가입 페이지 응답
                     */
                    if (optionalUser.isPresent()) {
                        statusCode = "400 Bad Request";
                        path = "/register.html";
                    }
                }
            }

            /**
             * Request URI에 해당하는 파일 찾기
             * 요청받은 파일이 존재하는 경우에만 파일을 읽어 응답.
             * 만약 파일이 존재하지 않거나, 디렉토리인 경우 기본 메시지 응답
             */
            URL resource = getClass().getClassLoader().getResource("static" + path);
            if (resource != null && !path.endsWith("/")) {
                byte[] fileContents = Files.readAllBytes(new File(resource.getFile()).toPath());
                responseBody = new String(fileContents);
            }

            /**
             * 파일 확장자 추출 + 추출한 파일 확장자로 Content-Type 설정
             */
            String fileExtension = "html";
            int fileExtensionIndex = path.lastIndexOf(".");
            if (fileExtensionIndex >= 0) {
                fileExtension = path.substring(fileExtensionIndex)
                        .replace(".", "");
            }
            String responseContentType = "text/" + fileExtension;

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
