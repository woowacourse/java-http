package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.ReasonPhrase;
import org.apache.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=utf-8";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOCATION = "Location";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final Map<String, String> queryParams = parseQueryParam(request.getHttpBody().getValue());

        final String account = queryParams.get("account");
        final String email = queryParams.get("email");
        final String password = queryParams.get("password");

        if (account != null && email != null && password != null) {
            InMemoryUserRepository.save(new User(account, password, email));
            log.info("가입 성공, account = {}, email = {}, password = {}", account, email, password);
        }

        response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                new ReasonPhrase("Found"));

        response.putHeader(LOCATION, "/index.html");

        response.write();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

        FileReader fileReader = new FileReader();
        final String body = fileReader.readFile("static/register.html");

        response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                new ReasonPhrase("OK"));
        response.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
        response.setHttpBody(new HttpBody(body));
        response.write();
    }

    private Map<String, String> parseQueryParam(String queryLine) {
        final String[] params = queryLine.split(PARAM_DELIMITER);

        final Map<String, String> queries = new HashMap<>();
        for (String param : params) {
            final String[] keyToken = param.split(KEY_VALUE_DELIMITER);
            queries.put(URLDecoder.decode(keyToken[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyToken[1], StandardCharsets.UTF_8));
        }
        return queries;
    }
}
