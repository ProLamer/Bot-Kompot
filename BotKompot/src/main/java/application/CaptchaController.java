package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class CaptchaController {
	@FXML
	private TextField captchaText;

	@FXML
	private WebView imageCaptcha;

	@FXML
	public void onSubmitButtonClick() {
		if (!captchaText.getText().isEmpty()) {
			MainViewController.captchaKey = captchaText.getText();
			MainViewController.stage.close();
		}
	}

	@FXML
	public void initialize() {
		WebEngine engine = imageCaptcha.getEngine();
		engine.load(MainViewController.captchaImg);
	}
}
