package org.apache.coyote;

import java.io.IOException;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

/**
 * 프로토콜 계층(coyote)과 컨테이너 계층(catalina)의 경계.
 * 프로토콜은 요청을 누가 처리하는지 알지 못한다.
 */
public interface Adapter {

    HttpResponse service(HttpRequest request) throws IOException;
}
