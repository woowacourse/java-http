package nextstep;

import common.FileReader;
import java.io.IOException;

public class ViewResolver {

    private ViewResolver() {
    }

    public static View resolve(final String viewName) throws IOException {
        return new View(FileReader.readFile(viewName), SupportContentType.getContentType(viewName));
    }
}
