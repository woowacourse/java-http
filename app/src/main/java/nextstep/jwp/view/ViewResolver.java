package nextstep.jwp.view;

import nextstep.jwp.http.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViewResolver {

    public String resolve(View view) {
        return resolve(view.getPath());
    }

    public String resolve(String path) {
        try {
            System.out.println("==== RESOLVE RESOURCE ====");
            System.out.println(path);
            final URL resourceUrl = getClass().getResource("/static/" + path);
            final Path filePath = new File(resourceUrl.getFile()).toPath();
            return String.join("\n", Files.readAllLines(filePath)) + "\n";
        } catch (IOException e) {
            e.printStackTrace();
            return "Hello world!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("적절한 HTTP 헤더 포맷이 아닙니다.");
        }
    }
}
