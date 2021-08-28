package nextstep.jwp.view;

import java.io.IOException;
import java.util.List;

public interface ViewResolver {

    boolean isExist(String fileName);

    boolean isSuitable(List<String> acceptTypes);

    View getView(String fileName) throws IOException;
}