package namedEntity.category.company;

import namedEntity.NamedEntity;

public class Company extends NamedEntity {
  protected Integer numeromiembros;
  protected String formacanonica;
  protected String tipoorganizacion;

  public Company(String name, String topic, int frequency, String formacanonica, Integer numeromiembros,
      String tipoorganizacion) {
    super(name, "Company", topic, frequency);
    this.formacanonica = formacanonica;
    this.numeromiembros = numeromiembros;
    this.tipoorganizacion = tipoorganizacion;
  }

  public String getFormacanonica() {
    return formacanonica;
  }

  public void setFormacanonica(String formacanonica) {
    this.formacanonica = formacanonica;
  }

  public Integer getNumeromiembros() {
    return numeromiembros;
  }

  public void setNumeromiembros(Integer numeromiembros) {
    this.numeromiembros = numeromiembros;
  }

  public String getTipoorganizacion() {
    return tipoorganizacion;
  }

  public void setTipoorganizacion(String tipoorganizacion) {
    this.tipoorganizacion = tipoorganizacion;
  }

}
