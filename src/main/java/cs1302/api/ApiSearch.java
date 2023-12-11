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
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Platform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class retrieves a images of the weather and the city based on the
 * search results.
 * @throws IOException if any unknown error.
 * @throws IllegalArgumentExcepton if any unknown error.
 */

public class ApiSearch {

    // Config variables for Apis
    private static String configPath = "resources/config.properties";
    private static String geoApiKey;
    private static String weatherApiKey;
    private static String unsplashApiKey;

    // Global variables for latitude and longitude
    private static String stringLat;
    private static String stringLon;

    // I/O variables
    private static String input;
    public String[] output = new String[3];

    /**
     * Represents an GeoCoding API document.
     */
    static class GeoApiResponse {
        float lat;
        float lon;
    } // GeoApiResponse

    /**
     * Represents a WeatherMap API document.
     */
    static class WeatherApiResponse {
        WeatherApiResult[] weather;
        WeatherApiMain main;
    } // WeatherApiResponse

    /**
     * Represents the Description object from the WeatherMap API result.
     */
    static class WeatherApiResult {
        String description;
    } // WeatherApiResult


    /**
     * Represnts the main object from the WeatherMap API result.
     */
    static class WeatherApiMain {
        double temp;
    } //WeatherApiMain

    /**
     * Represents a Unsplash API document.
     */
    static class UnsplashApiResponse {
        UnsplashApiResult[] results;
    } // UnsplashApiResponse

    /**
     * Represents an object of the Unsplash API.
     */
    static class UnsplashApiResult {
        UnsplashUrl urls;
    } // UnsplashApiResult

    /**
     * Represents an the results object of the Unsplash API result.
     */
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

    // Base Api query URIs
    private static final String GEO_API = "http://api.openweathermap.org/geo/1.0/direct";
    private static final String WEATHER_API = "https://api.openweathermap.org/data/2.5/weather";
    private static final String UNSPLASH_API = "https://api.unsplash.com/search/photos";

    /**
     * Takes in a {@code input} city and returns an array of strings {@code output}
     * with desired information. Uses HttpClient, HttpRequest, and
     * HttpResponse to request information. Uses GSON libary to pase JSON
     * information. if Geo Coordinates array is null or has no elements,
     * update {@code currentWeatherText} to show user error.
     * @param inp city String input.
     * @return String[] array of important values.
     * @throws IOException if any errors.
     * @throws InterruptedException if any errors.
     */
    public String[] executeApiCall(String inp) {
        input = inp;
        setApiKeys();
        try {
            // checksif getGeoApiData returns true.
            if (getGeoApiData()) {
                // -------------------------WEATHER API - TEMPERATURE-------------
                String latQuery = URLEncoder.encode(stringLat, StandardCharsets.UTF_8);
                String lonQuery =  URLEncoder.encode(stringLon, StandardCharsets.UTF_8);
                String query = String.format("?lat=%s&lon=%s&appid=%s",
                    latQuery,
                    lonQuery,
                    weatherApiKey);
                String weatherUri = WEATHER_API + query;

                HttpRequest weatherRequest = HttpRequest.newBuilder()
                    .uri(URI.create(weatherUri))
                    .build();
                HttpResponse<String> weatherResponse = HTTP_CLIENT
                    .send(weatherRequest, BodyHandlers.ofString());
                // ensure the request is okay
                if (weatherResponse.statusCode() != 200) {
                    throw new IOException(weatherResponse.toString());
                } // if

                String stringWeatherResponse = weatherResponse.body();

                WeatherApiResponse weatherApiResponse = GSON
                    .fromJson(stringWeatherResponse, ApiSearch.WeatherApiResponse.class);

                // System.out.println(GSON.toJson(weatherApiResponse));
                String weatherDescription = weatherApiResponse.weather[0].description + " weather";
                output[0] = String.valueOf(weatherApiResponse.main.temp);

                //CITY PHOTO
                output[1] = getCityPhoto();

                //WEATHER PHOTO
                output[2] = getWeatherPhoto(weatherDescription);
            } else {
                Platform.runLater(() -> {
                    ApiApp.currentWeatherText.setText("City Not available");
                });
            } // if

        } catch (IOException | InterruptedException e) {
            // either:
            // 1. an I/O error occurred when sending or receiving;
            // 2. the operation was interrupted; or
            // 3. the Image class could not load the image.
            System.err.println("No Data found");
        } // try
        return output;
    } // executeApiCall

    /**
     * Conducts API search using the Geo Api. Gets coordinates of input city.
     * Returns weather of not the city was found.
     * @return true if coordinates are found, false if not.
     * @throws IOException if I/O error.
     * @throws InterruptedException if operation was interrupted.
     */
    private static boolean getGeoApiData() throws IOException, InterruptedException {
        String city = URLEncoder.encode(input, StandardCharsets.UTF_8);
        String limit = URLEncoder.encode("5", StandardCharsets.UTF_8);
        String query = String.format("?q=%s&limit=%s&appid=%s", city, limit, geoApiKey);
        String geoUri = GEO_API + query;

        // System.out.println(geoUri);

        HttpRequest geoRequest = HttpRequest.newBuilder()
            .uri(URI.create(geoUri))
            .build();
        HttpResponse<String> geoResponse = HTTP_CLIENT
            .send(geoRequest, BodyHandlers.ofString());
        // ensure the request is okay
        if (geoResponse.statusCode() != 200) {
            System.err.println(geoResponse.statusCode());
            throw new IOException(geoResponse.toString());
        } // if

        String stringGeoResponse = geoResponse.body();
        // System.out.println(stringGeoResponse.trim());
        GeoApiResponse[] geoApiResponse = GSON
            .fromJson(stringGeoResponse, ApiSearch.GeoApiResponse[].class);

        // System.out.println(GSON.toJson(geoApiResponse));
        if (geoApiResponse == null || geoApiResponse.length == 0) {
            return false;
        } else {
            stringLat = String.valueOf(geoApiResponse[0].lat);
            stringLon = String.valueOf(geoApiResponse[0].lon);
            return true;
        }
    } // getGeoApiData

