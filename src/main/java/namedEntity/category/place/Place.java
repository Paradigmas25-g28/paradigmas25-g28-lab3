package namedEntity.category.place;

import namedEntity.NamedEntity;

public class Place extends NamedEntity {

  protected Pais pais;
  protected Ciudad ciudad;
  protected Direction direction;
  protected OtherPlace other;

  public Place(String name, String topic, int frequency, Pais pais, Ciudad ciudad, Direction direction, OtherPlace otro) {
    super(name, "Place", topic, frequency);
    this.pais = pais;
    this.ciudad = ciudad;
    this.direction = direction;
    this.other = otro;
  }

  public Pais getPais() {
    return pais;
  }

  public void setPais(Pais pais) {
    this.pais = pais;
  }

  public Ciudad getCiudad() {
    return ciudad;
  }

  public void setCiudad(Ciudad ciudad) {
    this.ciudad = ciudad;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDireccion(Direction direction) {
    this.direction = direction;
  }

  public OtherPlace getOther() {
    return other;
  }

  public void setOher(OtherPlace other) {
    this.other = other;
  }
}
