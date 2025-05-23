package namedEntity.category.product;

import namedEntity.NamedEntity;

public class Product extends NamedEntity{
    protected  Boolean comercial;
    protected String productor;
    public Product(String name, String topic, int frequency, Boolean comercial, String productor) {
        super(name, "Product", topic, frequency);
        this.comercial = comercial;
        this.productor = productor;
    }
    public Boolean getComercial() {
        return comercial;
    }
    public void setComercial(Boolean comercial) {
        this.comercial = comercial;
    }
    public String getProductor() {
        return productor;
    }
    public void setProductor(String productor) {
        this.productor = productor;
    }

}
