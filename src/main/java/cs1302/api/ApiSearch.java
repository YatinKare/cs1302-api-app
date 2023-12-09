package cs1302.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiSearch {
    
    private static String configPath = "resources/config.properties";
    private static String geoApiKey;
    private static String weatherApiKey;
    private static String unsplashApiKey;

    /**
     * Represents an GeoCoding API document.
     */

    static class GeoApiResponse {
        GeoApiResult[] geoResults;
    } // GeoApiResponse

    static class GeoApiResult {
        double lat;
        double lon;
    } // GeoApiResult
    /**
     * Represents a WeatherMap API document.
     */
    static class WeatherApiResponse {
        WeatherApiResult[] weatherResults;
        WeatherApiMain main;
    } // WeatherApiResponse

    static class WeatherApiResult {
        String description;
    } // WeatherApi

    static class WeatherApiMain {
        double temp;
    } //WeatherApiMain

    /**
     * Represents a Unsplash API document.
     */

    static class UnsplashApiResponse {
        UnsplashApiResult[] unsplashResults;
    } // UnsplashApiResponse

    static class UnsplashApiResult {
        UnsplashUrl[] unsplashUrl;
    } // UnsplashApiResult

    static class UnsplashUrl {
        String regular;
    } // UnsplashUrl

    private static HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           
        .followRedirects(HttpClient.Redirect.NORMAL)  
        .build();                                     

    private static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private final static String GEO_API = "http://api.openweathermap.org/geo/1.0/direct";
    private final static String WEATHER_API = "https://api.openweathermap.org/data/2.5/weather";
    private final static String UNSPLASH_API = "https://api.unsplash.com/search/photos";

    public static void main(String[] args) {
        setApiKeys();
        try {
            String city = URLEncoder.encode("atlanta", StandardCharsets.UTF_8);
            String limit = URLEncoder.encode("5", StandardCharsets.UTF_8);
            String query = String.format("?q=%s&limit=%s&appid=&s", city, limit, geoApiKey);
            String geoUri = GEO_API + query;

            String latQuery = URLEncoder.encode("33.75", StandardCharsets.UTF_8);
            String lonQuery =  URLEncoder.encode("-84.39", StandardCharsets.UTF_8); 
            query = String.format("?lat=%s&lon=%s&appid=&s", latQuery, lonQuery, weatherApiKey);
            String weatherUri = WEATHER_API + query;

            String photoCity = URLEncoder.encode("atlanta", StandardCharsets.UTF_8); 
            query = String.format("?query=%s&client_id=%s", photoCity, unsplashApiKey);
            String unsplashUri = UNSPLASH_API + query;
             
            HttpRequest geoRequest = HttpRequest.newBuilder()
                .uri(URI.create(geoUri))
                .build();
            HttpResponse<String> geoResponse = HTTP_CLIENT
                .send(geoRequest, BodyHandlers.ofString());
            // ensure the request is okay
            if (geoResponse.statusCode() != 200) {
                throw new IOException(geoResponse.toString());
            } // if

            HttpRequest weatherRequest = HttpRequest.newBuilder()
                .uri(URI.create(weatherUri))
                .build();
            HttpResponse<String> weatherResponse = HTTP_CLIENT
                .send(weatherRequest, BodyHandlers.ofString());
            // ensure the request is okay
            if (weatherResponse.statusCode() != 200) {
                throw new IOException(weatherResponse.toString());
            } // if

            HttpRequest unsplashRequest = HttpRequest.newBuilder()
                .uri(URI.create(unsplashUri))
                .build();
            HttpResponse<String> unsplashResponse = HTTP_CLIENT
                .send(unsplashRequest, BodyHandlers.ofString());
            // ensure the request is okay
            if (unsplashResponse.statusCode() != 200) {
                throw new IOException(unsplashResponse.toString());
            } // if

            GeoApiResponse geoApiResponse = GSON
                .fromJson(geoResponse.body(), ApiSearch.GeoApiResponse.class);

            System.out.println(GSON.toJson(geoApiResponse));            

         } catch (IOException | InterruptedException e) {
            // either:
            // 1. an I/O error occurred when sending or receiving;
            // 2. the operation was interrupted; or
            // 3. the Image class could not load the image.
            System.err.println(e);
            e.printStackTrace();
        } // try

    } // main


    private static void setApiKeys() {
        try (FileInputStream configFileStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            config.load(configFileStream);
            config.list(System.out);                                  
            geoApiKey = config.getProperty("geo.api");         
            weatherApiKey = config.getProperty("weather.api");
            unsplashApiKey = config.getProperty("unsplash.api");
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try
    } // setApiKeys

} // ApiSearch



