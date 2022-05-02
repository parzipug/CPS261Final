package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameOfPig extends Application {
	HBox pane;
	BorderPane mainPane;
	VBox leftPane, rightPane;
	FlowPane leftButtons, rightButtons;
	List<Image> dice;
	ImageView leftDice, rightDice;
	Text rightTotal, leftTotal;
	Button rollL, rollR, holdL, holdR;
	Player p1, p2;
	TableView<Player> table;
	Arrow arrowL, arrowR;
	
	int roundTotalL = 0, roundTotalR = 0;
	Boolean otherEntered;

	class Arrow extends Group {
		Arrow() {
			super();
			Rectangle arrowBottom = new Rectangle(20, 60, Color.BLUE);
			Polygon arrowTop = new Polygon(new double[] { -30.0, 20.0, 10.0, -30.0, 50.0, 20.0 });

			arrowTop.setFill(Color.BLUE);
			arrowTop.setScaleX(0.9);
			arrowTop.setScaleY(0.9);
			this.getChildren().addAll(arrowBottom, arrowTop);
			this.setScaleX(0.7);
			this.setScaleY(0.7);
		}
	}

	Scene winGUI() {
		Text winner = new Text();
		winner.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 40));
		if (p1.getResult().equals("Won"))
			winner.setText(p1.getName() + " won!");
		else
			winner.setText(p2.getName() + " won!");
		mainPane = new BorderPane();
		mainPane.setCenter(winner);

		return new Scene(mainPane, 300, 300);
	}

	Scene gameGUI(Stage stage) {
		pane = new HBox();
		mainPane = new BorderPane();
		mainPane.setCenter(pane);
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(20));
		pane.setSpacing(20);

		leftPane = new VBox();
		leftPane.setAlignment(Pos.CENTER);
		rightPane = new VBox();
		rightPane.setAlignment(Pos.CENTER);

		leftButtons = new FlowPane();
		leftButtons.setPadding(new Insets(15));
		leftButtons.setHgap(15);
		leftButtons.setAlignment(Pos.CENTER);
		rightButtons = new FlowPane();
		rightButtons.setPadding(new Insets(15));
		rightButtons.setHgap(15);
		rightButtons.setAlignment(Pos.CENTER);

		dice = new ArrayList<>();
		for (int i = 1; i < 7; i++) {
			dice.add(new Image("file:dice/dice" + i + ".png"));
		}

		leftDice = new ImageView(dice.get(0));
		rightDice = new ImageView(dice.get(0));

		leftTotal = new Text("Total: 0");
		rightTotal = new Text("Total: 0");

		rollL = new Button("Roll");
		rollR = new Button("Roll");
		holdL = new Button("Hold");
		holdR = new Button("Hold");

		rollR.setOpacity(.5f);
		rollR.setDisable(true);
		holdL.setOpacity(.5f);
		holdL.setDisable(true);
		holdR.setOpacity(.5f);
		holdR.setDisable(true);

		arrowL = new Arrow();
		arrowR = new Arrow();
		arrowR.setVisible(false);

		leftButtons.getChildren().addAll(rollL, holdL);
		rightButtons.getChildren().addAll(rollR, holdR);
		leftPane.getChildren().addAll(leftDice, leftTotal, leftButtons, arrowL);
		rightPane.getChildren().addAll(rightDice, rightTotal, rightButtons, arrowR);

		pane.getChildren().addAll(leftPane, rightPane);

		gameLogic(stage);
		return new Scene(mainPane, 400, 400);
	}
	
	void gameLogic(Stage stage) {
		Random rand = new Random();
		rollL.setOnMouseClicked(e -> {
			int roll = Math.abs((rand.nextInt() % 6)) + 1;
			if (roll != 1) {
				holdL.setDisable(false);
				holdL.setOpacity(1);
				leftDice.setImage(dice.get(roll - 1));
				roundTotalL += roll;
				holdL.setText("Hold +" + roundTotalL);
			} else {
				holdL.setText("Hold");
				roundTotalL = 0;
				leftDice.setImage(dice.get(roll - 1));
				leftEnd();
			}

		});

		holdR.setOnMouseClicked(e -> {
			holdR.setText("Hold");
			p2.setPoints(p2.getPoints() + roundTotalR);
			roundTotalR = 0;
			rightTotal.setText("Total: " + p2.getPoints());
			if (p2.getPoints() >= 100) {
				p2.setResult("Won");
				p1.setResult("Lost");
				writeToHistorySheet();
				stage.setScene(winGUI());
			}
			rightEnd();
		});

		rollR.setOnMouseClicked(e -> {
			int roll = Math.abs((rand.nextInt() % 6)) + 1;
			if (roll != 1) {
				holdR.setDisable(false);
				holdR.setOpacity(1);
				rightDice.setImage(dice.get(roll - 1));
				roundTotalR += roll;
				holdR.setText("Hold +" + roundTotalR);
			} else {
				holdR.setText("Hold");
				roundTotalR = 0;
				rightDice.setImage(dice.get(roll - 1));
				rightEnd();
			}

		});

		holdL.setOnMouseClicked(e -> {
			holdL.setText("Hold");
			p1.setPoints(p1.getPoints() + roundTotalL);
			roundTotalL = 0;
			leftTotal.setText("Total: " + p1.getPoints());
			if (p1.getPoints() >= 100) {
				p1.setResult("Won");
				p2.setResult("Lost");
				writeToHistorySheet();
				stage.setScene(winGUI());
			}
			leftEnd();
		});
	}

	void leftEnd() {
		rollL.setDisable(true);
		rollL.setOpacity(.5f);
		rollR.setDisable(false);
		rollR.setOpacity(1);
		arrowL.setVisible(false);
		arrowR.setVisible(true);
		holdL.setDisable(true);
		holdL.setOpacity(.5f);
	}

	void rightEnd() {
		rollR.setDisable(true);
		rollR.setOpacity(.5f);
		rollL.setDisable(false);
		rollL.setOpacity(1);
		arrowL.setVisible(true);
		arrowR.setVisible(false);
		holdR.setDisable(true);
		holdR.setOpacity(.5f);
	}
	
	Scene startGameGUI(Stage stage) {
		TextField player1Name = new TextField("Type Player 1 Name Here");
		TextField player2Name = new TextField("Type Player 2 Name Here");
		Button enter1 = new Button("Enter");
		Button enter2 = new Button("Enter");

		otherEntered = false;

		enter1.setOnAction(e -> {
			String tmp = player1Name.getText().equals("Type Player 1 Name Here") ? "Anonomous1" : player1Name.getText();
			p1 = new Player(tmp, LocalDate.now());
			enter1.setDisable(true);
			if (otherEntered)
				stage.setScene(gameGUI(stage));
			else
				otherEntered = true;
		});

		enter2.setOnAction(e -> {
			String tmp = player2Name.getText().equals("Type Player 2 Name Here") ? "Anonomous2" : player2Name.getText();
			p2 = new Player(tmp, LocalDate.now());
			enter2.setDisable(true);
			if (otherEntered)
				stage.setScene(gameGUI(stage));
			else
				otherEntered = true;
		});

		leftPane = new VBox();
		leftPane.setAlignment(Pos.CENTER);
		leftPane.setSpacing(10);
		leftPane.getChildren().addAll(player1Name, enter1);
		rightPane = new VBox();
		rightPane.setAlignment(Pos.CENTER);
		rightPane.setSpacing(10);
		rightPane.getChildren().addAll(player2Name, enter2);

		pane = new HBox();
		pane.setAlignment(Pos.CENTER);
		pane.setSpacing(20);
		pane.getChildren().addAll(leftPane, rightPane);

		return new Scene(pane, 400, 200);
	}

	Scene startGUI(Stage stage) {
		Button startNew = new Button("Start a New Game");
		Button history = new Button("Look up History Records");
		FlowPane startButtons = new FlowPane(startNew, history);
		startButtons.setPadding(new Insets(12));
		startButtons.setHgap(12);
		startButtons.setAlignment(Pos.CENTER);

		startNew.setOnAction(e -> {
			stage.setScene(startGameGUI(stage));
		});

		history.setOnAction(e -> {
			stage.setScene(historyGUI());
		});

		Text title = new Text("Game Of Pig");
		title.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 40));

		mainPane = new BorderPane();
		mainPane.setCenter(title);
		mainPane.setBottom(startButtons);

		return new Scene(mainPane, 350, 130);
	}

	Scene historyGUI() {
		leftPane = new VBox();
		leftPane.setAlignment(Pos.CENTER);
		leftPane.setPadding(new Insets(15));
		leftPane.setSpacing(10);
		table = new TableView<>();
		ObservableList<Player> list = loadHistorySheet();
		table.setItems(list);
		table.setMinHeight(200);
		makeColumns();
		TextArea totals = new TextArea();

		List<String> playerNames = list.stream().map(e -> e.getName()).distinct().toList();

		Iterator<String> players = playerNames.iterator();
		while (players.hasNext()) {
			String tmp = players.next();
			totals.appendText(tmp + " has won " + Long.toString(
					list.stream().filter(e -> e.getName().equals(tmp)).filter(e -> e.getResult().equals("Won")).count())
					+ " times\n");
		}

		leftPane.getChildren().addAll(table, totals);

		return new Scene(leftPane, 800, 400);
	}

	void makeColumns() {
		final int MIN_WIDTH = 200;
		// name Column creation
		TableColumn<Player, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setMinWidth(MIN_WIDTH);
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		// Object wrapper Date Column creation
		TableColumn<Player, LocalDate> dateColumn = new TableColumn<>("Date");
		dateColumn.setMinWidth(MIN_WIDTH);
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

		// result Column creation
		TableColumn<Player, Boolean> resultColumn = new TableColumn<>("Win or Lose");
		resultColumn.setMinWidth(MIN_WIDTH);
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));

		// points Column creation
		TableColumn<Player, Integer> pointsColumn = new TableColumn<>("Total Points");
		pointsColumn.setMinWidth(MIN_WIDTH);
		pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

		table.getColumns().add(nameColumn);
		table.getColumns().add(dateColumn);
		table.getColumns().add(resultColumn);
		table.getColumns().add(pointsColumn);
	}

	ObservableList<Player> loadHistorySheet() {
		Scanner input = null;
		try {
			input = new Scanner(new FileInputStream("historySheet.txt"));
		} catch (FileNotFoundException e) {
			System.err.println("loadHistorySheet(): can't find file!");
		}
		ObservableList<Player> tmp = FXCollections.observableArrayList();
		String line, name;
		int points, year, month, day;
		String result;
		Scanner lineScan;
		while (input.hasNextLine()) {
			line = input.nextLine();
			lineScan = new Scanner(line);
			lineScan.useDelimiter(";");

			name = lineScan.next();

			year = lineScan.nextInt();
			month = lineScan.nextInt();
			day = lineScan.nextInt();
			LocalDate tmpDate = LocalDate.of(year, month, day);

			result = lineScan.next();
			points = lineScan.nextInt();

			tmp.add(new Player(name, tmpDate, result, points));
		}
		return tmp;
	}

	void writeToHistorySheet() {
		try {
			FileWriter editor = new FileWriter("historySheet.txt", true);
			editor.write(p1.getName() + ";" + p1.getDate().getYear() + ";" + p1.getDate().getMonthValue() + ";"
					+ p1.getDate().getDayOfMonth() + ";" + p1.getResult() + ";" + p1.getPoints() + ";" + '\n');
			editor.write(p2.getName() + ";" + p2.getDate().getYear() + ";" + p2.getDate().getMonthValue() + ";"
					+ p2.getDate().getDayOfMonth() + ";" + p2.getResult() + ";" + p2.getPoints() + ";" + '\n');

			editor.close();
		} catch (IOException e) {
			System.err.println("writeToHistorySheet(): fileWrite error");
		}
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Game of Pig");
		primaryStage.setScene(startGUI(primaryStage));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}