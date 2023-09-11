package nextstep.jwp.controller;

import static org.apache.coyote.http11.ParseUtils.parseParam;
import static org.apache.coyote.http11.header.HeaderType.LOCATION;
import static org.apache.coyote.http11.responseline.HttpStatus.FOUND;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

  private static final String INDEX_PAGE = "/index.html";
  private static final String REGISTER_PAGE = "/register.html";

  @Override
  protected void doPost(final HttpRequest request, final HttpResponse response) {
    final Map<String, String> params = parseParam(request.getBody());
    final String account = params.get("account");
    final String password = params.get("password");
    final String email = params.get("email");

    final User user = new User(account, password, email);
    InMemoryUserRepository.save(user);

    response.setStatus(FOUND);
    response.setHeader(LOCATION, INDEX_PAGE);
  }

  @Override
  protected void doGet(final HttpRequest request, final HttpResponse response) {
    response.setBodyAsStaticFile(REGISTER_PAGE);
  }
}
