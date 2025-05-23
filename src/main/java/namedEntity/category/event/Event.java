package namedEntity.category.event;

import namedEntity.NamedEntity;

public class Event extends NamedEntity{
    
    protected String formacanonica;
    protected String fecha;
    protected Boolean recurrente;
    
    public Event(String name, String topic, int frequency, String formacanonica, String fecha, Boolean recurrente) {
        super(name, "Event", topic, frequency);
        this.formacanonica = formacanonica;
        this.fecha = fecha;
        this.recurrente = recurrente;
    }
    public String getFormacanonica() {
        return formacanonica;
    }
    public void setFormacanonica(String formacanonica) {
        this.formacanonica = formacanonica;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public Boolean getRecurrente() {
        return recurrente;
    }
    public void setRecurrente(Boolean recurrente) {
        this.recurrente = recurrente;
    }
    
}
