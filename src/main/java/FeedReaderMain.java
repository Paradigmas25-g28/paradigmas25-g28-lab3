import java.io.File;
import java.util.ArrayList;
import java.util.List;

import feed.Article;
import feed.Feed;
import httpRequest.httpRequester;
import namedEntity.TableOfNamedEntity;
import namedEntity.heuristic.QuickHeuristic;
import namedEntity.heuristic.RandomHeuristic;
import parser.RedditParser;
import parser.RssParser;
import parser.SubscriptionParser;
import subscription.SingleSubscription;
import subscription.Subscription;

public class FeedReaderMain {

  private static void printHelp() {
    System.out.println("Please, call this program in correct way: FeedReader [-ne]");
  }

  public static void main(String[] args) {
    System.out.println("************* FeedReader version 1.0 *************");

    if (args.length == 0) {

      List<Feed> feedl = feedsFromSubscriptions("config/subscriptions.json");

      for (Feed xfeed : feedl) {
        xfeed.prettyPrint();
      }


    } else if (args.length == 1) {

      List <Feed> feedl = feedsFromSubscriptions("config/subscriptions.json");

      // INFO: Llamar a la heuristica para que compute las entidades nombradas de cada
      // articulos del feed
      QuickHeuristic qh = new QuickHeuristic();
      for (Feed feed : feedl) {

        TableOfNamedEntity tablita = new TableOfNamedEntity();
        tablita.TheRealFunction(feed, qh);

        // INFO: LLamar al prettyPrint de la tabla de entidades nombradas del feed.

        tablita.prettyPrint();
      }

    } else if (args.length == 2) {
      
      List<Feed> feedl = feedsFromSubscriptions("config/subscriptions.json");

      // INFO: Llamar a la heuristica para que compute las entidades nombradas de cada
      // articulos del feed
      if (args[1].equals("-qh")) {
        QuickHeuristic qh = new QuickHeuristic();
        for (Feed feed : feedl) {

          TableOfNamedEntity tablita = new TableOfNamedEntity();
          tablita.TheRealFunction(feed, qh);

          // INFO: LLamar al prettyPrint de la tabla de entidades nombradas del feed.

          tablita.prettyPrint();
        }
      } else if (args[1].equals("-rh")) {
        RandomHeuristic rh = new RandomHeuristic();
        for (Feed feed : feedl) {

          TableOfNamedEntity tablita = new TableOfNamedEntity();
          tablita.TheRealFunction(feed, rh);

          // INFO: LLamar al prettyPrint de la tabla de entidades nombradas del feed.

          tablita.prettyPrint();
        }
      } else {
        System.err.println("Segundo argumento no valido");
        return;
      }

    } else {
      printHelp();
    }
  }

  private static List<Feed> feedsFromSubscriptions(String Path) {

    List<Feed> feeds = new ArrayList<>();
    // INFO: Leer el archivo de suscription por defecto;
    SubscriptionParser parser = new SubscriptionParser();
    parser.parse(Path);

    // INFO: Llamar al httpRequester para obtenr el feed del servidor
    httpRequester requester = new httpRequester();

    for (SingleSubscription subscription : parser.getSubscriptions()) {
      // INFO: Llamar al Parser especifico para extrar los datos necesarios por la
      // aplicacion
      String urlType = subscription.getUrlType().toLowerCase();

      for (String topic : subscription.getUrlParams()) {
        try {

          if (urlType.equals("rss")) {
            String urlFormat = subscription.getUrl();
            String finalUrl = String.format(urlFormat, topic);

            System.out.println("Descargando RSS desde: " + finalUrl);
            File rssXml = requester.getFeedRssToFile(finalUrl);

            RssParser rssParser = new RssParser();
            rssParser.parse(rssXml.getPath());

            String siteName = subscription.getUrlType().toUpperCase() + " - " + topic;

            // INFO: Llamar al constructor de Feed
            Feed feed = new Feed(siteName);

            for (Article article : rssParser.getArticles()) {
              feed.addArticle(article);
            }

            feeds.add(feed);

          } else if (urlType.equals("reddit")) {
            String urlFormat = subscription.getUrl();
            String finalUrl = String.format(urlFormat, topic);

            System.out.println("Descargando Reddit desde: " + finalUrl);
            File rssXml = requester.getFeedRssToFile(finalUrl);

            RedditParser redditParser = new RedditParser();
            redditParser.parse(rssXml.getPath());

            String siteName = subscription.getUrlType().toUpperCase() + " - " + topic;

            // INFO: Llamar al constructor de Feed
            Feed feed = new Feed(siteName);

            for (Article article : redditParser.getArticles()) {
              feed.addArticle(article);
            }

            feeds.add(feed);
          }
        } catch (Exception e) {
          System.err.println("Error al procesar el feed Reddit para el topic: " + topic);
          e.printStackTrace();
        }
      }
    }     
    return feeds;
  }
}

