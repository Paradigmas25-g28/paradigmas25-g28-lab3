package parser;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import subscription.SingleSubscription;
import subscription.Subscription;

import feed.Article;

import httpRequest.httpRequester;


/*
 * Esta clase implementa el parser de feed de tipo reddit (json)
 * pero no es necesario su implemntacion 
 * */

public class RedditParser extends JsonParser {

  private List<Article> articles;

  @Override
  public void parse(String path) {
    this.articles = new ArrayList<>();
    try {
      String oneJSON = Files.readString(new File(path).toPath());
    
      JSONObject json = new JSONObject(oneJSON);
      JSONArray children = json.getJSONObject("data").getJSONArray("children");

      for (int i = 0; i < children.length(); i++) {
        JSONObject postData = children.getJSONObject(i).getJSONObject("data");

        String title = postData.optString("title", "No title");
        String description = postData.optString("selftext", ""); 
        long timestamp = postData.optLong("created_utc", System.currentTimeMillis() / 1000);
        Date pubDate = new Date(timestamp * 1000); 
        String link = postData.optString("url", "");
      

          articles.add(new Article(title, description, pubDate, link));
      }
    } catch (Exception e) {
      System.err.println("Error al parsear json: " + e.getMessage());
      this.articles = null;
    }
  }

  public List<Article> getArticles() {
    return articles;
  }

  public static void main(String[] args) {
    SubscriptionParser subscriptionParser = new SubscriptionParser();
    subscriptionParser.parse("config/subscriptions.json");

    SingleSubscription redditSubscription = null;
    for (int i = 0; i < subscriptionParser.getLength(); i++) {
        SingleSubscription currentSub = subscriptionParser.getSingleSubscription(i);
        if ("reddit".equalsIgnoreCase(currentSub.getUrlType())) {
            redditSubscription = currentSub;
            break; 
        }
    }

    if (redditSubscription == null) {
        System.err.println("No se encontró ninguna suscripción de tipo 'reddit' en config/subscriptions.json");
        return;
    }

    httpRequester requester = new httpRequester();

    String urlFormat = redditSubscription.getUrl();
    String topic = redditSubscription.getUrlParams().get(0); 
    String urlFinal = String.format(urlFormat, topic);
    System.out.println("Usando URL de Reddit: " + urlFinal);

    File jsonFile = requester.getFeedRedditToFile(urlFinal); 

    if (jsonFile == null || !jsonFile.exists()) {
        System.err.println("No se pudo descargar o encontrar el archivo JSON del feed de Reddit.");
        return;
    }

    RedditParser redditParser = new RedditParser();
    redditParser.parse(jsonFile.getPath());

    if (redditParser.getArticles() != null) {
        System.out.println("Artículos parseados desde Reddit: " + redditParser.getArticles().size());
        for (Article xArticle : redditParser.getArticles()) {
            xArticle.prettyPrint();
        }
    } else {
        System.out.println("El parsing de Reddit falló o no se encontraron artículos.");
    }
  }
}
