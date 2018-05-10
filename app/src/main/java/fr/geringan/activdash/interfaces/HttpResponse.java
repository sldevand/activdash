package fr.geringan.activdash.interfaces;

import org.json.JSONException;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public interface HttpResponse {

    void setHttpResponse(String response) throws IllegalAccessException, InterruptedException, ExecutionException, ParseException, JSONException;
}
