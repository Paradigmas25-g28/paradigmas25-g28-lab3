package parser;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONTokener;

/* Esta clase modela los atributos y métodos comunes a todos los distintos tipos de parser existentes en la aplicación */
public class JsonParser extends GeneralParser {

    JSONArray jsonarray = null;

    /**
     * Parseo el archivo JSON desde la ruta indicada
     *
     * @param jsonPath Ruta al archivo JSON
     * @throws FileNotFoundException Si no encontre el archivo en la ruta indicada
     */
    @Override
    protected void parse(String path) {
        try {
            FileReader reader = new FileReader(path);
            this.jsonarray = new JSONArray(new JSONTokener(reader));
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
            this.jsonarray = new JSONArray();
        }
    }

    protected JSONArray getJsonArray() {
        return this.jsonarray;
    }
}
