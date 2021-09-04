package nextstep.jwp.framework.http.common;

import java.io.File;
import org.apache.commons.io.FilenameUtils;

public class FileUtils {

    public static String fileExtension(final File file) {
        return FilenameUtils.getExtension(file.getName());
    }
}
