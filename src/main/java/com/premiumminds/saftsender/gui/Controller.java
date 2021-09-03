package com.premiumminds.saftsender.gui;

import com.premiumminds.saftsender.api.Arguments.Builder;
import com.premiumminds.saftsender.api.Arguments.Operation;
import com.premiumminds.saftsender.api.ISender;
import com.premiumminds.saftsender.sender.FACTEMICLISender;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;

public class Controller {

	@FXML
	private Spinner<Integer> yearSpinner;

	@FXML
	private Spinner<Month> monthSpinner;

	@FXML
	private Button sendButton;

	@FXML
	private Button fileButton;

	@FXML
	private TextField filePath;

	@FXML
	private TextField nifField;

	@FXML
	private TextField passwordField;

	@FXML
	private CheckBox testesField;

	@FXML
	private RadioButton sendRadioButton;

	@FXML
	private RadioButton validateRadioButton;

	@FXML
	private ToggleGroup operation;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private TextArea outputArea;

	void sendClicked(ActionEvent event) {

		if (!isFieldsValid()){
			return;
		}

		outputArea.clear();
		sendButton.setDisable(true);
		progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

		TextInputControlOutputStream stream = new TextInputControlOutputStream(outputArea);

		var args = Builder.builder()
						  .withInput(Path.of(filePath.getText().trim()))
						  .withMes(monthSpinner.getValue().getValue())
						  .withAno(Integer.toString(yearSpinner.getValue()))
						  .withNif(nifField.getText())
						  .withPassword(passwordField.getText())
						  .withTestes(testesField.isSelected())
						  .withOp((Operation) operation.getSelectedToggle().getUserData())
						  .build();
		outputArea.appendText(args.toString());
		outputArea.appendText(System.lineSeparator());
		new Thread(() -> {
			StringBuilder stringBuilder = new StringBuilder();
			try {
				ISender sender = new FACTEMICLISender();
				sender.send(args, stream);
				stringBuilder.append("DONE");
			} catch (Throwable t) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				stringBuilder.append(sw);
			}

			Platform.runLater(() -> {
				outputArea.appendText(stringBuilder.toString());
				sendButton.setDisable(false);
				progressBar.setProgress(0);
			});
		}).start();
	}

	private boolean isFieldsValid() {
		var errors = new ArrayList<String>();
		if (filePath.getText().isBlank()){
			errors.add("Escolha um ficheiro SAFT para enviar ou validar.");
		}
		if (nifField.getText().isBlank()){
			errors.add("Preencha o NIF.");
		}
		if (passwordField.getText().isBlank()){
			errors.add("Preencha a password.");
		}

		if (!errors.isEmpty()){
			Alert alert = new Alert(AlertType.ERROR, String.join("\n", errors));
			alert.showAndWait();
			return false;
		}
		return true;
	}

	@FXML
	void initialize() {
		final var yearSpinnerFactory = new IntegerSpinnerValueFactory(2000, LocalDate.now().getYear(), LocalDate.now().getYear());
		yearSpinner.setValueFactory(yearSpinnerFactory);

		final var months = FXCollections.observableArrayList(Month.values());
		final var listSpinnerValueFactory = new ListSpinnerValueFactory<>(months);
		listSpinnerValueFactory.setValue(LocalDate.now().minusMonths(1).getMonth());
		monthSpinner.setValueFactory(listSpinnerValueFactory);

		FileChooser fileChooser = new FileChooser();
		fileButton.setOnAction(e -> {
			final var file = fileChooser.showOpenDialog(null);
			if (file != null) {
				filePath.setText(file.getAbsolutePath());
			}
		});

		validateRadioButton.setUserData(Operation.VALIDATE);
		sendRadioButton.setUserData(Operation.SEND);

		sendButton.setOnAction(this::sendClicked);
		progressBar.setProgress(0);
	}

	public static class TextInputControlOutputStream extends OutputStream {

		private final TextInputControl output;

		public TextInputControlOutputStream(TextInputControl ta) {
			this.output = ta;
		}

		@Override
		public void write(int i) throws IOException {
			Platform.runLater(() -> output.appendText(String.valueOf((char) i)));
		}
	}
}
