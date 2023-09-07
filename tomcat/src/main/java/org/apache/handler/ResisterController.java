package org.apache.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.response.ContentType;
import org.apache.common.FileReader;
import org.apache.response.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResisterController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(ResisterController.class);
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String query = httpRequest.getBody();
        String[] queries = query.split("&");
        String account = queries[0].split("=")[1];
        String email = queries[1].split("=")[1];
        String password = queries[2].split("=")[1];

        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
        LOG.info("회원가입 성공한 회원 : {}", user);
        String content = FileReader.read(INDEX_PAGE);

        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(content);
        httpResponse.setLocation(INDEX_PAGE);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String content = FileReader.read(REGISTER_PAGE);
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(content);
    }
}
