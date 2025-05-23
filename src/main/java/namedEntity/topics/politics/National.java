package namedEntity.topics.politics;

import namedEntity.topics.politics.Politics;

public class National extends Politics {
    protected National(String name, String category, int frecuency){
        super(name, category, "National Politics", frecuency);
    }
}
