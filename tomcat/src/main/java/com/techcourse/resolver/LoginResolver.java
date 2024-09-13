package com.techcourse.resolver;

import com.techcourse.Session;
import com.techcourse.SessionManager;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;


@Location("/login")
public class LoginResolver extends HttpRequestResolver {

    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (sessionManager.findSession(request.getCookie()).isEmpty()) {
            System.out.println(request.getCookie());
            String body = new ResourceFinder(request.getLocation()).getStaticResource(response);
            response.setBody(body);
            return;
        }
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Location", "/index.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> payload = request.getPayload();
        if (InMemoryUserRepository.findByAccount(payload.get("account")).isEmpty()) {
            throw new IllegalArgumentException("account already exists");
        }
        Session session = sessionManager.findSession(payload.get("account")).orElseThrow();
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId() + " ");
    }

//    private String getQueryParameterResponseBody(String query) throws URISyntaxException, IOException {
//        String[] split = query.split("\\?");
//        String fileName = split[0] + ".html";
//        String[] keyValueArray = split[1].split("&");
//        Map<String, String> keyValues = new HashMap<>();
//        for (int i = 0; i < keyValueArray.length; i++) {
//            String[] keyValueSplit = keyValueArray[i].split("=");
//            keyValues.put(keyValueSplit[0], keyValueSplit[1]);
//        }
//
//        String statusCode = "200 OK";
//
//        log.info("account={}", keyValues.get("account"));
//        log.info("password={}", keyValues.get("password"));
//
//        if (fileName.equals("/login.html")) {
//            if (keyValues.get("account").equals("gugu") && keyValues.get("password").equals("password")) {
//                statusCode = "302 Found";
//                return String.join("\r\n", "HTTP/1.1 " + statusCode + " ", "Content-Type: " + "text/html" + " ",
//                        "Location: /index.html" + " ");
//            } else {
//                fileName = "/401.html";
//                statusCode = "401 Unauthorized";
//            }
//        }
//
////        if (fileName.equals("/register.html")) {
//////            System.out.println();
////            statusCode = "302 Found";
////            return String.join("\r\n",
////                            "HTTP/1.1 " + statusCode + " ",
////                            "Content-Type: " + "text/html" + " ",
////                            "Location: /index.html" + " ")
////                    .getBytes();
////        }
//
//        String extension = fileName.split("\\.")[1];
//        if (extension.equals("js")) {
//            extension = "javascript";
//        }
//        String responseBody = Files.readString(
//                Path.of(getClass().getClassLoader().getResource("static" + fileName).toURI()));
//        return String.join("\r\n", "HTTP/1.1 " + statusCode + " ", "Content-Type: " + "text/" + extension + " ",
//                "Content-Length: " + responseBody.getBytes().length + " ", "", responseBody);
//    }

}
