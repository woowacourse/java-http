package org.apache.coyote.http;

import static org.apache.coyote.PageMapper.isCustomFileRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.FileName;
import org.apache.coyote.PageMapper;

public class Body {

    private String value;

    public Body(final String bodyValue) {
        this.value = bodyValue;
    }

//    private String makeResponseBody(String url) {
//        if (isCustomFileRequest(url)) {
//            final Path filePath;
//            try {
//                String fileName = FileName.findFileName(url).getFileName();
//                filePath = new PageMapper().getFilePath(fileName);
//                return readFile(filePath);
//            } catch (URISyntaxException | IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        final Path filePath;
//        try {
//            filePath = new PageMapper().getFilePath(url);
//            return readFile(filePath);
//        } catch (URISyntaxException | IOException e) {
//            e.printStackTrace();
//        }
//
//        return "Hello world!";
//    }

    public String getValue() {
        return value;
    }
}
