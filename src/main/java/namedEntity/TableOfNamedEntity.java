package namedEntity;

import utils.Tripla;
import utils.AnsiColors;

import java.util.List;

import feed.Article;
import feed.Feed;
import httpRequest.httpRequester;
import namedEntity.heuristic.*;
import parser.RssParser;
import parser.SubscriptionParser;
import subscription.SingleSubscription;

import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.jakewharton.fliptables.FlipTable;
import com.jakewharton.fliptables.FlipTableConverters;

public class TableOfNamedEntity {

    List<Tripla> dataTableTopic;
    List<Tripla> dataTableCategory;
    Map<String, Integer> table;

    public TableOfNamedEntity() {
        this.dataTableTopic = new ArrayList<Tripla>();
        this.dataTableCategory = new ArrayList<Tripla>();
        this.table = new HashMap<String, Integer>();
    }

    private static List<String> ListKeys = Arrays.asList(
            "NamedEntity",
            "Person",
            "Place",
            "Other Category",
            "Company",
            "Product",
            "Event",
            "Date",
            "Topic",
            "Other Topic",
            "Sports",
            "Football",
            "Basketball",
            "Tennis",
            "Formula1",
            "Other Sport",
            "Culture",
            "Film",
            "Music",
            "Other Culture",
            "Politics",
            "National Politics",
            "International Politics",
            "Other Politics");

    private static List<String> CatKeys = Arrays.asList(
            "Person",
            "Place",
            "Company",
            "Product",
            "Event",
            "Date",
            "Other Category");

    private static List<String> TopKeys = Arrays.asList(
            "Sports",
            "Football",
            "Basketball",
            "Tennis",
            "Formula1",
            "Other Sport",
            "Culture",
            "Film",
            "Music",
            "Other Culture",
            "Politics",
            "National Politics",
            "International Politics",
            "Other Politics",
            "Other Topic");

    public void InitialiceTable() {
        for (String elem : ListKeys) {
            table.put(elem, 0);
        }
    }

    public void TheRealFunction(Feed feed, Heuristic hh) {

        for (Article article : feed.getArticleList()) {

            article.computeNamedEntities(hh);

            List<NamedEntity> listEntities = article.getListArticles();

            InitialiceTable();

            table.put("NamedEntity", listEntities.size());

            for (NamedEntity ne : listEntities) {
                Integer frequency = ne.getFrequency();
                NamedEntity nepapa = hh.getNamedEntity(ne.getName());
                if (nepapa != null) {

                    String category = nepapa.getCategory();
                    String topic = nepapa.getTopic();
                    // Integer frequency = nepapa.getFrequency();
                    String name = nepapa.getName();

                    table.put(category, table.get(category) + frequency);
                    table.put(topic, table.get(topic) + frequency);
                        
                    Tripla triplaNeT = new Tripla(name, topic, frequency.toString());
                    Tripla triplaNeC = new Tripla(name, category, frequency.toString());

                    dataTableCategory.add(triplaNeC);
                    dataTableTopic.add(triplaNeT);

                    // System.err.println("NAME -> " + name);
                    // System.err.println("FREQ -> " + frequency);
                    // System.err.println(category);
                    // System.err.println("Tabla[" + category + "] : "
                    //         + table.get(category).toString());
                    // System.err.println(topic);
                    // System.err.println("Tabla[" + topic + "] : " + table.get(topic).toString());
                    // System.err.println("----------------------------------------------------------------------");
                }
            }
        }
    }


    public void prettyPrint() {

        /* Para cada feed, imprime una tabla con las entidades nombradas de cada articulo, y su frecuencia en el articulo.*/
        String[] t_headers = { "Category", "Name", "Topic", "Frequency" };
        Object[][] t_data = new Object[dataTableCategory.size()][4];

        Integer n = 0;
        for (Tripla trip_t : dataTableTopic) {

            t_data[n][1] = trip_t.getFirst();
            t_data[n][2] = trip_t.getSecond();
            t_data[n][3] = trip_t.getThird();
            n++;
        }
        Integer m = 0;
        for (Tripla trip_c : dataTableCategory) {
            t_data[m][0] = trip_c.getSecond();
            m++;
        }

        System.out.println(FlipTableConverters.fromObjects(t_headers, t_data));

        /* Para cada feed, imprime una tabla con la frecuencia de cada category y de cada topic.*/

        // Topic
        String[] top_h = {"Topic", "Frequency"};
        String[][] top_d = new String[TopKeys.size()][2];

        Integer i = 0;
        for (String top : TopKeys) {
            Integer freq_top = 0;
            for (Tripla trip_t : dataTableTopic) {
                if ((trip_t.getSecond()).equals(top)){
                    freq_top = freq_top + (Integer.parseInt(trip_t.getThird()));
                }
            }
            top_d[i][0] = top;
            top_d[i][1] = freq_top.toString();
            i++;
        }
        String top_t = FlipTable.of(top_h, top_d);

        // Tabla de categorías
        String[] cat_h = {"Category", "Frequency"};
        String[][] cat_d = new String[CatKeys.size()][2];

        Integer j = 0;
        for (String cat : CatKeys) {
            Integer freq_cat = 0;
            for (Tripla trip_c : dataTableCategory) {
                if ((trip_c.getSecond()).equals(cat)){
                    freq_cat = freq_cat + (Integer.parseInt(trip_c.getThird()));
                }
            }
            cat_d[j][0] = cat;
            cat_d[j][1] = freq_cat.toString();
            j++;
        }
        String cat_t = FlipTable.of(cat_h, cat_d);

        String[] headers = {"Categories", "Topics"};
        String[][] data = {{cat_t, top_t}};

        System.out.println(FlipTableConverters.fromObjects(headers, data));
    }

    public static void main(String[] args) {
        System.out.println("Compilo");

        QuickHeuristic qh = new QuickHeuristic();
        List<Feed> feedl = new ArrayList<Feed>();
        SubscriptionParser parser = new SubscriptionParser();
        parser.parse("config/subscriptions.json");
        httpRequester requester = new httpRequester();

        for (SingleSubscription subscription : parser.getSubscriptions()) {
            if (subscription.getUrlType().equalsIgnoreCase("rss")) {
                String urlFormat = subscription.getUrl();

                for (String topic : subscription.getUrlParams()) {
                    try {
                        String finalUrl = String.format(urlFormat, topic);

                        System.out.println("Descargando RSS desde: " + finalUrl);
                        File rssXml = requester.getFeedRssToFile(finalUrl);

                        RssParser rssParser = new RssParser();
                        rssParser.parse(rssXml.getPath());

                        String siteName = subscription.getUrlType().toUpperCase() + " - " + topic;

                        Feed feed = new Feed(siteName);

                        for (Article article : rssParser.getArticles()) {
                            feed.addArticle(article);
                        }
                        feedl.add(feed);

                    } catch (Exception e) {
                        System.err.println("Error al procesar el feed RSS para el topic: " + topic);
                        e.printStackTrace();
                    }
                }
            }
        }

        List<Feed> feedlpapa = feedl;
        for (Feed feed : feedlpapa) {

            TableOfNamedEntity tablita = new TableOfNamedEntity();
            tablita.TheRealFunction(feed, qh);
            tablita.prettyPrint();

            // System.out.println("Tabla global de ocurrencias de entidades nombradas");
            // tablita.prettyPrint("Global");
            // System.out.println("Tabla de ocurrencias de categorias y topicos");
            // tablita.prettyPrint("Class");
            // System.out.println("Tabla de ocurrencias de categorias y topicos");
        }
    }
}
