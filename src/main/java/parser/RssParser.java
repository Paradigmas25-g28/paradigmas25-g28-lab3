package parser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import subscription.SingleSubscription;
import subscription.Subscription;

import feed.Article;

import httpRequest.httpRequester;
/* Esta clase implementa el parser de feed de tipo rss (xml)
 * https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm 
 * */

public class RssParser extends GeneralParser {

  private List<Article> articles;

  @Override
  public void parse(String path) {
    try {
      File xmlFile = new File(path);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document doc = builder.parse(xmlFile);
      doc.getDocumentElement().normalize();

      NodeList items = doc.getElementsByTagName("item");

      this.articles = new ArrayList<>();

      for (int i = 0; i < items.getLength(); i++) {
        Node node = items.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element item = (Element) node;

          String title = getText(item, "title");
          String description = getText(item, "description");
          Date pubDate = parseDate(getText(item, "pubDate"));
          String link = getText(item, "link");

          articles.add(new Article(title, description, pubDate, link));
        }
      }
    } catch (Exception e) {
      System.err.println("Error al parsear RSS: " + e.getMessage());
      this.articles = null;
    }
  }

  private String getText(Element element, String tagName) {
    NodeList list = element.getElementsByTagName(tagName);
    return (list.getLength() > 0) ? list.item(0).getTextContent() : "";
  }

  private Date parseDate(String dateStr) {
    try {
      SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      return format.parse(dateStr);
    } catch (Exception e) {
      return new Date();
    }
  }

  public List<Article> getArticles() {
    return articles;
  }

  // public static void main(String[] args) {
  // SubscriptionParser parser = new SubscriptionParser();
  // parser.parse("config/subscriptions.json");
  //
  // // httpRequester requester = new httpRequester();
  // // String urlFormat, urlFinal;
  // //
  // // int i = 0;
  // // urlFormat = parser.getSingleSubscription(i).getUrl();
  // // for (String topic : parser.getSingleSubscription(i).getUlrParams()) {
  // // urlFinal = String.format(urlFormat, topic);
  // // System.out.println(requester.getFeedRssToString(urlFinal));
  // // }
  //
  // httpRequester requester = new httpRequester();
  // String urlFormat, urlFinal;
  //
  // int i = 0;
  // urlFormat = parser.getSingleSubscription(i).getUrl();
  // String topic = parser.getSingleSubscription(i).getUlrParams(i);
  // urlFinal = String.format(urlFormat, topic);
  // System.out.println(requester.getFeedRssToString(urlFinal));
  //
  // }

  // INFO: Funcion para testear RssParser
  public static void main(String[] args) {
    SubscriptionParser parser = new SubscriptionParser();
    parser.parse("config/subscriptions.json");

    httpRequester requester = new httpRequester();

    String urlFormat = parser.getSingleSubscription(0).getUrl();
    String topic = parser.getSingleSubscription(0).getUrlParams(0);
    String urlFinal = String.format(urlFormat, topic);
    System.out.println(urlFinal);

    System.out.println(requester.getFeedRssToFile(urlFinal));

    File file = requester.getFeedRssToFile(urlFinal);

    RssParser rssparser = new RssParser();
    rssparser.parse(file.getPath());

    // Imprimo lo que me devuelve RssParser

    for (Article xArticle : rssparser.getArticles())
      xArticle.prettyPrint();
  }
}
