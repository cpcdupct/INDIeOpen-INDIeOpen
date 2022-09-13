package es.upct.cpcd.indieopen.common.response;

import org.json.JSONObject;

public class JSONResponse {

    private JSONObject object;

    private JSONResponse() {
        this.object = new JSONObject();
    }

    public static JSONResponse create() {
        return new JSONResponse();
    }

    public JSONResponse add(String key, Object value) {
        object.put(key, value);
        return this;
    }

    public JSONResponse put(JSONObject object) {
        this.object = object;
        return this;
    }

    public JSONObject toBody() {
        return object;
    }

    public String toBodyString() {
        return object.toString();
    }

}
