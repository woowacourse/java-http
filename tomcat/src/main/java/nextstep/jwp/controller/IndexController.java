package nextstep.jwp.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

  private static final String INDEX_PAGE = "/index.html";

  @Override
  protected void doGet(final HttpRequest request, final HttpResponse response) {
    if (request.isUrlEndWith("/")) {
      response.setBodyAsString("Hello world!");
      return;
    }

    response.setBodyAsStaticFile(INDEX_PAGE);
  }
}
