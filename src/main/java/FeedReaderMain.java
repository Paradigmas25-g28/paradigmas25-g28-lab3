import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap; // <--- AÑADIR ESTE
import java.util.Map;

import utils.AnsiColors;
import feed.Article;
import feed.Feed;
import httpRequest.httpRequester;
import namedEntity.TableOfNamedEntity;
import namedEntity.heuristic.Heuristic;
import namedEntity.heuristic.QuickHeuristic;
import namedEntity.heuristic.RandomHeuristic;
import parser.RedditParser;
import parser.RssParser;
import parser.SubscriptionParser;
import scala.Tuple2;
import subscription.SingleSubscription;
import subscription.Subscription;
import namedEntity.NamedEntity;

import scala.Tuple2; // Para JavaPairRDD y sus elementos

import javax.ws.rs.client.Entity;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class FeedReaderMain {

  private static final String CHARS_TO_REMOVE = ".,;:()'!?\n";

    /**
     * Genera una lista de Feeds a partir de una SingleSubscription.
     * Cada urlParam dentro de la SingleSubscription resultará en un Feed individual.
     * Los parsers y requesters se instancian aquí para seguir la recomendación de Spark.
     */
    private static List<Feed> generateFeedsFromSubscription(SingleSubscription subscription) {
        List<Feed> feedsGenerated = new ArrayList<>();
        httpRequester requester = new httpRequester(); // Instancia local para la tarea
        String urlType = subscription.getUrlType().toLowerCase();

        for (String topic : subscription.getUrlParams()) {
            Feed currentFeed = null; // Feed para este topic específico
            try {
                String urlFormat = subscription.getUrl();
                String finalUrl = String.format(urlFormat, topic); // topic puede ser "" para URLs base

                String siteName = subscription.getUrlType().toUpperCase() + " - " + (topic.isEmpty() ? "General" : topic);
                currentFeed = new Feed(siteName);

                if (urlType.equals("rss")) {
                    File rssXml = requester.getFeedRssToFile(finalUrl);
                    if (rssXml != null && rssXml.exists()) {
                        RssParser rssParser = new RssParser(); // Instancia local
                        rssParser.parse(rssXml.getPath());
                        if (rssParser.getArticles() != null) {
                            for (Article article : rssParser.getArticles()) {
                                currentFeed.addArticle(article);
                            }
                        } else {
                            System.err.println(AnsiColors.RED + "Error: No se pudieron parsear artículos para el feed RSS: " + siteName + " desde " + finalUrl + AnsiColors.RESET);
                            currentFeed = null; // Marcar como inválido si el parseo falla
                        }
                    } else {
                         System.err.println(AnsiColors.RED + "Error: No se pudo descargar o encontrar el archivo del feed RSS: " + siteName + " desde " + finalUrl + AnsiColors.RESET);
                         currentFeed = null; // Marcar como inválido si la descarga falla
                    }
                } else if (urlType.equals("reddit")) {
                    // Aunque el enunciado simplifica a RSS, el JSON de prueba tiene Reddit.
                    // Esta rama lo manejará si está presente.
                    File redditJson = requester.getFeedRedditToFile(finalUrl);
                     if (redditJson != null && redditJson.exists()) {
                        RedditParser redditParser = new RedditParser(); // Instancia local
                        redditParser.parse(redditJson.getPath());
                        if (redditParser.getArticles() != null) {
                            for (Article article : redditParser.getArticles()) {
                                currentFeed.addArticle(article);
                            }
                        } else {
                            System.err.println(AnsiColors.RED + "Error: No se pudieron parsear artículos para el feed Reddit: " + siteName + " desde " + finalUrl + AnsiColors.RESET);
                            currentFeed = null;
                        }
                    } else {
                        System.err.println(AnsiColors.RED + "Error: No se pudo descargar o encontrar el archivo del feed Reddit: " + siteName + " desde " + finalUrl + AnsiColors.RESET);
                        currentFeed = null;
                    }
                } else {
                    System.err.println(AnsiColors.RED + "Tipo de URL no soportado: " + urlType + " para " + finalUrl + AnsiColors.RESET);
                    currentFeed = null;
                }

                // Solo agregar el feed si es válido y contiene artículos procesados
                if (currentFeed != null && currentFeed.getNumberOfArticles() > 0) {
                    feedsGenerated.add(currentFeed);
                } else if (currentFeed != null) { // Feed válido pero sin artículos (o no se pudieron parsear)
                    System.out.println(AnsiColors.YELLOW + "Info: El feed " + siteName + " ("+ finalUrl + ") no contiene artículos o no se pudieron procesar." + AnsiColors.RESET);
                }

            } catch (Exception e) {
                // Captura excepciones más generales durante el procesamiento de un topic
                System.err.println(AnsiColors.RED_BOLD + "Excepción severa procesando topic '" + topic + "' para URL '" + subscription.getUrl() + "': " + e.getMessage() + AnsiColors.RESET);
                // e.printStackTrace(); // Descomentar para debugging detallado
            }
        }
        return feedsGenerated;
    }

    public static void main(String[] args) {
        SubscriptionParser subparser = new SubscriptionParser();
        String subscriptionsFilePath = "config/subscriptions.json"; // Path por defecto

        String heuristicType = "DEFAULT"; // "DEFAULT" significa que no se usa ni -qh ni -rh
    
        if (args.length == 0) {
        // No hay argumentos, se usan los valores por defecto.
        // heuristicType ya es "DEFAULT".
            System.out.println("Modo de ejecución: Por defecto (sin heurística específica).");
        
        } else if (args.length == 1) {
        // Un solo argumento, debe ser la heurística.
        if (args[0].equalsIgnoreCase("-qh")) {
            heuristicType = "QUICK";
        } else if (args[0].equalsIgnoreCase("-rh")) {
            heuristicType = "RANDOM";
        } else {
            // Si no es una heurística válida, lo consideramos un error.
            System.err.println("Argumento no válido: " + args[0]);
            System.err.println("Use -qh para QuickHeuristic o -rh para RandomHeuristic.");
            return; // Termina la ejecución
        }
        
        } else if (args.length == 2) {
        // Dos argumentos: el primero es la ruta, el segundo la heurística.
        subscriptionsFilePath = args[0];
        if (args[1].equalsIgnoreCase("-qh")) {
            heuristicType = "QUICK";
        } else if (args[1].equalsIgnoreCase("-rh")) {
            heuristicType = "RANDOM";
        } else {
            System.err.println("Segundo argumento no válido: " + args[1]);
            System.err.println("Use -qh o -rh.");
            return;
        }
        } else {
        // Demasiados argumentos.
        System.err.println("Error: Demasiados argumentos.");
        System.err.println("Uso: java FeedReaderMain [ruta_a_suscripciones] [-qh | -rh]");
        return;
        }   
    
        System.out.println(AnsiColors.BLUE + "Usando archivo de suscripciones: " + subscriptionsFilePath + AnsiColors.RESET);
        System.out.println(AnsiColors.BLUE + "Modo de detección de entidades: " + heuristicType + AnsiColors.RESET);

        // Hacemos la variable final para poder usarla dentro de la lambda de Spark
        final String finalHeuristicType = heuristicType;
        subparser.parse(subscriptionsFilePath);

        SparkSession spark = SparkSession
            .builder()
            .appName("FeedReaderSpark") // Nombre de la aplicación Spark
            .master("local[*]")      // Ejecutar en modo local usando todos los núcleos disponibles
            .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        // 1. Leer suscripciones y paralelizar
        List<SingleSubscription> subscriptionsList = subparser.getSubscriptions();
        if (subscriptionsList == null || subscriptionsList.isEmpty()) {
            System.err.println(AnsiColors.RED_BOLD + "No se encontraron suscripciones en " + subscriptionsFilePath + " o el parser falló. Terminando." + AnsiColors.RESET);
            sc.close();
            spark.stop();
            return;
        }
        JavaRDD<SingleSubscription> subsRDD = sc.parallelize(subscriptionsList);

        // 2. Generar Feeds (RDD<Feed>)
        //    Un SingleSubscription puede tener múltiples urlParams, cada uno generando un Feed.
        //    Por eso usamos flatMap, ya que generateFeedsFromSubscription devuelve una List<Feed>.
        JavaRDD<Feed> feedsRDD = subsRDD.flatMap(
            subscription -> generateFeedsFromSubscription(subscription).iterator()
        );

        JavaRDD<Article> articlesRDD = feedsRDD.flatMap(
            feed -> feed.getArticleList().iterator()
        );

        // Etapa 1: Extraer SOLO las entidades definidas en Heuristic.categoryMap
        JavaRDD<Tuple2<String, String>> entityCategoryPairs = articlesRDD.flatMap(
            article -> {
                // QuickHeuristic quickHeuristic = new QuickHeuristic(); // YA NO SE USA PARA DETECTAR
                List<Tuple2<String, String>> pairs = new ArrayList<>();
                String textContent = article.getTitle() + " " + article.getText();
                String cleanedText = textContent;
                for (char c : CHARS_TO_REMOVE.toCharArray()) {
                    cleanedText = cleanedText.replace(String.valueOf(c), "");
                }

                for (String word : cleanedText.split("\\s+")) {
                    String trimmedWord = word.trim();
                    if (!trimmedWord.isEmpty()) {
                        // Verificar DIRECTAMENTE si la palabra es una clave en categoryMap
                        NamedEntity neDetails = Heuristic.categoryMap.get(trimmedWord);
                        if (neDetails != null) {
                            // La palabra es una de las entidades propuestas por el laboratorio
                            pairs.add(new Tuple2<>("ENTITY_" + trimmedWord, "OCCURRENCE")); // Contar la entidad
                            if (neDetails.getCategory() != null) {
                                pairs.add(new Tuple2<>("CAT_" + neDetails.getCategory(), "COUNT"));
                            }
                            if (neDetails.getTopic() != null) {
                                pairs.add(new Tuple2<>("TOPIC_" + neDetails.getTopic(), "COUNT"));
                            }
                        }
                        // Si no está en categoryMap, simplemente la ignoramos.
                    }
                }
                return pairs.iterator();
            }
        );

        // Etapa 2: Contar ocurrencias (sin cambios)
        JavaPairRDD<String, Integer> countsRDD = entityCategoryPairs
            .mapToPair(pair -> new Tuple2<>(pair._1, 1))
            .reduceByKey((a, b) -> a + b);

        // Etapa 3: Recolectar y separar los conteos (sin cambios)
        List<Tuple2<String, Integer>> allCountsList = countsRDD.collect();

        Map<String, Integer> entityCounts = new HashMap<>();
        Map<String, Integer> categoryCounts = new HashMap<>();
        Map<String, Integer> topicCounts = new HashMap<>();

        for (Tuple2<String, Integer> tuple : allCountsList) {
            if (tuple._1.startsWith("ENTITY_")) {
                entityCounts.put(tuple._1.substring("ENTITY_".length()), tuple._2);
            } else if (tuple._1.startsWith("CAT_")) {
                categoryCounts.put(tuple._1.substring("CAT_".length()), tuple._2);
            } else if (tuple._1.startsWith("TOPIC_")) {
                topicCounts.put(tuple._1.substring("TOPIC_".length()), tuple._2);
            }
        }

        // Etapa 4: Imprimir (sin cambios, pero ahora los resultados serán diferentes)
        System.out.println(AnsiColors.GREEN_BOLD + "\n--- Conteo Final de Entidades ---" + AnsiColors.RESET);
        if (entityCounts.isEmpty()) {
            System.out.println(AnsiColors.YELLOW + "No se encontraron entidades definidas." + AnsiColors.RESET);
        } else {
            entityCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(AnsiColors.CYAN + entry.getKey() + AnsiColors.RESET + ": " + AnsiColors.YELLOW_BOLD + entry.getValue() + AnsiColors.RESET));
        }

        System.out.println(AnsiColors.GREEN_BOLD + "\n--- Conteo Final por Categoría ---" + AnsiColors.RESET);
        if (categoryCounts.isEmpty()) {
            System.out.println(AnsiColors.YELLOW + "No se encontraron categorías." + AnsiColors.RESET);
        } else {
            categoryCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(AnsiColors.BLUE + entry.getKey() + AnsiColors.RESET + ": " + AnsiColors.YELLOW_BOLD + entry.getValue() + AnsiColors.RESET));
        }

        System.out.println(AnsiColors.GREEN_BOLD + "\n--- Conteo Final por Tema ---" + AnsiColors.RESET);
        if (topicCounts.isEmpty()) {
            System.out.println(AnsiColors.YELLOW + "No se encontraron temas." + AnsiColors.RESET);
        } else {
            topicCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(AnsiColors.PURPLE + entry.getKey() + AnsiColors.RESET + ": " + AnsiColors.YELLOW_BOLD + entry.getValue() + AnsiColors.RESET));
        }

        System.out.println(AnsiColors.GREEN_BOLD + "-----------------------------------------" + AnsiColors.RESET);

        sc.close();
        spark.stop();
    }
}