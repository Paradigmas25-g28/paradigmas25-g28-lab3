package namedEntity.category.date;

import namedEntity.NamedEntity;

public class Date extends NamedEntity{
    
    protected String formacanonica;
    protected String precisa;

    
    public Date(String name, String topic, int frequency, String formacanonica, String precisa) {
        super(name, "Date", topic, frequency);
        this.formacanonica = formacanonica;
        this.precisa = precisa;
    }
    public String getFormacanonica() {
        return formacanonica;
    }
    public void setFormacanonica(String formacanonica) {
        this.formacanonica = formacanonica;
    }
    public String getPrecisa() {
        return precisa;
    }
    public void setPrecisa(String precisa) {
        this.precisa = precisa;
    }
    
}
