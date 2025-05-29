package feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.File;
import java.io.Serializable;

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

/*Esta clase modela la lista de articulos de un determinado feed*/
public class Feed implements Serializable {

  String siteName;
  List<Article> articleList;

  public Feed(String siteName) {
    super();
    this.siteName = siteName;
    this.articleList = new ArrayList<Article>();
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  public List<Article> getArticleList() {
    return articleList;
  }

  public void setArticleList(List<Article> articleList) {
    this.articleList = articleList;
  }

  public void addArticle(Article a) {
    this.getArticleList().add(a);
  }

  public Article getArticle(int i) {
    return this.getArticleList().get(i);
  }

  public int getNumberOfArticles() {
    return this.getArticleList().size();
  }

  @Override
  public String toString() {
    return "Feed [siteName=" + getSiteName() + ", articleList=" + getArticleList() + "]";
  }

  public void prettyPrint() {
    for (Article a : this.getArticleList()) {
      a.prettyPrint();
    }

  }

  public static void main(String[] args) {
    Article a1 = new Article("This Historically Black University Created Its Own Tech Intern Pipeline",
        "A new program at Bowie State connects computing students directly with companies, bypassing an often harsh Silicon Valley vetting process",
        new Date(),
        "https://www.nytimes.com/2023/04/05/technology/bowie-hbcu-tech-intern-pipeline.html");

    Article a2 = new Article("This Historically Black University Created Its Own Tech Intern Pipeline",
        "A new program at Bowie State connects computing students directly with companies, bypassing an often harsh Silicon Valley vetting process",
        new Date(),
        "https://www.nytimes.com/2023/04/05/technology/bowie-hbcu-tech-intern-pipeline.html");

    Article a3 = new Article("This Historically Black University Created Its Own Tech Intern Pipeline",
        "A new program at Bowie State connects computing students directly with companies, bypassing an often harsh Silicon Valley vetting process",
        new Date(),
        "https://www.nytimes.com/2023/04/05/technology/bowie-hbcu-tech-intern-pipeline.html");

    Feed f = new Feed("nytimes");
    f.addArticle(a1);
    f.addArticle(a2);
    f.addArticle(a3);

    f.prettyPrint();

  }

  // Esta funcion es re contra bobina, lo que hace es tomar un SingleSubscription
  // y devuelve un FEED SUPREMO armado con los url de cada SingleSubscription
  public static Feed buildFeed(SingleSubscription subscription) {

    Feed feedSupremo = null;
    httpRequester requester = new httpRequester();

    String urlType = subscription.getUrlType().toLowerCase();

    for (String topic : subscription.getUrlParams()) {
      try {

        String urlFormat = subscription.getUrl();
        String finalUrl = String.format(urlFormat, topic);

        if (urlType.equals("rss")) {
          File rssXml = requester.getFeedRssToFile(finalUrl);
          RssParser rssParser = new RssParser();
          rssParser.parse(rssXml.getPath());

          String siteName = subscription.getUrlType().toUpperCase() + " - " + topic;
          feedSupremo = new Feed(siteName);

          for (Article article : rssParser.getArticles()) {
            feedSupremo.addArticle(article);
          }
        }
        if (urlType.equals("reddit")) {
          File redditJson = requester.getFeedRedditToFile(finalUrl);
          RedditParser redditParser = new RedditParser();
          redditParser.parse(redditJson.getPath());

          String siteName = subscription.getUrlType().toUpperCase() + " - " + topic;
          feedSupremo = new Feed(siteName);

          for (Article article : redditParser.getArticles()) {
            feedSupremo.addArticle(article);
          }
        }
      } catch (Exception e) {
        System.err.println("Error al procesar el feed Reddit para el topic: " + topic);
        e.printStackTrace();
      }
    }
    return feedSupremo;
  }

}
