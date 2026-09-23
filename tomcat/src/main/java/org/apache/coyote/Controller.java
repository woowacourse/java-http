package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

//WAS와 애플리케이션 사이의 계약. 이제 Was는 구체로직 몰라도 됨.
public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}