package jmsChat;

import java.io.File;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class JMSChat extends Application{

	public static void main(String[] args) {
		Application.launch(JMSChat.class);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// create the interface
		primaryStage.setTitle("JMS CHAT");
		
		BorderPane pane = new BorderPane();
		
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(10));
		hBox.setSpacing(10);
		hBox.setBackground(new Background(new BackgroundFill(Color.ORANGE,CornerRadii.EMPTY,Insets.EMPTY)));
		
		Label labelCode = new Label("Code:");
		TextField textFieldCode = new TextField("C1");
		textFieldCode.setPromptText("Code");
		
		Label labelHost = new Label("Host:");
		TextField textFieldHost = new TextField("localhost");
		textFieldHost.setPromptText("Host");
		
		Label labelPort = new Label("Port:");
		TextField textFieldPort = new TextField("61616");
		textFieldPort.setPromptText("Port");
		
		Button connectionButton = new Button("Connextion");
		
		
		hBox.getChildren().add(labelCode);
		hBox.getChildren().add(textFieldCode);
		hBox.getChildren().add(labelHost);
		hBox.getChildren().add(textFieldHost);
		hBox.getChildren().add(labelPort);
		hBox.getChildren().add(textFieldPort);
		hBox.getChildren().add(connectionButton);
		
		pane.setTop(hBox);
		
		HBox hBoxButtom = new HBox();
		hBoxButtom.setBackground(new Background(new BackgroundFill(Color.ORANGE,CornerRadii.EMPTY,Insets.EMPTY)));
		Label labelStatus = new Label("");
		labelStatus.setStyle("-fx-text-fill: green;");
		labelStatus.setStyle("-fx-font-weight: bold");
		hBoxButtom.getChildren().add(labelStatus);
		
		pane.setBottom(hBoxButtom);
		
		VBox vBox = new VBox();
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		HBox hBoxCenter = new HBox();
		vBox.getChildren().add(gridPane);
		vBox.getChildren().add(hBoxCenter);
		pane.setCenter(vBox);
		
		Label labelTo = new Label("To");
		TextField textFieldTo = new TextField("C1");
		textFieldTo.setPrefWidth(350);
		textFieldTo.setPromptText("Message to ...");
		Label labelMessage = new Label("Message");
		TextArea textAreaMessage = new TextArea();
		textAreaMessage.setPrefWidth(350);
		textAreaMessage.setPrefRowCount(2);
		Button sendButton = new Button("Send");
		Label labelImage = new Label("Image");
		File file = new File("images");
		ObservableList<String> observableListCombo = FXCollections.observableArrayList(file.list());
		ComboBox<String> comboBoxImages = new ComboBox<String>(observableListCombo);
		comboBoxImages.getSelectionModel().select(0);
		Button sendImageButton = new Button("Send image");
		
		
		gridPane.add(labelTo, 0, 0);
		gridPane.add(textFieldTo, 1, 0);
		gridPane.add(labelMessage, 0, 1);
		gridPane.add(textAreaMessage, 1, 1);
		gridPane.add(sendButton, 2, 1);
		gridPane.add(labelImage, 0, 2);
		gridPane.add(comboBoxImages, 1, 2);
		gridPane.add(sendImageButton, 2	,2);
		
		Scene scene = new Scene(pane,850,600);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("chatMessage.png"));
		primaryStage.show(); 
		
		connectionButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String codeUser = textFieldCode.getText();
				String host = textFieldHost.getText();
				int port = Integer.parseInt(textFieldPort.getText());
				ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://"+host+":"+port);
				try {
					Connection connection = cf.createConnection();
					connection.start(); // important, to be able to recieve messages
					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
					Destination destination = session.createTopic("lyes.topic");
					MessageConsumer messageConsumer = session.createConsumer(destination);
					messageConsumer.setMessageListener(message -> {
						if(message instanceof TextMessage) {
							try {
								TextMessage textMessage = (TextMessage) message;
								System.out.println(textMessage.getText());
							} catch (JMSException e) {
								e.printStackTrace();
							}
						}else if(message instanceof StreamMessage) {
							
						}
					});
					labelStatus.setText("Connected to the server");
				} catch (JMSException e) {
					e.printStackTrace();
				}
				
			}
			
		});
	}

}