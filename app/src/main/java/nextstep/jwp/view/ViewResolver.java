package nextstep.jwp.view;

import java.io.IOException;

public interface ViewResolver {

    boolean isSuitable(String fileName);

    View getView(String fileName) throws IOException;
}