package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;

import java.io.IOException;

public interface Controller {
    String process(HttpRequestParser httpRequestParser, HttpResponseBuilder httpResponseBuilder) throws IOException;
}
