package application;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.objects.groups.responses.GetMembersFieldsResponse;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.likes.LikesType;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.wall.WallGetFilter;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainViewController {
	static int currentTime;
	int userId = parseIdOFUser(Main.tokenUrl);
	public static int ownerId;
	String code = parseAccessToken(Main.tokenUrl);
	VkApiClient vk = new VkApiClient(new HttpTransportClient());
	VkApiInitialize initializator = new VkApiInitialize(vk);
	UserActor actor = initializator.initUserActor(userId, code);
	public static Pattern hashtags = Pattern.compile(
			"(?s)(.*?)\\#(iphone3g@apple_lb|iphone3gs@apple_lb|iphone4@apple_lb|iphone4s@apple_lb|iphone5@apple_lb|iphone5s@apple_lb|iphone5c@apple_lb|iphone5se@apple_lb|iphone6@apple_lb|iphone6plus@apple_lb|iphone6s@apple_lb|iphone6splus@apple_lb|iphone7@apple_lb|iphone7plus@apple_lb|ipad@apple_lb|ipod@apple_lb|iwatch@apple_lb|macbook@apple_lb|mac@apple_lb|accesories@apple_lb|services@apple_lb).*");
	public static final Pattern findKeyWords = Pattern.compile(
			".*(iphone 6s\\+|iphone 6s|iphone 7\\+|iphone 3gs|iphone 3g|iphone 4s|iphone 4|iphone 5se|iphone 5s|iphone 5c|"
					+ "iphone 5|iphone 6\\+|iphone 6|iphone 7|ipad|ipod|iwatch|macbook|mac|earpods).*",
			Pattern.CASE_INSENSITIVE);
	public static String nameOfCommunity;
	public static String rules;
	private int shift;
	private ObservableList<String> groups = FXCollections.observableArrayList("Львівська Барахолка | Apple | Львів");
	private ObservableList<Integer> hoursDigits = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
	private ObservableList<Integer> minutesDigits = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40,
			45, 50, 55);
	static String captchaSid = null;
	static String captchaImg = null;
	static String captchaKey = null;
	static Stage stage = new Stage();
	@FXML
	private TextField shiftTime;

	@FXML
	private TextField groupIdLikes;

	@FXML
	private ChoiceBox<String> listOfTags;

	@FXML
	private ChoiceBox<Integer> hours;

	@FXML
	private ChoiceBox<Integer> minutes;

	@FXML
	private DatePicker date;

	@FXML
	private Button runBotButton;

	@FXML
	private Button likesButton;

	@FXML
	public TextArea logs;

	public int getOwnerId() {
		return ownerId;
	}

	public int getShift() {
		return shift;
	}

	public void setShift(int shift) {
		if (shift < 101 && shift > 0) {
			this.shift = shift;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Не коректно заповнені поля");
			alert.setContentText("Заповніть, будь ласка, правильно поле із зсувом по часу.");
			alert.showAndWait();
		}
	}

	@FXML
	public void onRunButtonClick() throws ApiException, ClientException, InterruptedException {
		int days = date.getValue().getDayOfMonth();
		int year = date.getValue().getYear();
		int month = date.getValue().getMonthValue();
		int hour = hours.getValue();
		int minute = minutes.getValue();
		logs.setEditable(false);
		currentTime = getUnixTime(days, year, month, hour, minute);

		MainViewController controller = new MainViewController();
		controller.setShift(Integer.parseInt(shiftTime.getText()));

		InnerRunnable runnable = new InnerRunnable();
		Platform.runLater(runnable);
	}

	@FXML
	public void onLikesButtonClick() {
		String groupIdLikesLokal = groupIdLikes.getText();
		if (!groupIdLikes.getText().isEmpty()) {
			try {
				setLikes(groupIdLikesLokal);
			} catch (ApiException | ClientException | InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Не коректно заповнені поля");
			alert.setContentText("Заповніть, будь ласка, правильно поле із значенням id спільноти.");
			alert.showAndWait();
		}
	}

	@FXML
	public void onCleanButtonClick() {
		logs.clear();
	}

	public class InnerRunnable implements Runnable {

		@Override
		public void run() {
			try {
				publishAdv(ownerId, hashtags);
			} catch (ApiException | ClientException | InterruptedException e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setContentText("Виникла помилка, спробуйте, будь ласка, інші дані");
				alert.showAndWait();
			}
		}

	}

	@FXML
	public void initialize() {
		shiftTime.setText("20");

		listOfTags.setItems(groups);
		hours.setItems(hoursDigits);
		minutes.setItems(minutesDigits);

		date.setValue(LocalDate.now());
		listOfTags.setValue(groups.get(0));
		hours.setValue(10);
		minutes.setValue(0);

		nameOfCommunity = "Apple Барахолка";
		rules = "https://vk.cc/5TSEiv";
		ownerId = -91043696;
		listener();
	}

	private int getUnixTime(int days, int year, int month, int hour, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, days, hour, minutes, 0);
		return (int) (calendar.getTimeInMillis() / 1000l);
	}

	public void listener() {
		listOfTags.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				switch (newValue.intValue()) {
				case 0: {
					nameOfCommunity = "Apple Барахолка";
					rules = "https://vk.cc/5TSEiv";
					ownerId = -91043696;
					// p =
					// Pattern.compile("(?s)(.*?)\\#(iphone3g@apple_lb|iphone3gs@apple_lb|iphone4@apple_lb|iphone4s@apple_lb|iphone5@apple_lb|iphone5s@apple_lb|iphone5c@apple_lb|iphone5se@apple_lb|iphone6@apple_lb|iphone6plus@apple_lb|iphone6s@apple_lb|iphone6splus@apple_lb|iphone7@apple_lb|iphone7plus@apple_lb|ipad@apple_lb|ipod@apple_lb|iwatch@apple_lb|macbook@apple_lb|mac@apple_lb|accesories@apple_lb|services@apple_lb).*");
					break;
				}
				default:
					break;
				}
			}
		});
	}

	private void publishAdv(int ownerId, Pattern p) throws ApiException, ClientException, InterruptedException {
		GetResponse getResponse = vk.wall().get(actor).ownerId(ownerId).count(100).filter(WallGetFilter.SUGGESTS)
				.execute();
		List<WallpostAttachment> attachments = new ArrayList<>();

		String message = "Привіт. Ви запропонували оголошення у спільноту " + nameOfCommunity
				+ ", яке згідно правил є неправильним (правила тут: " + rules + ")." + "\n\n"
				+ "Вартість публікації термінового оголошення, яке ми оформимо згідно правил самі -  15 грн. (оплата: приват-банк)."
				+ "\n" + "Щоб оплатити звертайтеся:" + "\n" + "- https://vk.cc/2ZFzUs" + "\n"
				+ "- https://vk.cc/61098d";

		String m1 = "\nКількість запропонованих оголошень в спільноті id = " + ownerId + " рівна "
				+ getResponse.getCount();
		logs.appendText(m1);
		int counter = 0;

		for (int i = 0; i < getResponse.getCount(); i++) {
			String text = getResponse.getItems().get(i).getText();
			attachments = getResponse.getItems().get(i).getAttachments();

			if (checkForCurrent(text, hashtags)) {
				int goodAdv = getResponse.getItems().get(i).getId();
				String m2 = "\nоголошення №" + goodAdv + " правильне";
				logs.appendText(m2);
				getPostponedTimes(actor, ownerId, goodAdv, vk, attachments);
				counter++;
				TimeUnit.MILLISECONDS.sleep(350);
			} else {
				int advId = getResponse.getItems().get(i).getId();
				int fromId = getResponse.getItems().get(i).getFromId();
				String user = String.valueOf(fromId);
				String[] users = new String[1];
				users[0] = user;

				if (getCurrentHashTag(text).isEmpty()) {
					vk.wall().delete(actor).ownerId(ownerId).postId(advId).execute();
					String m3 = "\nоголошення №" + advId + " видалене";
					logs.appendText(m3);
					TimeUnit.MILLISECONDS.sleep(350);
				} else {
					if (attachments == null) {
						currentTime += 1200;
						vk.wall().post(actor).ownerId(ownerId).signed(true).postId(advId)
								.message(text + "\n" + getCurrentHashTag(text)).publishDate((int) currentTime)
								.execute();
						String m = "\nоголошення №" + advId + " виправлене і буде опубліковане о: "
								+ getNormalTime(currentTime);
						logs.appendText(m);
					} else {
						currentTime += 1200;
						vk.wall().post(actor).ownerId(ownerId).signed(true).attachments(getPhotoList(attachments))
								.postId(advId).message(text + "\n" + getCurrentHashTag(text))
								.publishDate((int) currentTime).execute();
						String m = "\nоголошення №" + advId + " виправлене і буде опубліковане о: "
								+ getNormalTime(currentTime);
						logs.appendText(m);
					}
					TimeUnit.MILLISECONDS.sleep(350);
				}

				// if(vk.users().get().fields(UserField.CAN_WRITE_PRIVATE_MESSAGE).userIds(users).execute().get(0).canWritePrivateMessage()){
				// vk.messages().send(actor).peerId(fromId).message(message).execute();
				// logs.appendText(", користувача id = "+fromId+" повідомлено
				// про видалення посту");
				// TimeUnit.MILLISECONDS.sleep(350);
				// }

				// if(vk.friends().add(actor, fromId).execute().getValue() ==
				// 1){
				// logs.appendText(", користувача id = "+fromId+" додано в
				// друзі");
				// }

			}
		}
		String m4 = "\nправильних оголошень: " + counter;
		logs.appendText(m4);
	}

	private void setLikes(String groupIdLikesLokal) throws ApiException, ClientException, InterruptedException {
		List<UserXtrRole> users = new ArrayList<>();
		int fullId = 0;
		List<GetMembersFieldsResponse> members = getUsers(groupIdLikesLokal);
		for (GetMembersFieldsResponse getMembers : members) {
			users.addAll(getMembers.getItems());
		}
		int counter = 0;
		for (UserXtrRole user : users) {
			if (user.isOnline() && user.hasPhoto()) {
				counter++;
				fullId = parsePhotoId(vk.users().get().userIds(String.valueOf(user.getId())).fields(UserField.PHOTO_ID)
						.execute().get(0).getPhotoId());
				TimeUnit.MILLISECONDS.sleep(350);
				if (vk.likes().isLiked(actor, LikesType.PHOTO, fullId).ownerId(user.getId()).execute().isLiked()) {
					logs.appendText("Фото " + user.getId() + "_" + fullId + " уже було пролайкано раніше...\n");
				} else {
					try {
						vk.likes().add(actor, LikesType.PHOTO, fullId).ownerId(user.getId()).execute();
						logs.appendText("Фото " + user.getId() + "_" + " успішно пролайкано:)\n");
					} catch (ApiCaptchaException e) {
						captchaSid = e.getSid();
						captchaImg = e.getImage();
					}

					if (captchaImg != null) {
						loadCaptchaView(captchaImg);
						vk.likes().add(actor, LikesType.PHOTO, fullId).ownerId(user.getId()).captchaSid(captchaSid)
								.captchaKey(captchaKey).execute();
						logs.appendText("Фото " + user.getId() + "_" + " успішно пролайкано:)\n");
						captchaImg = null;
					}
				}
				TimeUnit.MILLISECONDS.sleep(350);
			}
		}
		logs.appendText("пролайканих фото: " + counter + "\n");
	}

	private void loadCaptchaView(String captchaImage) {
		try {
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("/logo.png")));
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/CaptchaView.fxml"));
			AnchorPane parent = (AnchorPane) loader.load();
			Scene scene = new Scene(parent);
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int parsePhotoId(String photoId) {
		int result = 0;
		String[] words = photoId.split("_");
		return Integer.valueOf(words[1]);
	}

	private List<GetMembersFieldsResponse> getUsers(String groupIdLikesLokal)
			throws ApiException, ClientException, InterruptedException {
		UserField[] fields = { UserField.ONLINE, UserField.HAS_PHOTO };
		List<GetMembersFieldsResponse> users = new ArrayList<>();
		int countOfUsers = vk.groups().getMembers(fields).groupId(groupIdLikesLokal).execute().getCount();
		logs.appendText("Кількість підписників в спільноті: " + countOfUsers + "\n");
		for (int i = 0; i <= countOfUsers / 1000; i++) {
			users.add(vk.groups().getMembers(fields).offset(i * 1000).groupId(groupIdLikesLokal).execute());
			if (vk.groups().getMembers(fields).offset(i * 1000).groupId(groupIdLikesLokal).execute().getItems()
					.size() < 1000) {
				break;
			}
		}
		return users;
	}

	private void getPostponedTimes(UserActor actor, int ownerId, int advId, VkApiClient vk,
			List<WallpostAttachment> attachments) throws ApiException, ClientException, InterruptedException {
		GetResponse getResponse = vk.wall().get(actor).ownerId(ownerId).count(100).filter(WallGetFilter.POSTPONED)
				.execute();
		List<Integer> listOfPostponedTimes = new ArrayList<>();
		for (int i = 0; i < getResponse.getCount(); i++) {
			listOfPostponedTimes.add(getResponse.getItems().get(i).getDate().intValue());
		}
		setAdOnTimer(listOfPostponedTimes, actor, ownerId, vk, advId, attachments);
	}

	private void setAdOnTimer(List<Integer> listOfPostponedTimes, UserActor actor, int ownerId, VkApiClient vk,
			int advId, List<WallpostAttachment> attachments)
			throws ApiException, ClientException, InterruptedException {
		if (listOfPostponedTimes.size() != 0) {
			for (int int1 : listOfPostponedTimes) {
				if (int1 != (currentTime + 1200)) {
					if (attachments == null) {
						currentTime += 1200;
						vk.wall().post(actor).ownerId(ownerId).signed(true).postId(advId).publishDate((int) currentTime)
								.execute();
						String m5 = " ,буде опубліковано о: " + getNormalTime(currentTime);
						logs.appendText(m5);
						TimeUnit.MILLISECONDS.sleep(350);
						break;
					} else {
						currentTime += 1200;
						vk.wall().post(actor).ownerId(ownerId).signed(true).attachments(getPhotoList(attachments))
								.postId(advId).publishDate((int) currentTime).execute();
						String m5 = " ,буде опубліковано о: " + getNormalTime(currentTime);
						logs.appendText(m5);
						TimeUnit.MILLISECONDS.sleep(350);
						break;
					}
				}
			}
		} else {
			if (attachments == null) {
				currentTime += 1200;
				vk.wall().post(actor).ownerId(ownerId).signed(true).postId(advId).publishDate((int) currentTime)
						.execute();
				String m5 = " ,буде опубліковано о: " + getNormalTime(currentTime);
				logs.appendText(m5);
			} else {
				currentTime += 1200;
				vk.wall().post(actor).ownerId(ownerId).signed(true).attachments(getPhotoList(attachments)).postId(advId)
						.publishDate((int) currentTime).execute();
				String m5 = " ,буде опубліковано о: " + getNormalTime(currentTime);
				logs.appendText(m5);
			}
		}
	}

	private List<String> getPhotoList(List<WallpostAttachment> attachments) {
		List<String> photos = new ArrayList<>();
		String attachId;

		for (int i = 0; i < attachments.size(); i++) {
			attachId = "photo" + attachments.get(i).getPhoto().getOwnerId() + "_"
					+ attachments.get(i).getPhoto().getId();
			photos.add(attachId);
		}
		return photos;
	}

	public static long getCurrentTime() {
		Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+02:00"));
		return calendar.getTimeInMillis() / 1000L;
	}

	private static String getNormalTime(long unixSeconds) {
		Date date = new Date(unixSeconds * 1000L); // *1000 is to convert
													// seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the
																				// format
																				// of
																				// your
																				// date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+02:00")); // give a timezone
															// reference for
															// formating
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

	private static boolean checkForCurrent(String textOfAd, Pattern p) {
		Matcher m = p.matcher(textOfAd);
		return m.matches();
	}

	private String parseTextForMatches(String textOfAd, Pattern p) {
		String res = "";
		Matcher m = p.matcher(textOfAd);
		if (m.find()) {
			res = m.group(1);
			return res.replaceAll(" ", "").toLowerCase();
		}
		return res;
	}

	private String getCurrentHashTag(String textOfAd) {
		String match = parseTextForMatches(textOfAd, findKeyWords);
		String result = "";
		if (match.equalsIgnoreCase("earpods")) {
			result = "#accesories@apple_lb";
		} else if (!match.isEmpty()) {
			result = "#" + match + "@apple_lb";
		}
		return result;
	}

	private String parseAccessToken(String tokenUrl) {
		String token = null;
		Pattern p = Pattern.compile("#access_token=(.*?)&");
		Matcher m = p.matcher(tokenUrl);
		if (m.find()) {
			token = m.group(1);
			return token;
		}
		return token;
	}

	private int parseIdOFUser(String tokenUrl) {
		int id = 0;
		Pattern p = Pattern.compile("user_id=(.*?)&");
		Matcher m = p.matcher(tokenUrl);
		if (m.find()) {
			id = Integer.parseInt(m.group(1));
			return id;
		}

		return id;
	}

}
