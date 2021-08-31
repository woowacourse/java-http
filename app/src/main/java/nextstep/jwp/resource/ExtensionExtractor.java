package nextstep.jwp.resource;

import java.util.Arrays;
import java.util.List;

public class ExtensionExtractor {

    private static final String DOT_PATTERN = "\\.";
    private static final String BLANK = "";
    private static final int EXIST_EXTENSION_SIZE = 2;

    public static String extract(String url) {
        List<String> list = Arrays.asList(url.split(DOT_PATTERN));
        if (list.size() < EXIST_EXTENSION_SIZE) {
            return BLANK;
        }

        return list.get(list.size() - 1);
    }
}
