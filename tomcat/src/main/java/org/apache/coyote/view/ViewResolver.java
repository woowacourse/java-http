package org.apache.coyote.view;

import com.techcourse.exception.server.InternalServerException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;

public class ViewResolver {
    public static View getView(String viewName) {
        try {
            URL url = Http11Processor.class.getClassLoader().getResource("static/" + viewName);
            if (url == null) {
                throw new InternalServerException("파일을 가져올 수 없습니다. 경로 = " + viewName);
            }
            File file = new File(url.getFile());
            String content = Files.readString(file.toPath());
            return new View(content);
        } catch (IOException e) {
            throw new InternalServerException("파일을 읽는데 실패했습니다.");
        }
    }
}
