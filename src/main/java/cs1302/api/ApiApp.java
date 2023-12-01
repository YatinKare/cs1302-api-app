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
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.application.Platform;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    /** Instance variables for the stage and scene. */
    Stage stage;
    Scene scene;
    
    /** The root container for the application scene graph. */

    VBox root;

    /** The container for the Search functions*/
    HBox searchBarHBox;
    TextField searchField;
    Button searchButton;

    
    /** The container for the current weather */
    // IF NEEDED ADD HBOX FOR RESIZE!!!!!!
    Text currentWeatherText;
    
    /** The container for the DisplayComponent */
    HBox display;
    DisplayComponent city;
    DisplayComponent weather;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        /** Initialize root */    
        root = new VBox();
        
        /** Initialize Search components */
        searchBarHBox = new HBox(10);
        searchField = new TextField("Prompt City...");
        searchButton = new Button("Find");

        /** Initialize current weather text */
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
        /**
        // demonstrate how to load local asset using "file:resources/"
        Image bannerImage = new Image("file:resources/readme-banner.png");
        ImageView banner = new ImageView(bannerImage);

        banner.setPreserveRatio(true);
        banner.setFitWidth(640);

        // some labels to display information
        Label notice = new Label("Modify the starter code to suit your needs.");

        // setup scene
        root.getChildren().addAll(banner, notice);
        */
        
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBarHBox.getChildren().addAll(searchField, searchButton);


        display.getChildren().addAll(city, weather);
        root.getChildren().addAll(searchBarHBox, display);
        scene = new Scene(root);

        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));

    } // start

} // ApiApp
