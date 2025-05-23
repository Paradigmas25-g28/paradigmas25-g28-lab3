package namedEntity.category.person;


public class LastName {
    protected String formacanonica;
    protected String origen;
    public LastName(String formacanonica, String origen) {
        this.formacanonica = formacanonica;
        this.origen = origen;
    }
    public String getFormacanonica() {
        return formacanonica;
    }
    public void setFormacanonica(String formacanonica) {
        this.formacanonica = formacanonica;
    }
    public String getOrigen() {
        return origen;
    }
    public void setOrigen(String origen) {
        this.origen = origen;
    }
    
}
