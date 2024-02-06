package io.mosip.registration.preloader;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.update.ClientSetupValidator;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPreLoader extends Preloader {

    private static final Logger logger = LoggerFactory.getLogger(ClientPreLoader.class);

    public static boolean errorsFound = false;
    public static boolean restartRequired = false;
    private Stage preloaderStage;
    private ProgressBar progressBar = new ProgressBar();
    private TextArea textArea = new TextArea();
    private Label label = new Label("Starting Registration Client : Please wait...");
    private Button stopClient = new Button("Stop Client");
    private Button hidePreLoader = new Button("Exit");
    private Image logoImage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResource("/NotoSans-Regular.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("/NotoSansEthiopic-Regular.ttf").toExternalForm(), 12);

        this.preloaderStage = primaryStage;

        Image logoImage = new Image(getClass().getResource(RegistrationConstants.LOGO).toExternalForm());
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(60);
        logoView.setFitWidth(60);

        String buttonStyle =
                "-fx-font-size: 16px; " +
                "-fx-pref-width: 100px; " +
                "-fx-pref-height: 40px; ";
        stopClient.setStyle(buttonStyle );
        hidePreLoader.setStyle(buttonStyle);

        Label titleLabel = new Label("Ethiopian National ID - Registration System");
        titleLabel.setFont(new Font("Noto Sans", 15));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label secondaryTitleLabel = new Label("የኢትዮጵያ ብሔራዊ መታወቂያ - የምዝገባ ስርዓት");
        secondaryTitleLabel.setFont(new Font("Noto Sans Ethiopic", 15));
        secondaryTitleLabel.setStyle("-fx-text-fill: white;");

        VBox titleBox = new VBox( titleLabel, secondaryTitleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(0);
        HBox header = new HBox(10, logoView, titleBox);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color:#0a384f; -fx-background-radius: 10 10 0 0; -fx-text-fill: white; -fx-padding: 10,0,0,10");
        header.prefWidthProperty().bind(primaryStage.widthProperty()); // **Ensures header stretches full width**

        VBox loading = new VBox(20);
        VBox loadingHolder = new VBox();
        // **Below line ensures VBox (loading) takes the maximum available width for alignment, but won't affect the header stretching**
        loading.setFillWidth(true);
        loading.getChildren().add(header); // Header is added first to stretch across the top
        // **For each component below, apply padding individually if needed**

        loadingHolder.setAlignment(Pos.CENTER);

        label.setStyle("-fx-font-size: 18;-fx-text-fill:#0a384f; ");
        loadingHolder.getChildren().add(label);

        progressBar.setStyle("-fx-pref-width: 400; -fx-pref-height: 40; " +
                "-fx-accent: #0a384f; " +
                "-fx-control-inner-background: #e0e0e0; " +
                "-fx-padding: 2;");
        loadingHolder.getChildren().add(progressBar);
        loadingHolder.getChildren().add(textArea);

        loading.getChildren().add(loadingHolder);

        VBox.setMargin(textArea, new Insets(20, 30, 10, 30));

        textArea.setPrefWidth(500);
        textArea.setPrefHeight(300);
        textArea.setStyle("-fx-background-color:white;-fx-padding: 10; -fx-background-radius: 5;");

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(hidePreLoader, stopClient);
        loading.getChildren().add(buttons);

//
//        hidePreLoader.setOnMouseEntered(e -> hidePreLoader.setStyle(buttonStyle + "-fx-background-color:#0a384f"));
//        hidePreLoader.setOnMouseExited(e -> hidePreLoader.setStyle(buttonStyle + "-fx-background-color:white"));
//
//        stopClient.setOnMouseEntered(e -> stopClient.setStyle(buttonStyle + "-fx-background-color:#0a384f"));
//        stopClient.setOnMouseExited(e -> stopClient.setStyle(buttonStyle + "-fx-background-color:white"));



        stopClient.setVisible(false);
        stopClient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.appendText("Exiting from application...\n");
                System.exit(0);
            }
        });

        hidePreLoader.setVisible(false);
        hidePreLoader.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preloaderStage.hide();
            }
        });


        BorderPane root = new BorderPane(loading);
        root.setStyle("-fx-background-color:white; -fx-background-radius: 10;");
        Scene scene = new Scene(root);
//        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Client Pre-Loader");
        primaryStage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.getIcons().add(new Image(getClass().getResource(RegistrationConstants.LOGO).toExternalForm()));
        primaryStage.show();
    }

    @Override
    public boolean handleErrorNotification(ErrorNotification info) {
        return true;
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if(info instanceof ClientPreLoaderErrorNotification) {
            errorsFound = true;
            Throwable t = ((ClientPreLoaderErrorNotification)info).getCause();
            textArea.appendText(t.getMessage()+"\n");
            hidePreLoader.setVisible(false);
            stopClient.setVisible(true);
            logger.error(t.getMessage(), t);
            return;
        }

        if(info instanceof ClientPreLoaderNotification) {
            textArea.appendText(((ClientPreLoaderNotification)info).getMessage());
            textArea.appendText("\n");
            return;
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_LOAD) {
            textArea.appendText("Started to validate the build setup...\n");
            try {
                progressBar.setProgress(0.1);
                ClientSetupValidator clientSetupValidator = new ClientSetupValidator();
                clientSetupValidator.validateBuildSetup();
                if(clientSetupValidator.isPatch_downloaded()) {
                    restartRequired = true;
                    throw new RegBaseCheckedException("","New patches downloaded, Kindly restart the client");
                }
                if(clientSetupValidator.isUnknown_jars_found()) {
                    restartRequired = true;
                    throw new RegBaseCheckedException("","Unrecognized jars found in the classpath, Kindly restart the client");
                }
                else
                    errorsFound = clientSetupValidator.isValidationFailed();

            } catch (RegBaseCheckedException e) {
                errorsFound = true;
                notifyPreloader(new ClientPreLoaderErrorNotification(e));
            }
            textArea.appendText("Build setup validation completed with status : " + (errorsFound ? "FAILURE" : "SUCCESS") + "\n");
        }
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_INIT) {
            if(!errorsFound) {
                textArea.appendText("Started to initialize application...\n");
                progressBar.setProgress(0.3);
            }
        }
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            if(restartRequired) {
                label.setText("Starting Registration Client : Restart Required");
            }
            else if(errorsFound) {
                label.setText("Starting Registration Client : Failed!");
            }
            else {
                textArea.appendText("Setting up application stage...\n");
                progressBar.setProgress(100);
                label.setText("Starting Registration Client : Success.");
                textArea.appendText("Registration client started :)\n");
                hidePreLoader.setVisible(true);
                stopClient.setVisible(true);
            }
        }
    }
}
