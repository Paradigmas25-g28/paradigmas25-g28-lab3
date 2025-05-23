package namedEntity.category.otherCategory;

import namedEntity.NamedEntity;

public class OtherCategory extends NamedEntity{
    protected String comentarios;
    
    public OtherCategory(String name, String topic, int frequency, String comentarios) {
        super(name, "Other Category", topic, frequency);
        this.comentarios = comentarios;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