    /**
     * Conducts API search using the Unsplash API. Gets image url of input city.
     * Similar logic as {@code getGeoApiData()}. Returns city image url.
     * @return outputCity returns uri of original city image
     * @throws IOException if I/O error.
     * @throws InterruptedException if operation was interrupted.
     */
    private String getCityPhoto() throws IOException, InterruptedException {

        String photoCity = URLEncoder.encode(input, StandardCharsets.UTF_8);
        String query = String.format("?query=%s&client_id=%s", photoCity, unsplashApiKey);
        String unsplashUri = UNSPLASH_API + query;

        HttpRequest unsplashRequest = HttpRequest.newBuilder()
            .uri(URI.create(unsplashUri))
            .build();
        HttpResponse<String> unsplashResponse = HTTP_CLIENT
            .send(unsplashRequest, BodyHandlers.ofString());
        // ensure the request is okay
        if (unsplashResponse.statusCode() != 200) {
            throw new IOException(unsplashResponse.toString());
        } // if

        String stringUnsplashResponse = unsplashResponse.body();
        // System.out.println(stringUnsplashResponse.trim());
        UnsplashApiResponse unsplashApiResponse = GSON
            .fromJson(stringUnsplashResponse, ApiSearch.UnsplashApiResponse.class);

        // System.out.println(GSON.toJson(unsplashApiResponse.results[0].urls.regular));
        String outputCity = unsplashApiResponse.results[0].urls.regular;

        String fileName = "resources/cityPhoto.jpg";
        Path outputPath = Path.of(fileName);


        unsplashRequest = HttpRequest.newBuilder()
            .uri(URI.create(outputCity))
            .build();
        HttpResponse<Path> unsplashResponseCity = HTTP_CLIENT
            .send(unsplashRequest, BodyHandlers.ofFile(outputPath));
        // ensure the request is okay
        if (unsplashResponseCity.statusCode() != 200) {
            throw new IOException(unsplashResponseCity.toString());
        } // if

        return outputCity;
    } // getCityPhoto

    /**
     * Conducts API search using the Unsplash API. Gets image url of weather.
     * Uses previously found {@code weatherDescription}.
     * Similar logic as {@code getGeoApiData()}.
     * Returns url from image found.
     * @param wD {@code weatherDescription} variable.
     * @return outputWeather contains weather Url for image.
     * @throws IOException if I/O error.
     * @throws InterruptedException if operation was interrupted.
     */
    private String getWeatherPhoto(String wD) throws IOException, InterruptedException {
        String photoWeather = URLEncoder.encode(wD, StandardCharsets.UTF_8);
        String query = String.format("?query=%s&client_id=%s", photoWeather, unsplashApiKey);
        String unsplashUri = UNSPLASH_API + query;

        HttpRequest unsplashRequest = HttpRequest.newBuilder()
            .uri(URI.create(unsplashUri))
            .build();
        HttpResponse<String> unsplashResponse = HTTP_CLIENT
            .send(unsplashRequest, BodyHandlers.ofString());
        // ensure the request is okay
        if (unsplashResponse.statusCode() != 200) {
            throw new IOException(unsplashResponse.toString());
        } // if

        String stringUnsplashResponse = unsplashResponse.body();
        // System.out.println(stringUnsplashResponse.trim());
        UnsplashApiResponse unsplashApiResponse = GSON
            .fromJson(stringUnsplashResponse, ApiSearch.UnsplashApiResponse.class);

        // System.out.println(GSON.toJson(unsplashApiResponse.results[0].urls.regular));
        String outputWeather = unsplashApiResponse.results[0].urls.regular;

        String fileName = "resources/weatherPhoto.jpg";
        Path outputPath = Path.of(fileName);

        String photoWeather2 = URLEncoder.encode(wD, StandardCharsets.UTF_8);
        query = String.format("?query=%s&client_id=%s", photoWeather2, unsplashApiKey);
        unsplashUri = UNSPLASH_API + query;

        unsplashRequest = HttpRequest.newBuilder()
            .uri(URI.create(outputWeather))
            .build();
        HttpResponse<Path> unsplashResponse2 = HTTP_CLIENT
            .send(unsplashRequest, BodyHandlers.ofFile(outputPath));
        // ensure the request is okay
        if (unsplashResponse2.statusCode() != 200) {
            throw new IOException(unsplashResponse2.toString());
        } // if

        return outputWeather;

    } //getWeatherPhoto

    /**
     * Using {@code config.properties}, sets Api Key variables.
     * Throws errors if found.
     * @throw IOException if any errors found.
     */
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
