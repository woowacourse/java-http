package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.handler.AbstractController;
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
    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String account = httpRequest.getParam(ACCOUNT);
        String email = httpRequest.getParam(EMAIL);
        String password = httpRequest.getParam(PASSWORD);

        User user = new User(account, password, email);
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
