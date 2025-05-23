package namedEntity.category.place;


public class Ciudad {
    protected String pais;
    protected Integer poblacion;
    protected String capital;

    public Ciudad(String capital, Integer poblacion, String pais) {
        this.capital = capital;
        this.poblacion = poblacion;
        this.pais = pais;
    }

    public String getCapital() {
        return capital;
    }
    public void setCapital(String capital) {
        this.capital = capital;
    }
    public Integer getPoblacion() {
        return poblacion;
    }
    public void setPoblacion(Integer poblacion) {
        this.poblacion = poblacion;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    
}
