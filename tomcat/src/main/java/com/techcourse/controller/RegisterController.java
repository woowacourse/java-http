package com.techcourse.controller;

import java.util.Map;

import org.apache.catalina.http.VersionOfProtocol;
import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.StatusLine;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {
    private static final String REGISTER_PATH = "/register";
    private static final String BAD_REQUEST_PAGE = "/400.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public boolean matchesRequest(HttpRequest request) {
        return REGISTER_PATH.equals(request.getPath());
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.OK),
                request.getFileType(),
                FileReader.loadFileContent(request.getPath() + ".html"));
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        return handleRegistration(request.getVersionOfProtocol(), request.getBody(), request.getFileType());
    }

    private HttpResponse handleRegistration(
            VersionOfProtocol versionOfProtocol,
            Map<String, String> bodyParams,
            String accept
    ) {
        String account = bodyParams.get(ACCOUNT);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new HttpResponse(
                    new StatusLine(versionOfProtocol, HttpStatus.BAD_REQUEST),
                    accept,
                    FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }
        String password = bodyParams.get(PASSWORD);
        String email = bodyParams.get(EMAIL);
        InMemoryUserRepository.save(new User(account, password, email));
        HttpResponse response = new HttpResponse(
                new StatusLine(versionOfProtocol, HttpStatus.FOUND),
                accept,
                FileReader.loadFileContent(INDEX_PAGE));
        response.addLocation(INDEX_PAGE);
        return response;
    }
}
