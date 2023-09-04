package org.apache.coyote.detector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;

public class FileDetector {

  private static final ClassLoader classLoader;

  static {
    classLoader = FileDetector.class.getClassLoader();
  }

  private FileDetector() {
  }

  public static String detect(final String resourcePath) throws IOException {
    final URL resource = classLoader.getResource(resourcePath);

    if (resource == null) {
      throw new NoSuchElementException("해당 resource는 존재하지 않습니다.");
    }

    return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
  }
}
