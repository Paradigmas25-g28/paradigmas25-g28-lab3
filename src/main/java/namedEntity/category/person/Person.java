package namedEntity.category.person;

import namedEntity.NamedEntity;

public class Person extends NamedEntity {

    protected String idpersona;
    LastName lastName;
    Name firstName;
    Title title;

    public Person(String name, String topic, int frequency, String idpersona, Name firstName, LastName lastName, Title title) {
        super(name, "Person", topic, frequency);
        this.idpersona = idpersona;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
    }
    
    public String getIdpersona() {
        return idpersona;
    }

    public void setIdpersona(String idpersona) {
        this.idpersona = idpersona;
    }
    
}
