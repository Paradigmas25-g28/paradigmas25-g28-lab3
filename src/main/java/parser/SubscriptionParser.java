package parser;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import subscription.SingleSubscription;
import subscription.Subscription;

public class SubscriptionParser extends JsonParser {

    private Subscription subsList;

    /**
     *
     * @param path ruta del archivo JSON a leer
     */
    @Override
    public void parse(String path) {
        super.parse(path);

        this.subsList = new Subscription(path);

        for (int i = 0; i < this.jsonarray.length(); i++) {
            JSONObject subObjJson = this.jsonarray.getJSONObject(i);

            String url = subObjJson.getString("url");
            String urlType = subObjJson.getString("urlType");
            JSONArray paramsJsonArray = subObjJson.getJSONArray("urlParams");
            List<String> urlParamsList = new ArrayList<>();

            for (int j = 0; j < paramsJsonArray.length(); j++) {
                urlParamsList.add(paramsJsonArray.getString(j));
            }
            SingleSubscription singleSub = new SingleSubscription(url, urlParamsList, urlType);
            this.subsList.addSingleSubscription(singleSub);

        }
    }

    public List<SingleSubscription> getSubscriptions() {
        if (this.subsList == null) {
            System.err.println("SubscriptionParser: El contenedor de suscripciones es null.");
            return new ArrayList<>();
        }
        return this.subsList.getSubscriptionsList();
    }

    /**
     * 
     * @param i
     * @return
     * 
     */
    public SingleSubscription getSingleSubscription(int i) {
        if (this.subsList == null) {
            System.err.println("SubscriptionParser: El contenedor de suscripciones es null. ¿Se llamó a parseFile?");
            return null;
        }
        List<SingleSubscription> subsList = this.subsList.getSubscriptionsList();
        if (i >= 0 && i < subsList.size()) {
            return subsList.get(i);
        } else {
            System.err.println("SubscriptionParser: Índice fuera de rango: " + i);
            return null;
        }
    }

    public int getLength() {
        List<SingleSubscription> subsList = this.subsList.getSubscriptionsList();
        return subsList.size();
    }

    public static void main(String[] args) {
    }
}
