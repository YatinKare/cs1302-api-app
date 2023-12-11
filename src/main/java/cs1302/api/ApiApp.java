package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import javafx.geometry.Pos;
import java.lang.Math;
/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    /** Instance variables for the stage and scene. */
    Stage stage;
    Scene scene;

    /** The root container for the application scene graph. */
    VBox root;

    /** The container for the Search functions.*/
    HBox searchBarHBox;
    TextField searchField;
    Button searchButton;

    /** The container for the current weather. */
    // IF NEEDED ADD HBOX FOR RESIZE!!!!!!
    HBox cWTHBox;
    static Text currentWeatherText;

    /** The container for the DisplayComponent. */
    HBox display;
    DisplayComponent city;
    DisplayComponent weather;

    /** Variables for outputs from ApiSearch.executeApiCall.*/
    String cityText;
    String weatherText;
    double temp;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        /** Initialize root */    
        root = new VBox(5);

        /** Initialize Search components */
        searchBarHBox = new HBox(10);
        searchField = new TextField("Prompt City...");
        searchButton = new Button("Find");

        /** Initialize current weather text */
        cWTHBox = new HBox();
        currentWeatherText = new Text();

        /** Initialize Display Components */
        display = new HBox(10);
        city = new DisplayComponent();
        weather = new DisplayComponent();
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        HBox.setHgrow(searchField, Priority.ALWAYS);
        this.searchButton.setOnAction(event -> {
            runNow(() -> this.apiMethod());
        });
        searchBarHBox.getChildren().addAll(searchField, searchButton);

        currentWeatherText.setTextAlignment(TextAlignment.CENTER);
        currentWeatherText.setText("PlaceHolderText");
        cWTHBox.setAlignment(Pos.CENTER);
        // cWTHBox.setMinWidth(root.getWidth();
        cWTHBox.getChildren().add(currentWeatherText);

        display.getChildren().addAll(city, weather);
        root.getChildren().addAll(searchBarHBox, cWTHBox, display);

        scene = new Scene(root);

        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));

    } // start
    /**
     * Creates and immediately starts a new daemon thread that executes
     * {@code target.run()}. This method, which may be called from any thread,
     * will return immediately its the caller.
     * @param target the object whose {@code run} method is invoked when this
     *               thread is started
     */
    private static void runNow(Runnable target) {
        Thread thread = new Thread(target);
        thread.setDaemon(true);
        thread.start();
    } // runNow

    /**
     * Creates a new {@code ApiSearch} object and calls its 
     * {@code executeApiCall} method with the current text 
     * of the search field. First the {@code currentWeatherText}
     * is changed to update the status. Second the {@code searchButton}
     * is disabled and then the api method is called.
     * After trying the Api calls,If the status text is not updated 
     * with "City Not Available", then it will update it wit the temperature
     * in Farenheit. It will also update the DisplayComponents
     * {@code city} and {@code weather} with their respective images.
     */
    private void apiMethod() {
        Platform.runLater(() -> {
            ApiApp.currentWeatherText.setText("Loading Temperature...");
            this.searchButton.setDisable(true);
        });
        ApiSearch apiSearch = new ApiSearch();
        String[] output = new String[3];
        output = apiSearch.executeApiCall(searchField.getText());
        if (!ApiApp.currentWeatherText.getText().equals("City Not available")) {
            try {
                temp = Double.valueOf(output[0]);
                temp = Math.round(((temp - 273.15) * (9/5) + 35) * Math.pow(10, 2)) / Math.pow(10, 2);
                String stringTemp = String.valueOf(temp);
                Platform.runLater(() -> {
                    ImageView imv = (ImageView) this.city.getChildren().get(0);
                    imv.setImage(new Image("file:resources/cityPhoto.jpg", 500, 500, true, true));
                });
                Platform.runLater(() -> {
                    ImageView imv = (ImageView) this.weather.getChildren().get(0);
                    imv.setImage(new Image("file:resources/weatherPhoto.jpg", 500, 500, true, true));
                });

                Platform.runLater(() -> {
                    ApiApp.currentWeatherText.setText("Current Temperature: " + stringTemp + "°F");
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            } // try
        }
        Platform.runLater(() -> this.searchButton.setDisable(false));
    } // apiMethod
} // ApiApp
