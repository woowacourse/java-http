package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticFileController extends AbstractController {


  @Override
  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    return responseStaticFile(request, request.getUrl());
  }
}
