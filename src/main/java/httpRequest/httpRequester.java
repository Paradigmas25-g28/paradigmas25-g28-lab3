package httpRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

/* Esta clase se encarga de realizar efectivamente el pedido de feed al servidor de noticias
 * Leer sobre como hacer una http request en java
 * https://www.baeldung.com/java-http-request
 * */

public class httpRequester {

  public File getFeedRssToFile(String urlFeed) {
    File tempFile = null;
    try {
      URI uri = new URI(urlFeed);
      URL url = uri.toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuilder content = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine).append("\n");
      }
      in.close();
      conn.disconnect();

      // Crear archivo temporal
      tempFile = File.createTempFile("feed_", ".xml");
      tempFile.deleteOnExit();

      // Escribir contenido en archivo
      try (FileWriter writer = new FileWriter(tempFile)) {
        writer.write(content.toString());
      }

    } catch (Exception e) {
      System.err.println("Error al obtener el feed: " + e.getMessage());
    }
    return tempFile;
  }

  public String getFeedRssToString(String urlFeed) {
    try {
      URI uri = new URI(urlFeed);
      URL url = uri.toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuilder content = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine).append("\n");
      }
      in.close();
      conn.disconnect();

      return content.toString();

    } catch (Exception e) {
      System.err.println("Error al obtener el feed: " + e.getMessage());
      return "";
    }
  }

  public File getFeedRedditToFile(String urlFeed) {
    File tempFile = null;
    try {
      URI uri = new URI(urlFeed);
      URL url = uri.toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("User-Agent", "Mozilla/5.0");

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuilder content = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine).append("\n");
      }
      in.close();
      conn.disconnect();

      tempFile = File.createTempFile("feed_reedit_", ".json");
      tempFile.deleteOnExit();

      try (FileWriter writer = new FileWriter(tempFile)) {
        writer.write(content.toString());
      }

    } catch (Exception e) {
      System.err.println("Error al obtener el feed: " + e.getMessage());
    }
    return tempFile;
  }

}
