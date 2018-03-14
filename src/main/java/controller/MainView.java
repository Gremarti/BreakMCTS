package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.ai.AI;
import model.ai.mcts.MCTS;
import model.ai.random.Random;
import model.board.Board;
import model.board.Move;
import model.board.Position;
import model.board.utils.PawnColor;

import java.io.IOException;
import java.util.List;

public class MainView {

	@FXML private VBox vbox;
	@FXML private GridPane gridpane;

	private Pane[][] tiles = new Pane[Board.SIZEX][Board.SIZEY];

	private Board board = new Board();
	private PawnColor playerColor = PawnColor.BLACK;
	private PawnColor aiColor = PawnColor.WHITE;

	private Stage mainStage;

	public MainView(Stage mainStage){
		this.mainStage = mainStage;
		Border borderPane = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1)));

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				Pane pane = new Pane();
				pane.setBorder(borderPane);
				pane.setStyle("-fx-background-color: grey");

				tiles[i][j] = pane;
			}
		}

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				Pane tile = tiles[i][j];

				gridpane.add(tile, j, i);
				tile.prefHeightProperty().bind(gridpane.getRowConstraints().get(0).prefHeightProperty());
				tile.prefWidthProperty().bind(gridpane.getColumnConstraints().get(0).prefWidthProperty());
			}
		}

		fillPawns();

		mainStage.setScene(new Scene(vbox));
		mainStage.setTitle("BreakMCTS");
		mainStage.show();

		Thread thread = new Thread(this::play);
		thread.setDaemon(true);
		thread.start();
	}

	private void fillPawns(){
		List<Position> blackPawns = board.getBlacks();
		List<Position> whitePawns = board.getWhites();

		for (Pane[] rowTile : tiles) {
			for (Pane tile : rowTile) {
				Platform.runLater(() -> tile.getChildren().clear());
			}
		}

		blackPawns.forEach(position -> {
			Circle pawn = new Circle(10, Color.BLACK);
			Pane tile = tiles[position.x][position.y];

			pawn.setCenterX(tile.getPrefWidth()/2);
			pawn.setCenterY(tile.getPrefHeight()/2);
			Platform.runLater(() -> tile.getChildren().add(pawn));
		});

		whitePawns.forEach(position -> {
			Circle pawn = new Circle(10, Color.WHITE);
			Pane tile = tiles[position.x][position.y];

			pawn.setCenterX(tile.getPrefWidth()/2);
			pawn.setCenterY(tile.getPrefHeight()/2);
			Platform.runLater(() -> tile.getChildren().add(pawn));
		});

	}

	public void play(){
		AI aiWhite = new MCTS(PawnColor.WHITE);
		AI aiBlack = new MCTS(PawnColor.BLACK);


		while (!board.isFinished()){
			Move aiMove = aiWhite.getAIMove(board);
			board.movePawn(aiMove);
			fillPawns();

			if(board.isFinished()){
				break;
			}

			Move aiRandomMove = aiBlack.getAIMove(board);
			board.movePawn(aiRandomMove);
			fillPawns();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.err.println("Finished");
	}
}
