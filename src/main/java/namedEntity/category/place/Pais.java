package namedEntity.category.place;


public class Pais {
    protected String lenguaOficial;
    protected Integer poblacion;
    public Pais(String lenguaOficial, Integer poblacion) {
        this.lenguaOficial = lenguaOficial;
        this.poblacion = poblacion;
    }
    public String getLenguaOficial() {
        return lenguaOficial;
    }
    public void setLenguaOficial(String lenguaOficial) {
        this.lenguaOficial = lenguaOficial;
    }
    public Integer getPoblacion() {
        return poblacion;
    }
    public void setPoblacion(Integer poblacion) {
        this.poblacion = poblacion;
    }

}
