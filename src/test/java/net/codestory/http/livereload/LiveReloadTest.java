package net.codestory.http.livereload;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.codestory.http.WebServer;
import net.codestory.http.misc.Env;
import net.codestory.simplelenium.SeleniumTest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LiveReloadTest extends SeleniumTest {
  WebServer webServer;

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void startServer() {
    webServer = new WebServer() {
      @Override
      protected Env createEnv() {
        return Env.dev(temp.getRoot());
      }
    }.startOnRandomPort();
  }

  @Override
  protected String getDefaultBaseUrl() {
    return "http://localhost:" + webServer.port();
  }

  @Test
  public void change_file_and_refresh() throws IOException {
    File app = temp.newFolder("app");
    File index = new File(app, "changing.html");

    write(index, "---\nlayout: default\n---\n\n<h1>Hello</h1>");
    goTo("/changing.html");
    find("h1").should().contain("Hello");

    write(index, "---\nlayout: default\n---\n\n<h1>Changed</h1>");
    goTo("/changing.html");
    find("h1").should().contain("Changed");
  }

  @Test
  @Ignore("Phantomjs doesn't support websockets")
  public void change_file_and_auto_refresh() throws IOException {
    File app = temp.newFolder("app");
    File index = new File(app, "changing.html");

    write(index, "---\nlayout: default\n---\n\n<h1>Hello</h1>");
    goTo("/changing.html");

    write(index, "---\nlayout: default\n---\n\n<h1>Changed</h1>");
    find("h1").should().contain("Changed");
  }

  static void write(File file, String content) throws IOException {
    Files.write(file.toPath(), content.getBytes(UTF_8));
  }
}