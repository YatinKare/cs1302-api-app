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
/**
 * Creates a custom VBox component to hold the imageView,
 * Image, and source url.
 */

public class DisplayComponent extends VBox {
    /** All instance variables go here as STATIC.*/
    HBox displayComponentHBox;
    ImageView imv;
    Text sourceText;

    // Set these variables later
    private static final String DEFAULT_IMG = "file:resources/default.png";

    /**
     * Creates DisplayComponent object.
     */
    public DisplayComponent() {
        super();

        displayComponentHBox = new HBox(8);
        imv = new ImageView();
        sourceText = new Text("example");

        this.getChildren().addAll(imv, sourceText);

        imv.setImage(new Image(DEFAULT_IMG));
        imv.setPreserveRatio(true);
        //Image defaultImage = new Image(DEFAULT_IMAGE);
        // Insantiate -> root = new VBox();
        // Init code: add to the DisplayComponent
        // this.getChildren().addAll(...)
    } // DisplayComponent


} // DisplayComponent
