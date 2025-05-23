package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/* Esta clase modela los atributos y métodos comunes a todos los distintos tipos de parser existentes en la aplicación */
public abstract class GeneralParser {
    protected String content;

    public GeneralParser() {
        this.content = "";
    }

    protected abstract void parse(String path);

}
