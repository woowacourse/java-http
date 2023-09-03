package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

    //    @Override
//    public void process(final Socket connection) {
//        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
//
//            RequestLine requestLine = readRequestLine(reader);
//            RequestHeader requestHeader = readHeader(reader);
//            String requestBody = readRequestBody(reader, requestHeader);
//
//            String response = ResponseGenerator.generate(requestLine, requestHeader, requestBody);
//            writer.write(response);
//            writer.flush();
//        } catch (IOException | UncheckedServletException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
    private static final String DEFAULT_PAGE = "/index.html";
    private static final String DEFAULT = "html";

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            RequestLine requestLine = readRequestLine(reader);
            RequestHeader requestHeader = readHeader(reader);
            String requestBody = readRequestBody(reader, requestHeader);

            Response response = getResponse(requestLine, requestHeader, requestBody);

            // "/index.html"이나 "login.html"인 경우

            writer.write(response.toString());
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Response getResponse(final RequestLine requestLine, final RequestHeader requestHeader, String requestBody) {
        String requestUrl = requestLine.getRequestUrl();
        // "/"인 경우
        if (requestUrl.equals("/") && requestLine.getRequestMethod().equals("GET")) {
            return responseStaticFile(DEFAULT_PAGE, requestHeader);
        }
        if (requestUrl.startsWith("/login")) {
            if (requestLine.getRequestMethod().equals("GET")) {
                return responseStaticFile("/login.html", requestHeader);
            }
            if (requestLine.getRequestMethod().equals("POST")) {
                return login(requestUrl);
            }
        }
        return responseStaticFile(requestUrl, requestHeader);
    }

    private static Response login(final String requestUrl) {
        try {
            int index = requestUrl.indexOf("?");
            String queryString = requestUrl.substring(index + 1);
            String[] splitQueryString = queryString.split("&");
            String account = splitQueryString[0].split("=")[1];
            String password = splitQueryString[1].split("=")[1];
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(() -> new IllegalArgumentException()
            );//todo : 해당하는 계정이 존재하지 않는다
            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException(); //todo :비밀번호가 일치하지 않는다.
            }
            return Response.redirection("/index.html");
        } catch (RuntimeException e) {
            log.error(e.getMessage(),e);
            return Response.redirection("/401.html");
        }
    }

    //todo : 정적파일은 다 있다고 가정? 없는 파일이면?
    /*
    정적파일 요청
    url로 파일 찾음
    - 파일 있으면 : 파일 읽어서 responseBody + http 요청 생성
    - 파일 없으면 : Location 헤더만 돌려줌
     */
    private static Response responseStaticFile(String requestUri, RequestHeader requestHeader) {
        String requestedFile = ClassLoader.getSystemClassLoader().getResource("static" + requestUri).getFile();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(requestedFile, Charset.forName("UTF-8")))) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
            String responseBody = sb.toString();
            return Response.ok(responseBody, requestHeader);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return Response.redirection("404.html");
    }

    private RequestLine readRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return RequestLine.from(line);
    }

    private RequestHeader readHeader(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                break;
            }
            lines.add(line);
        }
        return RequestHeader.from(lines);
    }

    //아직 쓰이지는 않음
    private String readRequestBody(final BufferedReader reader, final RequestHeader requestHeader) throws IOException {
        if (!requestHeader.hasRequestBody()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str + "\r\n");
        }
        return sb.toString();
    }


}
