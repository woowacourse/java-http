package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.util.HeaderLine;

public interface Controller {
    String process(HeaderLine headerLine) throws IOException;
}
