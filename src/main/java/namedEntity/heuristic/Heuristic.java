package namedEntity.heuristic;

import java.util.Map;
import static java.util.Map.entry;

import namedEntity.NamedEntity;
import namedEntity.category.company.Company;
import namedEntity.category.person.LastName;
import namedEntity.category.person.Name;
import namedEntity.category.person.Person;
import namedEntity.category.person.Title;
import namedEntity.category.place.Ciudad;
import namedEntity.category.place.Direction;
import namedEntity.category.place.Pais;
import namedEntity.category.place.Place;

public abstract class Heuristic {
  // public para poder consultar hasmap
  static Name n_musk = (new Name("elon", "hebrew", "ELON"));
  static LastName ln_musk = (new LastName("musk", "english"));
  static Title t_musk = (new Title("Phisics", true));

  static Name n_messi = (new Name("lionel andres", "frances", "goat"));
  static LastName ln_messi = (new LastName("messi", "italiano"));
  static Title t_messi = (new Title("football player", false));

  static Name n_biden = (new Name("joseph robinette", "biblico", "JOE"));
  static LastName ln_biden = (new LastName("biden", "irlandes"));
  static Title t_biden = (new Title("Law", true));

  static Name n_trump = (new Name("donald", "gaelic", "DONALD"));
  static LastName ln_trump = (new LastName("trump", "german"));
  static Title t_trump = (new Title("economist", true));

  static Name n_federer = (new Name("roger", "german", "ROGER"));
  static LastName ln_federer = (new LastName("federer", "swiss"));
  static Title t_federer = (new Title("tennis player", false));

  static Ciudad c_USA = (new Ciudad("Washington DC", 0, "United States of America"));
  static Pais p_USA = (new Pais("english", 0));
  static Direction d_USA = (null);

  static Ciudad c_Russia = (new Ciudad("Moscow", 0, "Russia"));
  static Pais p_Russia = (new Pais("russian", 0));
  static Direction d_Russia = (null);

  public static Map<String, NamedEntity> categoryMap = Map.ofEntries(
      entry("Microsft", new Company("Microsoft", "Other Topic", 0, "microsoft", 0, "tech")),
      entry("Apple", new Company("Apple", "Other Topic", 0, "apple", 0, "tech")),
      entry("Google", new Company("Google", "Other Topic", 0, "google", 0, "tech")),
      entry("Musk", new Person("Musk", "Other Topic", 0, "", n_musk, ln_musk, t_musk)),
      entry("Biden", new Person("Biden", "International Politics", 0, "", n_biden, ln_biden, t_biden)),
      entry("Trump", new Person("Trump", "International Politics", 0, "", n_trump, ln_trump, t_trump)),
      entry("Messi", new Person("Messi", "Football", 0, "", n_messi, ln_messi, t_messi)),
      entry("Federer", new Person("Federer", "Tennis", 0, "", n_federer, ln_federer, t_federer)),
      entry("USA", new Place("USA", "International Politics", 0, p_USA, c_USA, d_USA, null)),
      entry("Russia", new Place("Russia", "International Politics", 0, p_Russia, c_Russia, d_Russia, null))); // public

  public NamedEntity getNamedEntity(String entity) {
    return categoryMap.get(entity);
  }

  public abstract boolean isEntity(String word);

}
