import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.AnsiColors;
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

import javax.ws.rs.client.Entity;

import org.apache.spark.api.java.function.Function;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class FeedReaderMain {

  public static void main(String[] args) {
    SubscriptionParser subparser = new SubscriptionParser();
    subparser.parse("config/subscriptions.json");

    SparkSession spark = SparkSession
        .builder()
        .appName("FeedReader")
        .master("local[*]")
        .getOrCreate();
    JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

    JavaRDD<SingleSubscription> subs = sc.parallelize(subparser.getSubscriptions());

    Function<SingleSubscription, Feed> buildFeed = singSub -> Feed.buildFeed(singSub);
    JavaRDD<Feed> feed = subs.map(buildFeed);

    List<Feed> feedList = feed.collect();

    // Print debugg
    System.out.println(AnsiColors.YELLOW + AnsiColors.BLUE_BACKGROUND + feedList.toString() + AnsiColors.RESET);

    JavaRDD<Feed> feeds = sc.parallelize(feedList);

    // QuickHeuristic qh = new QuickHeuristic(); // Hardecode
    // Function<Feed, Map<String, Integer>> buildCountDict = feedprime ->
    // TableOfNamedEntity.buildCountDict(feedprime, qh);
    // JavaRDD<Map<String, Integer>> dicts = feeds.map(buildCountDict);
    //
    // Map<String, Integer> finalDict = dicts.reduce((d1, d2) ->
    // TableOfNamedEntity.mergeCountDicts(d1, d2));

    // hay que imprimir finalDict para terminar
    sc.close();
    spark.stop();
  }
}
