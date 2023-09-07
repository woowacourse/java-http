package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.HttpUtils.getContentType;
import static org.apache.coyote.http11.HttpUtils.parseParam;
import static org.apache.coyote.http11.HttpUtils.readContentsFromFile;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.headers.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;

public class RegisterController extends AbstractController {

  private static final String HTTP_1_1 = "HTTP/1.1";
  private static final String LOCATION = "Location";
  private static final String INDEX_PAGE = "/index.html";
  private static final String REGISTER_PAGE = "/register.html";

  @Override
  protected HttpResponse doPost(final HttpRequest request) {
    final Map<String, String> params = parseParam(request.getBody());
    final String account = params.get("account");
    final String password = params.get("password");
    final String email = params.get("email");
    final User user = new User(account, password, email);
    InMemoryUserRepository.save(user);

    final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.FOUND);
    final HttpHeader header = new HttpHeader();
    header.setHeader(LOCATION, INDEX_PAGE);
    return new HttpResponse(responseLine, header);
  }

  @Override
  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    final String body = readContentsFromFile(REGISTER_PAGE);
    final String contentType = getContentType(request.getHeader("Accept"));
    final ResponseLine responseLine = new ResponseLine(HTTP_1_1, HttpStatus.OK);
    final HttpHeader header = new HttpHeader();
    header.setHeader("Content-Type", contentType + ";charset=utf-8");
    header.setHeader("Content-Length", body.getBytes().length + "");
    return new HttpResponse(responseLine, header, body);
  }
}
