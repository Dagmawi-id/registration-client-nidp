package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.LoggerConstants.LOG_ALERT_GENERATION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.stage.Screen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.controller.eodapproval.RegistrationApprovalController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
public class AlertController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(AlertController.class);
	@FXML
	private ImageView alertImage;
	//	@FXML
//	private Label header;
	@FXML
	private Label context;
	@FXML
	public Button closeButton;
	@FXML
	private HBox alertHbox;
	@FXML
	private VBox imageVbox;
	@FXML
	private VBox contextVbox;
	@FXML
	private GridPane alertGridPane;
	@Autowired
	private ScanPopUpViewController scanPopUpViewController;
	@Autowired
	private RegistrationApprovalController registrationApprovalController;

	private static Stage prevStage=null;
	/**
	 * @return the alertGridPane
	 */
	public GridPane getAlertGridPane() {
		return alertGridPane;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//fXComponents.getScene().getRoot().setDisable(true);

//		if (scanPopUpViewController.getPopupStage() != null && scanPopUpViewController.getPopupStage().isShowing()) {
//			scanPopUpViewController.getPopupStage().getScene().getRoot().setDisable(true);
//		} else if (registrationApprovalController.getPrimaryStage() != null
//				&& registrationApprovalController.getPrimaryStage().isShowing()) {
//			registrationApprovalController.getPrimaryStage().getScene().getRoot().setDisable(true);
//		}


	}
	static AlertController controller=null;

	public void generateAlertResponse(String title, String contextString) {
		if(prevStage!=null) prevStage.close();
		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert generation has been started");

		String[] split = contextString.split(RegistrationConstants.SPLITTER);
		String contextSecondMsg = RegistrationConstants.EMPTY;
		Image image;
		image = new Image(RegistrationConstants.FAILURE_IMG_PTH);
		if (split.length > 1 && split[1].contains(RegistrationConstants.SUCCESS.toUpperCase())) {
			image = new Image(RegistrationConstants.SUCCESS_IMG_PTH);
			//header.setText(RegistrationUIConstants.SUCCESS);
			alertImage.setImage(image);
			alertGridPane.setStyle("-fx-background-color: #008975;");
			contextSecondMsg = gettingSecondErrorMessage(split, RegistrationConstants.SUCCESS.toUpperCase());
		} else if (split.length > 1 && split[1].contains(RegistrationConstants.ERROR.toUpperCase())) {

			//header.setText(RegistrationUIConstants.ALERT_FAILED_LABEL);
			alertImage.setImage(image);
			alertGridPane.setStyle("-fx-background-color: #D50000;");
			contextSecondMsg = gettingSecondErrorMessage(split, RegistrationConstants.ERROR.toUpperCase());
		} else {
			//header.setText(RegistrationUIConstants.ALERT_NOTE_LABEL);
			alertImage.setImage(image);
			alertGridPane.setStyle("-fx-background-color: #D50000;");
			contextSecondMsg = gettingSecondErrorMessage(split, RegistrationConstants.INFO);
		}
		context.setText(split[0].trim() + contextSecondMsg);
		context.setStyle("-fx-text-fill: white;");
//		if (context.getText().length() > 50) {
//			imageVbox.setAlignment(Pos.TOP_CENTER);
//		}
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		Stage stage = (Stage) alertGridPane.getScene().getWindow();
		stage.setY(20);
		stage.setWidth(bounds.getWidth());
		stage.setX(0);
		stage.setAlwaysOnTop(true);

		prevStage=stage;
		//stage2 = (Stage)alertHbox.getScene().getWindow();
		//stage2.close();

		startTimerDisplay();

		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert generation has been ended");
	}

	private void startTimerDisplay() {
		Thread thread = new Thread() {
			public void run() {
				try {
					for (int i = 1; i <= 4; i++) {
						alertImage.setVisible(true);
						Thread.sleep(200);
						alertImage.setVisible(false);
						Thread.sleep(200);
					}
					alertImage.setVisible(true);

					Thread.sleep(10000);

					Platform.runLater(() -> closeButton.fire());

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}};
		thread.start();
	}

	private String gettingSecondErrorMessage(String[] split, String splitter) {
		StringBuilder errorMessage = new StringBuilder();
		for (int i = 1; i < split.length; i++) {
			errorMessage = errorMessage.append(split[i].replaceAll(splitter, RegistrationConstants.SPACE));
		}
		return errorMessage.toString();
	}

	@FXML
	public void alertWindowExit() {
		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert closing has been started");

		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
		fXComponents.getScene().getRoot().setDisable(false);
		if (scanPopUpViewController.getPopupStage() != null && scanPopUpViewController.getPopupStage().isShowing()) {
			scanPopUpViewController.getPopupStage().getScene().getRoot().setDisable(false);
		} else if (registrationApprovalController.getPrimaryStage() != null
				&& registrationApprovalController.getPrimaryStage().isShowing()) {
			registrationApprovalController.getPrimaryStage().getScene().getRoot().setDisable(false);
		}
		LOGGER.info(LOG_ALERT_GENERATION, APPLICATION_NAME, APPLICATION_ID, "Alert closing has been ended");

	}
}
