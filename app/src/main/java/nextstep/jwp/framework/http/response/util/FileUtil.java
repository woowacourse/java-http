package nextstep.jwp.framework.http.response.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static nextstep.jwp.framework.http.common.Constants.NEWLINE;

public class FileUtil {

    private FileUtil() {
    }

    public static String read(final File file) {
        try {
            final String path = URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8);
            final List<String> fileLines = Files.readAllLines(Path.of(path));

            final StringBuilder responseBodyBuilder = new StringBuilder();
            for (String fileLine : fileLines) {
                responseBodyBuilder.append(fileLine).append(NEWLINE);
            }
            return responseBodyBuilder.toString();
        } catch (IOException e) {
            throw new IllegalStateException("파일 읽기에 실패하였습니다.");
        }
    }

    public static String getExtension(final File file) {
        return FilenameUtils.getExtension(file.getName());
    }
}
