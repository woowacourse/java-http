package nextstep.jwp.controller;

import static org.apache.coyote.http11.header.HeaderType.CONTENT_TYPE;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

  private static final String INDEX_PAGE = "/index.html";

  @Override
  protected void doGet(final HttpRequest request, final HttpResponse response) {
    if (request.isUrlEndWith("/")) {
      response.setBodyAsString("Hello world!");
      response.setHeader(CONTENT_TYPE, ContentType.HTML.getValue() + "; charset=utf-8");
      return;
    }

    response.setBodyAsStaticFile(INDEX_PAGE);
  }
}
