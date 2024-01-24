/**
 *
 */
package io.mosip.registration.util.control.impl;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import et.nidp.utility.EthDateDTO;
import et.nidp.utility.EthiopianDateUtility;
import io.mosip.registration.controller.ClientApplication;
import org.springframework.context.ApplicationContext;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.controller.Initialization;
import io.mosip.registration.controller.reg.DateValidation;
import io.mosip.registration.dto.mastersync.GenericDto;
import io.mosip.registration.dto.schema.UiFieldDTO;
import io.mosip.registration.util.control.FxControl;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author M1044402
 */
public class DOBAgeFxControl extends FxControl {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DOBAgeFxControl.class);
	private static final String DOBSubType = "dateOfBirth";
	private static String loggerClassName = "DOB Age Control Type Class";
	private FXUtils fxUtils;

	private DateValidation dateValidation;

	//Note: Fields added for ET Date Conversion
	static DOBAgeFxControl dateFieldGC;
	static DOBAgeFxControl dateFieldEC;
	private String fieldID = "";
	public String dateType = "GC";
	public static boolean canFireOnChange = true;
	public TextField ddFiled;
	public TextField mmField;
	public TextField yyField;
	public TextField ageField;

	public DOBAgeFxControl() {
		fxUtils = FXUtils.getInstance();
		ApplicationContext applicationContext = ClientApplication.getApplicationContext();
		this.dateValidation = applicationContext.getBean(DateValidation.class);
	}

	@Override
	public FxControl build(UiFieldDTO uiFieldDTO) {
		this.uiFieldDTO = uiFieldDTO;
		this.fieldID = uiFieldDTO.getId();



		this.control = this;
		VBox appLangDateVBox = create(uiFieldDTO);
		HBox hBox = new HBox();
		hBox.setSpacing(30);
		hBox.getChildren().add(appLangDateVBox);
		HBox.setHgrow(appLangDateVBox, Priority.ALWAYS);

		this.node = hBox;
		setListener(hBox);

		if (uiFieldDTO.getId().contains("_LocalDate")) {
			DOBAgeFxControl.dateFieldEC = this;
			this.ageField.getParent().setVisible(false);
			this.dateType = "ET";
		} else {
			DOBAgeFxControl.dateFieldGC = this;
			this.dateType = "GC";
		}

		return this.control;
	}

	private VBox create(UiFieldDTO uiFieldDTO) {

		HBox dobHBox = new HBox();
		dobHBox.setId(uiFieldDTO.getId() + RegistrationConstants.HBOX);
		dobHBox.setSpacing(10);

		String mandatorySuffix = getMandatorySuffix(uiFieldDTO);

		String langCode = getRegistrationDTo().getSelectedLanguagesByApplicant().get(0);
		ResourceBundle resourceBundle = io.mosip.registration.context.ApplicationContext.getInstance()
				.getBundle(langCode, RegistrationConstants.LABELS);

		VBox ageVBox = new VBox();
		ageVBox.setPrefWidth(390);

		List<String> labels = new ArrayList<>();
		getRegistrationDTo().getSelectedLanguagesByApplicant().forEach(lCode -> {
			labels.add(this.uiFieldDTO.getLabel().get(lCode));
		});

		/** DOB Label */
		ageVBox.getChildren().add(getLabel(uiFieldDTO.getId() + RegistrationConstants.LABEL,
				String.join(RegistrationConstants.SLASH, labels) + mandatorySuffix, RegistrationConstants.DEMOGRAPHIC_FIELD_LABEL, true, ageVBox.getWidth()));

		/** Add Date */
		dobHBox.getChildren().add(addDateTextField(uiFieldDTO, RegistrationConstants.DD,
				resourceBundle.getString(RegistrationConstants.DD)));
		/** Add Month */
		dobHBox.getChildren().add(addDateTextField(uiFieldDTO, RegistrationConstants.MM,
				resourceBundle.getString(RegistrationConstants.MM)));
		/** Add Year */
		dobHBox.getChildren().add(addDateTextField(uiFieldDTO, RegistrationConstants.YYYY,
				resourceBundle.getString(RegistrationConstants.YYYY)));

//		/** OR Label */
//		dobHBox.getChildren()
//				.add(getLabel(uiSchemaDTO.getId() + RegistrationConstants.LABEL,
//						resourceBundle.getString("ageOrDOBField"), RegistrationConstants.DEMOGRAPHIC_FIELD_LABEL, true,
//						ageVBox.getWidth()));

//        Label label = getLabel(uiFieldDTO.getId() + "OR" + RegistrationConstants.LABEL,
//                resourceBundle.getString("ageOrDOBField"), RegistrationConstants.DEMOGRAPHIC_FIELD_LABEL, true, dobHBox.getWidth());
//        label.setMinWidth(Region.USE_PREF_SIZE);
//        label.setAlignment(Pos.CENTER);
//        dobHBox.getChildren().add(label);

		/** Add Age Field */

		dobHBox.getChildren().add(addDateTextField(uiFieldDTO, RegistrationConstants.AGE_FIELD,
				resourceBundle.getString(RegistrationConstants.AGE_FIELD)));

//		/** YEARS Label */
//		dobHBox.getChildren()
//				.add(getLabel(uiSchemaDTO.getId() + RegistrationConstants.LABEL,
//						"YEARS, RegistrationConstants.DEMOGRAPHIC_FIELD_LABEL, true,
//						ageVBox.getWidth()));
		ageVBox.getChildren().add(dobHBox);

		/** Validation message (Invalid/wrong,,etc,.) */
		ageVBox.getChildren().add(getLabel(uiFieldDTO.getId() + RegistrationConstants.ERROR_MSG, null,
				RegistrationConstants.DemoGraphicFieldMessageLabel, false, ageVBox.getPrefWidth()));

		dobHBox.prefWidthProperty().bind(ageVBox.widthProperty());

		changeNodeOrientation(ageVBox, langCode);
		return ageVBox;
	}

	private VBox addDateTextField(UiFieldDTO uiFieldDTO, String dd, String text) {

		VBox dateVBox = new VBox();
		dateVBox.setId(uiFieldDTO.getId() + dd + RegistrationConstants.VBOX);

		double prefWidth = dateVBox.getPrefWidth();

		/** DOB Label */
		dateVBox.getChildren().add(getLabel(uiFieldDTO.getId() + dd + RegistrationConstants.LABEL, text,
				RegistrationConstants.DEMOGRAPHIC_FIELD_LABEL, false, prefWidth));

		/** DOB Text Field */
		dateVBox.getChildren().add(getTextField(uiFieldDTO.getId() + dd + RegistrationConstants.TEXT_FIELD, text,
				RegistrationConstants.DEMOGRAPHIC_TEXTFIELD, prefWidth, false));

		return dateVBox;
	}


	@Override
	public void setData(Object data) {
		TextField dd = (TextField) getField(
				uiFieldDTO.getId() + RegistrationConstants.DD + RegistrationConstants.TEXT_FIELD);
		TextField mm = (TextField) getField(
				uiFieldDTO.getId() + RegistrationConstants.MM + RegistrationConstants.TEXT_FIELD);
		TextField yyyy = (TextField) getField(
				uiFieldDTO.getId() + RegistrationConstants.YYYY + RegistrationConstants.TEXT_FIELD);
		if(dd!=null && mm!=null&&yyyy!=null)
			getRegistrationDTo().setDateField(uiFieldDTO.getId(), dd.getText(), mm.getText(), yyyy.getText(), uiFieldDTO.getSubType());
	}

	@Override
	public Object getData() {
		return getRegistrationDTo().getDemographics().get(uiFieldDTO.getId());
	}


	@Override
	public boolean isValid() {
		return dateValidation.validateDate((Pane) node, uiFieldDTO.getId());
	}

	@Override
	public boolean isEmpty() {
		TextField dd = (TextField) getField(
				uiFieldDTO.getId() + RegistrationConstants.DD + RegistrationConstants.TEXT_FIELD);
		TextField mm = (TextField) getField(
				uiFieldDTO.getId() + RegistrationConstants.MM + RegistrationConstants.TEXT_FIELD);
		TextField yyyy = (TextField) getField(
				uiFieldDTO.getId() + RegistrationConstants.YYYY + RegistrationConstants.TEXT_FIELD);
		return dd != null && dd.getText().isEmpty() && mm != null && mm.getText().isEmpty() && yyyy != null
				&& yyyy.getText().isEmpty();
	}

	@Override
	public List<GenericDto> getPossibleValues(String langCode) {
		return null;
	}

	@Override
	public void setListener(Node node) {

		addListener(
				(TextField) getField(uiFieldDTO.getId() + RegistrationConstants.DD + RegistrationConstants.TEXT_FIELD),
				RegistrationConstants.DD);
		addListener(
				(TextField) getField(uiFieldDTO.getId() + RegistrationConstants.MM + RegistrationConstants.TEXT_FIELD),
				RegistrationConstants.MM);
		addListener(
				(TextField) getField(
						uiFieldDTO.getId() + RegistrationConstants.YYYY + RegistrationConstants.TEXT_FIELD),
				RegistrationConstants.YYYY);
		addListener(
				(TextField) getField(
						uiFieldDTO.getId() + RegistrationConstants.AGE_FIELD + RegistrationConstants.TEXT_FIELD),
				RegistrationConstants.AGE_FIELD);

	}

	private void addListener(TextField textField, String dateFieldType) {
		textField.textProperty().addListener((ob, ov, nv) -> {
					try {
						if (DOBAgeFxControl.canFireOnChange) {
							if (this.dateType.equalsIgnoreCase("GC")) {
								fxUtils.toggleUIField((Pane) node,
										textField.getId().replaceAll(RegistrationConstants.TEXT_FIELD, "") + RegistrationConstants.LABEL,
										!textField.getText().isEmpty());

								if (!dateValidation.isNewValueValid(nv, dateFieldType)) {
									textField.setText(ov);
								}
								boolean isValid = RegistrationConstants.AGE_FIELD.equalsIgnoreCase(dateFieldType)
										? dateValidation.validateAge((Pane) node, this.fieldID)// uiFieldDTO.getId())
										: dateValidation.validateDate((Pane) node, this.fieldID);//uiFieldDTO.getId());
								if (isValid) {
									Calendar gcDate = Calendar.getInstance();
									gcDate.set(Integer.parseInt(dateFieldGC.yyField.getText()), Integer.parseInt(dateFieldGC.mmField.getText()) - 1, Integer.parseInt(dateFieldGC.ddFiled.getText()));
									EthDateDTO date = EthiopianDateUtility.ToEthiopianDate(gcDate.getTime());

									DOBAgeFxControl.canFireOnChange = false;
									dateFieldEC.ddFiled.setText(String.valueOf(date.getDay()));
									dateFieldEC.mmField.setText(String.valueOf(date.getMonth()));
									dateFieldEC.yyField.setText(String.valueOf(date.getYear()));

									LocalDate dated = LocalDate.of(gcDate.get(Calendar.YEAR),gcDate.get(Calendar.MONTH)+1,gcDate.get(Calendar.DATE));
									String age = String.valueOf(Period.between(dated, LocalDate.now(ZoneId.of("UTC"))).getYears());
									dateFieldEC.ageField.setText(age);

									DOBAgeFxControl.canFireOnChange = true;
									setData(null);
									refreshFields();
								}


							} else if (this.dateType.equalsIgnoreCase("ET")) {
								//Ethiopian Date Control
								fxUtils.toggleUIField((Pane) node,
										textField.getId().replaceAll(RegistrationConstants.TEXT_FIELD, "") + RegistrationConstants.LABEL,
										!textField.getText().isEmpty());

								if (!dateValidation.isNewETDateValueValid(nv, dateFieldType)) {
									textField.setText(ov);
								}
								boolean isValid = RegistrationConstants.AGE_FIELD.equalsIgnoreCase(dateFieldType)
										? dateValidation.validateAge((Pane) node, this.fieldID)// uiFieldDTO.getId())
										: dateValidation.validateDate((Pane) node, this.fieldID);//uiFieldDTO.getId());
								if (isValid) {

									Date date = EthiopianDateUtility.ToGregorianDate(Integer.parseInt(dateFieldEC.yyField.getText()), Integer.parseInt(dateFieldEC.mmField.getText()), Integer.parseInt(dateFieldEC.ddFiled.getText()));

									Calendar gcDate = Calendar.getInstance();
									gcDate.setTime(date);
									DOBAgeFxControl.canFireOnChange = false;
									String dd=String.valueOf(gcDate.get(Calendar.DATE));
									String mm=String.valueOf(gcDate.get(Calendar.MONTH)+1);
									String yy=String.valueOf(gcDate.get(Calendar.YEAR));
									dateFieldGC.ddFiled.setText(dd);
									dateFieldGC.mmField.setText(mm);
									dateFieldGC.yyField.setText(yy);

									LocalDate dated = LocalDate.of(gcDate.get(Calendar.YEAR),gcDate.get(Calendar.MONTH)+1,gcDate.get(Calendar.DATE));
									String age = String.valueOf(Period.between(dated, LocalDate.now(ZoneId.of("UTC"))).getYears());

									dateFieldGC.ageField.setText(age);

									DOBAgeFxControl.canFireOnChange = true;

//                            setData(null);
									getRegistrationDTo().setDateField(uiFieldDTO.getId(), dd, mm, yy, uiFieldDTO.getSubType());
									refreshFields();
								}

							}
						}

					} catch (Exception e) {
						DOBAgeFxControl.canFireOnChange = true;
						throw new RuntimeException(e);
					}
				}
		);
	}

	@Override
	public void fillData(Object data) {
		// TODO Parse and set the date
	}


	private TextField getTextField(String id, String titleText, String demographicTextfield, double prefWidth,
								   boolean isDisable) {

		/** Text Field */
		TextField textField = new TextField();
		textField.setId(id);
		textField.setPromptText(titleText);
		textField.getStyleClass().add(RegistrationConstants.DEMOGRAPHIC_TEXTFIELD);
		// textField.setPrefWidth(prefWidth);
		textField.setDisable(isDisable);

		if (id.contains(RegistrationConstants.DD))
			this.ddFiled = textField;
		else if (id.contains(RegistrationConstants.MM))
			this.mmField = textField;
		else if (id.contains(RegistrationConstants.YYYY))
			this.yyField = textField;
		else if (id.contains(RegistrationConstants.AGE_FIELD))
			this.ageField = textField;

		return textField;
	}

	@Override
	public void selectAndSet(Object data) {
		TextField yyyy = ((TextField) getField(
				this.uiFieldDTO.getId() + RegistrationConstants.YYYY + RegistrationConstants.TEXT_FIELD));
		TextField mm = ((TextField) getField(
				this.uiFieldDTO.getId() + RegistrationConstants.MM + RegistrationConstants.TEXT_FIELD));
		TextField dd = ((TextField) getField(
				this.uiFieldDTO.getId() + RegistrationConstants.DD + RegistrationConstants.TEXT_FIELD));

		if (data == null || ((String) data).trim().isEmpty()) {
			yyyy.clear();
			mm.clear();
			dd.clear();
			return;
		}

		if (data instanceof String) {
			String[] dobArray = ((String) data).split("/");
			yyyy.setText(dobArray[0]);
			mm.setText(dobArray[1]);
			dd.setText(dobArray[2]);
		}
	}

}
