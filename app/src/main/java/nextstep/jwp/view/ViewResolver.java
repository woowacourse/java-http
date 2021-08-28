package nextstep.jwp.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViewResolver {

    public View resolve(String viewName) {
        try {
            System.out.println("==== RESOLVE RESOURCE ====");
            System.out.println(viewName);
            final URL resourceUrl = getClass().getResource("/static/" + viewName);
            final Path filePath = new File(resourceUrl.getFile()).toPath();
            return View.of(String.join("\n", Files.readAllLines(filePath)) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return View.of("Hello world!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("view not found");
        }
    }
}
