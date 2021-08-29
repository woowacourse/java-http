package nextstep.jwp.service;

import java.io.IOException;
import nextstep.jwp.util.HeaderLine;

public interface Service {
    String process(HeaderLine headerLine) throws IOException;
}
