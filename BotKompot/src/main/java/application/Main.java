package application;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {
	private static final Logger LOG = LogManager.getLogger(application.Main.class);
	public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
	public static final String VK_AUTH_URL = "https://oauth.vk.com/authorize?client_id=5777325&groups_ids=91043696&display=page"
			+ "&redirect_uri=https://oauth.vk.com/blank.html&scope=friends,groups,messages,wall,manage&response_type=token&v=5.60";
	public static String tokenUrl;

	public static void main(String[] args) throws Exception {
		Main.getTokenUrl();
		// launch(Main.class);

	}

	public static String getTokenUrl() {
		launch(Main.class);
		return tokenUrl;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/logo.png")));
		final WebView view = new WebView();
		final WebEngine engine = view.getEngine();
		engine.load(VK_AUTH_URL);

		primaryStage.setScene(new Scene(view));
		primaryStage.setTitle("Bot Kompot");
		primaryStage.show();

		engine.locationProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.startsWith(REDIRECT_URL)) {
					tokenUrl = newValue;
					// primaryStage.close();
					setNewScene(primaryStage);

				}
			}

		});
	}

	public static void setNewScene(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/MainView.fxml"));
			AnchorPane parent = (AnchorPane) loader.load();
			Scene scene = new Scene(parent);
			stage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}