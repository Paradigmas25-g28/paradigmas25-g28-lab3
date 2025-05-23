package namedEntity.category.person;

public class Title {
  protected String formacanonica;
  protected Boolean profesional;

  public Title(String formacanonica, Boolean profesional) {
    this.formacanonica = formacanonica;
    this.profesional = profesional;
  }

  public String getFormacanonica() {
    return formacanonica;
  }

  public void setFormacanonica(String formacanonica) {
    this.formacanonica = formacanonica;
  }

  public Boolean getProfesional() {
    return profesional;
  }

  public void setProfesional(Boolean profesional) {
    this.profesional = profesional;
  }
}
