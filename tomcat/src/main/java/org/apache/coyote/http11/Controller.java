package org.apache.coyote.http11;

public interface Controller {

    HttpResponse process(String uri);
}
