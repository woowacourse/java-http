package nextstep.jwp.framework.context;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;

public interface Controller {

    boolean canHandle(HttpRequest httpRequest);

    HttpResponse handle(HttpRequest httpRequest);

    HttpResponse doGet(HttpRequest httpRequest);

    HttpResponse doPost(HttpRequest httpRequest);

    HttpResponse doPut(HttpRequest httpRequest);

    HttpResponse doDelete(HttpRequest httpRequest);
}
