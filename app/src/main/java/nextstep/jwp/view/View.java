package nextstep.jwp.view;

import java.io.IOException;
import java.io.OutputStream;

public interface View {

    void write(OutputStream outputStream) throws IOException;
}
