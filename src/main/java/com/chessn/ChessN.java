package com.chessn;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.stage.Window;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextBoundsType;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import com.chessn.controllers.WelcomeController;
import com.chessn.controllers.PlayerNamesController;
import com.chessn.controllers.VictoryController;
import com.chessn.controllers.StalemateController;
import com.chessn.controllers.MenuBarController;
import com.chessn.controllers.HowToPlayController;
import com.chessn.controllers.AboutController;
import com.chessn.controllers.PlayerInfoPanelBlackController;
import com.chessn.controllers.PlayerInfoPanelWhiteController;
import javafx.geometry.Point2D;
import com.chessn.controllers.PawnPromotionController;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import com.chessn.controllers.StockfishController;
import com.chessn.controllers.AISetupController;
import com.chessn.controllers.AISetupController.Difficulty;
import com.chessn.controllers.AISetupController.PlayerColor;
import java.util.concurrent.ThreadLocalRandom;
import com.chessn.controllers.GameModeDialogController;

public class ChessN extends Application {
    // ==================== Variables & Constants ====================
    // Game Model Instance
    private ChessGame game; 
    
    // GUI Elements and state local to the View/Controller
    private GridPane gridPane;        // The main board grid
    private static final int SQUARE_SIZE = 70;  // Size of each board square
    
    // Player names
    private String whitePlayerName = "Player 1";
    private String blackPlayerName = "Player 2";
    
    // Game state
    private boolean gameStarted = false;
    private boolean isVsAI = false;
    private boolean playerIsWhite = true;
    private int aiSkillLevel = 5; // Default medium
    private int aiDepth = 0; // Default no depth limit
    private int aiMultiPV = 1; // Default best move only
    
    // stockFish Controller
    private StockfishController stockfishController;
    
    // Color Constants (GUI-specific)
    private static final Color LIGHT_SQUARE = Color.web("fdfce8");    // Light squares on board
    private static final Color DARK_SQUARE = Color.web("dbb6ee");     // Dark squares on board
    private static final Color HIGHLIGHT_MOVE = Color.web("7f4ca5");  // Valid move highlight
    private static final Color HIGHLIGHT_CAPTURE = Color.web("111111"); // Capture highlight
    private static final Color CHECK_HIGHLIGHT = Color.web("ff4444"); // King in check highlight (changed from RED)
    private static final Color BUTTON_BG = Color.web("dbb6ee");      // Button background
    private static final Color BUTTON_TEXT = Color.web("734128");     // Button text
    private static final Color TITLE_TEXT = Color.web("864626");      // Title text
    private static final Color BACKGROUND = Color.web("f5f4d8");      // Window background (darker than before)
    private static final Color WHITE_PIECE = Color.web("391e10");     // White piece color
    private static final Color BLACK_PIECE = Color.web("391e10");     // Black piece color
    private static final Color TITLE_BAR_BG = Color.web("fff0ff");    // Title bar background
    private static final Color BORDER_COLOR = Color.web("dbb6ee");    // Border color for popups

    private PlayerInfoPanelBlackController blackPlayerController;
    private PlayerInfoPanelWhiteController whitePlayerController;

    private double dragOffsetX, dragOffsetY;
    private Text draggingPiece = null;
    private StackPane draggingFromSquare = null;

    // ==================== Setup and Initialization ====================
    public ChessN() {
        game = new ChessGame(); 
    }
    
    @Override
    public void start(Stage primaryStage) {
        showWelcomeScreen(primaryStage);
    }

   
    private void showWelcomeScreen(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WelcomeScreen.fxml"));
            Parent root = loader.load();
            
            WelcomeController controller = loader.getController();
            controller.setStage(primaryStage);
            controller.setCallback(new WelcomeController.WelcomeControllerCallback() {
                @Override
                public void onStartNewGame() {
                    showPlayerNamesDialog(primaryStage);
                }
                
                @Override
                public void onStartAI() {
                    showAISetupDialog(primaryStage);
                }
                
                @Override
                public void onHowToPlay() {
                    showHowToPlayDialog(primaryStage);
                }
                
                @Override
                public void onAbout() {
                    showAboutDialog(primaryStage);
                }
            });
            
            // Setup stage
            primaryStage.setTitle("ChessMate");
            
            // Set the application icon
            primaryStage.getIcons().add(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
            
            primaryStage.initStyle(StageStyle.UNDECORATED);
            
            // Make window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = primaryStage.getX() - e.getScreenX();
                dragDelta.y = primaryStage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                primaryStage.setX(e.getScreenX() + dragDelta.x);
                primaryStage.setY(e.getScreenY() + dragDelta.y);
            });
            
            // Show the welcome screen
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Show dialog to enter player names
     */
    private void showPlayerNamesDialog(Stage primaryStage) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlayerNamesDialog.fxml"));
            Parent root = loader.load();
            
            // Get controller
            PlayerNamesController controller = loader.getController();
            controller.setFromGame(gameStarted);
            controller.setDefaultNames("Player 1", "Player 2");
            controller.setCallback(new PlayerNamesController.PlayerNamesControllerCallback() {
                @Override
                public void onStartGame(String whitePlayerName, String blackPlayerName) {
                    ChessN.this.whitePlayerName = whitePlayerName;
                    ChessN.this.blackPlayerName = blackPlayerName;
                    
                    // Check if we're starting from welcome screen or menu bar
                    if (gameStarted) {
                        // From menu bar - reset current game
                        game.resetGame();
                        updatePlayerInfo();
                        updateBoard();
                        System.out.println("New game started! " + whitePlayerName + " vs " + blackPlayerName);
                    } else {
                        // From welcome screen - create new chess game
                        startChessGame(primaryStage);
                    }
                }
                
                @Override
                public void onBack() {
                    // Do nothing for now, just close the dialog
                }
            });
            
            // Create stage
            Stage namesStage = new Stage();
            namesStage.initStyle(StageStyle.UNDECORATED);
            namesStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            controller.setStage(namesStage);
            
            // Make window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = namesStage.getX() - e.getScreenX();
                dragDelta.y = namesStage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                namesStage.setX(e.getScreenX() + dragDelta.x);
                namesStage.setY(e.getScreenY() + dragDelta.y);
            });
            
            // Show the dialog
            Scene scene = new Scene(root, 400, 250);
            namesStage.setScene(scene);
            namesStage.show();
            namesStage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Show dialog to set up AI game
     */
    private void showAISetupDialog(Stage primaryStage) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AISetupDialog.fxml"));
            Parent root = loader.load();

            // Get controller
            AISetupController controller = loader.getController();
            controller.setCallback(new AISetupController.AISetupCallback() {
                @Override
                public void onStart(Difficulty difficulty, PlayerColor color) {
                    isVsAI = true;
                    aiSkillLevel = difficulty.level;
                    aiDepth = difficulty.depth;
                    aiMultiPV = difficulty.multiPV;
                    
                    if (color == PlayerColor.RANDOM) {
                        playerIsWhite = ThreadLocalRandom.current().nextBoolean();
                    } else {
                        playerIsWhite = (color == PlayerColor.WHITE);
                    }

                    if (playerIsWhite) {
                        whitePlayerName = "You";
                        blackPlayerName = "Stockfish";
                    } else {
                        whitePlayerName = "Stockfish";
                        blackPlayerName = "You";
                    }
                    
                    startChessGame(primaryStage);
                }

                @Override
                public void onBack() {
                    // Do nothing, just closes dialog
                }
            });

            // Create stage
            Stage setupStage = new Stage();
            setupStage.initStyle(StageStyle.UNDECORATED);
            setupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            controller.setStage(setupStage);

            // Make window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = setupStage.getX() - e.getScreenX();
                dragDelta.y = setupStage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                setupStage.setX(e.getScreenX() + dragDelta.x);
                setupStage.setY(e.getScreenY() + dragDelta.y);
            });

            // Show the dialog
            Scene scene = new Scene(root);
            setupStage.setScene(scene);
            setupStage.show();
            setupStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Start the actual chess game
     */
    private void startChessGame(Stage primaryStage) {
        // Create a new stage for the chess game
        Stage chessStage = new Stage();
        chessStage.setTitle("ChessMate");
        chessStage.initStyle(StageStyle.UNDECORATED);
        
        // Create and setup the board grid
        gridPane = new GridPane();
        gridPane.getStyleClass().add("chess-board");
        gridPane.setPrefSize(SQUARE_SIZE * 8, SQUARE_SIZE * 8);
        gridPane.setMinSize(SQUARE_SIZE * 8, SQUARE_SIZE * 8);
        gridPane.setMaxSize(SQUARE_SIZE * 8, SQUARE_SIZE * 8);

        // Remove padding around the board

        // Create player info panels
        HBox blackPlayerPanel;
        HBox whitePlayerPanel;
        try {
            // Load black player panel (above board)
            FXMLLoader blackPlayerLoader = new FXMLLoader(getClass().getResource("/fxml/PlayerInfoPanelBlack.fxml"));
            blackPlayerPanel = blackPlayerLoader.load();
            blackPlayerController = blackPlayerLoader.getController();
            
            // Load white player panel (below board)
            FXMLLoader whitePlayerLoader = new FXMLLoader(getClass().getResource("/fxml/PlayerInfoPanelWhite.fxml"));
            whitePlayerPanel = whitePlayerLoader.load();
            whitePlayerController = whitePlayerLoader.getController();
            
            // Set player names
            blackPlayerController.setPlayerName(blackPlayerName);
            whitePlayerController.setPlayerName(whitePlayerName);
            
            // Load CSS
            blackPlayerPanel.getStylesheets().add(getClass().getResource("/css/PlayerInfoPanel.css").toExternalForm());
            whitePlayerPanel.getStylesheets().add(getClass().getResource("/css/PlayerInfoPanel.css").toExternalForm());
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load player info panels FXML", e);
        }
        
        // Create menu bar
        MenuBar menuBar = null;
        try {
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/fxml/MenuBar.fxml"));
            menuBar = menuLoader.load();
            
            // Get controller and set callbacks
            MenuBarController menuController = menuLoader.getController();
            menuController.setCallback(new MenuBarController.MenuBarCallback() {
                @Override
                public void onNewGame() {
                    showGameModeDialog(chessStage);
                }
                
                @Override
                public void onReset() {
                    game.resetGame();
                    gameStarted = true;
                    updatePlayerInfo(blackPlayerController, whitePlayerController, chessStage);
                    updateBoard();
                }
                
                @Override
                public void onBackToWelcome() {
                    goBackToWelcomeScreen(chessStage);
                }
                
                @Override
                public void onHowToPlay() {
                    showHowToPlayDialog(chessStage);
                }
                
                @Override
                public void onAbout() {
                    showAboutDialog(chessStage);
                }
                
                @Override
                public void onUndo() {
                    handleUndo(chessStage);
                }
                
                @Override
                public void onRedo() {
                    handleRedo(chessStage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateBoard();

        // Create custom title bar
        Text titleText = new Text("ChessMate");
        titleText.setFont(Font.font("DynaPuff", 20));
        titleText.setFill(TITLE_TEXT);
        
        // Create close button
        Button closeButton = new Button("×");
        closeButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 20; -fx-cursor: hand;",
            BUTTON_BG.toString().replace("0x", "#"),
            BUTTON_TEXT.toString().replace("0x", "#")));
        closeButton.setPadding(new Insets(-3, 6, -1, 6));
        closeButton.setOnAction(e -> {
            if (isVsAI && stockfishController != null) {
                stockfishController.stopEngine();
            }
            chessStage.close();
        });
        
        // Add hover effects for close button
        closeButton.setOnMouseEntered(e -> {
            closeButton.setStyle(String.format("-fx-background-color: #ff6b6b; -fx-text-fill: %s; -fx-font-size: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);",
                BUTTON_TEXT.toString().replace("0x", "#")));
        });
        closeButton.setOnMouseExited(e -> {
            closeButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 20; -fx-cursor: hand;",
                BUTTON_BG.toString().replace("0x", "#"),
                BUTTON_TEXT.toString().replace("0x", "#")));
        });
        
        // Create minimize button
        Button minimizeButton = new Button("–");
        minimizeButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 22; -fx-cursor: hand;",
            BUTTON_BG.toString().replace("0x", "#"),
            BUTTON_TEXT.toString().replace("0x", "#")));
        minimizeButton.setPadding(new Insets(-4, 7, -1, 7));
        minimizeButton.setOnAction(e -> chessStage.setIconified(true));
        
        // Add hover effects for minimize button
        minimizeButton.setOnMouseEntered(e -> {
            minimizeButton.setStyle(String.format("-fx-background-color: #c8a0e0; -fx-text-fill: %s; -fx-font-size: 22; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);",
                BUTTON_TEXT.toString().replace("0x", "#")));
        });
        minimizeButton.setOnMouseExited(e -> {
            minimizeButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 22; -fx-cursor: hand;",
                BUTTON_BG.toString().replace("0x", "#"),
                BUTTON_TEXT.toString().replace("0x", "#")));
        });

        // Create resize button
        Button resizeButton = new Button("❐");
        resizeButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 17; -fx-cursor: hand;",
            BUTTON_BG.toString().replace("0x", "#"),
            BUTTON_TEXT.toString().replace("0x", "#")));
        resizeButton.setPadding(new Insets(0, 5, 0, 4));
        resizeButton.setOnAction(e -> {
            if (chessStage.isMaximized()) {
                chessStage.setMaximized(false);
            } else {
                // Get the screen bounds excluding the taskbar
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                chessStage.setX(screenBounds.getMinX());
                chessStage.setY(screenBounds.getMinY());
                chessStage.setWidth(screenBounds.getWidth());
                chessStage.setHeight(screenBounds.getHeight());
                chessStage.setMaximized(true);
            }
        });
        
        // Add hover effects for resize button
        resizeButton.setOnMouseEntered(e -> {
            resizeButton.setStyle(String.format("-fx-background-color: #c8a0e0; -fx-text-fill: %s; -fx-font-size: 17; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);",
                BUTTON_TEXT.toString().replace("0x", "#")));
        });
        resizeButton.setOnMouseExited(e -> {
            resizeButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 17; -fx-cursor: hand;",
                BUTTON_BG.toString().replace("0x", "#"),
                BUTTON_TEXT.toString().replace("0x", "#")));
        });

        // Title bar container
        HBox titleBar = new HBox(titleText, new Region(), minimizeButton, resizeButton, closeButton);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(5, 10, 6, 10));
        titleBar.setStyle(String.format("-fx-background-color: %s;", TITLE_BAR_BG.toString().replace("0x", "#")));
        HBox.setHgrow(titleBar.getChildren().get(1), Priority.ALWAYS);
        
        // margin between buttons
        HBox.setMargin(minimizeButton, new Insets(0, 4, 0, 0));
        HBox.setMargin(resizeButton, new Insets(0, 4, 1, 0));
        HBox.setMargin(closeButton, new Insets(0, 0, 1, 0));

        // Make window draggable
        final Delta dragDelta = new Delta();
        titleBar.setOnMousePressed(e -> {
            dragDelta.x = chessStage.getX() - e.getScreenX();
            dragDelta.y = chessStage.getY() - e.getScreenY();
        });
        titleBar.setOnMouseDragged(e -> {
            chessStage.setX(e.getScreenX() + dragDelta.x);
            chessStage.setY(e.getScreenY() + dragDelta.y);
        });

        // Main container with menu bar and player info panels
        VBox root = new VBox(); // Remove spacing
        root.setAlignment(Pos.TOP_CENTER); // Center content horizontally
        
        // Create centered containers for player panels
        HBox blackPlayerContainer = new HBox(blackPlayerPanel);
        blackPlayerContainer.setAlignment(Pos.CENTER);
        blackPlayerContainer.setStyle(String.format("-fx-background-color: %s;", BACKGROUND.toString().replace("0x", "#")));
        
        HBox whitePlayerContainer = new HBox(whitePlayerPanel);
        whitePlayerContainer.setAlignment(Pos.CENTER);
        whitePlayerContainer.setStyle(String.format("-fx-background-color: %s;", BACKGROUND.toString().replace("0x", "#")));
        
        // Set player panels to match board width
        blackPlayerPanel.setPrefWidth(8 * SQUARE_SIZE);
        whitePlayerPanel.setPrefWidth(8 * SQUARE_SIZE);
        
        // Create a centered container for the chess board
        HBox boardContainer = new HBox(gridPane);
        boardContainer.setAlignment(Pos.CENTER);
        boardContainer.setStyle(String.format("-fx-background-color: %s;", BACKGROUND.toString().replace("0x", "#")));
        
        root.getChildren().addAll(titleBar, menuBar, blackPlayerContainer, boardContainer, whitePlayerContainer);
        root.setStyle(String.format("-fx-background-color: %s;", BACKGROUND.toString().replace("0x", "#")));

        Scene scene = new Scene(root); // Remove fixed size - let it be responsive
        
        // Load CSS for chess board animations
        scene.getStylesheets().add(getClass().getResource("/css/chess-board.css").toExternalForm());
        
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.LEFT) {
                handleUndo(chessStage);
                event.consume();
            }
            if (event.getCode() == javafx.scene.input.KeyCode.RIGHT) {
                handleRedo(chessStage);
                event.consume();
            }
        });
        
        chessStage.setScene(scene);
        chessStage.show();
        
        // Start the game
        gameStarted = true;
        game.resetGame();
        if (isVsAI) {
            stockfishController = new StockfishController();
        }
        updatePlayerInfo(blackPlayerController, whitePlayerController, chessStage);
        updateBoard();
        System.out.println("Game started! " + whitePlayerName + " vs " + blackPlayerName);
        
        // If AI is white, trigger its first move
        if (isVsAI && !playerIsWhite) {
            triggerAIMove();
        }
        
        // Close the welcome screen
        primaryStage.close();
    }

    /**
     * Go back to welcome screen from the game
     */
    private void goBackToWelcomeScreen(Stage chessStage) {
        try {
            // Create a new welcome screen stage
            Stage welcomeStage = new Stage();
            welcomeStage.setTitle("ChessMate");
            welcomeStage.initStyle(StageStyle.UNDECORATED);
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WelcomeScreen.fxml"));
            Parent root = loader.load();
            
            // Get controller
            WelcomeController controller = loader.getController();
            controller.setStage(welcomeStage);
            controller.setCallback(new WelcomeController.WelcomeControllerCallback() {
                @Override
                public void onStartNewGame() {
                    showPlayerNamesDialog(welcomeStage);
                }
                
                @Override
                public void onStartAI() {
                    showAISetupDialog(welcomeStage);
                }
                
                @Override
                public void onHowToPlay() {
                    showHowToPlayDialog(welcomeStage);
                }
                
                @Override
                public void onAbout() {
                    showAboutDialog(welcomeStage);
                }
            });
            
            // Make window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = welcomeStage.getX() - e.getScreenX();
                dragDelta.y = welcomeStage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                welcomeStage.setX(e.getScreenX() + dragDelta.x);
                welcomeStage.setY(e.getScreenY() + dragDelta.y);
            });
            
            // Show the welcome screen
            Scene scene = new Scene(root);
            welcomeStage.setScene(scene);
            welcomeStage.show();
            welcomeStage.centerOnScreen();
            
            // Close the chess game stage
            chessStage.close();

            if (isVsAI && stockfishController != null) {
                stockfishController.stopEngine();
                isVsAI = false;
            }
            
            // Reset game state
            gameStarted = false;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show How to Play dialog
     */
    private void showHowToPlayDialog(Stage primaryStage) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HowToPlayDialog.fxml"));
            Parent root = loader.load();
            
            // Get controller
            HowToPlayController controller = loader.getController();
            
            // Create stage
            Stage helpStage = new Stage();
            helpStage.initStyle(StageStyle.UNDECORATED);
            helpStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            controller.setStage(helpStage);
            
            // Make window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = helpStage.getX() - e.getScreenX();
                dragDelta.y = helpStage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                helpStage.setX(e.getScreenX() + dragDelta.x);
                helpStage.setY(e.getScreenY() + dragDelta.y);
            });
            
            // Show the dialog
            Scene scene = new Scene(root);
            helpStage.setScene(scene);
            helpStage.show();
            helpStage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show About dialog
     */
    private void showAboutDialog(Stage primaryStage) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AboutDialog.fxml"));
            Parent root = loader.load();
            
            // Get controller
            AboutController controller = loader.getController();
            
            // Create stage
            Stage aboutStage = new Stage();
            aboutStage.initStyle(StageStyle.UNDECORATED);
            aboutStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            controller.setStage(aboutStage);
            
            // Make window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = aboutStage.getX() - e.getScreenX();
                dragDelta.y = aboutStage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                aboutStage.setX(e.getScreenX() + dragDelta.x);
                aboutStage.setY(e.getScreenY() + dragDelta.y);
            });
            
            // Show the dialog
            Scene scene = new Scene(root);
            aboutStage.setScene(scene);
            aboutStage.show();
            aboutStage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the board display
     */
    private void updateBoard() {
        System.out.println("Updating board...");
        gridPane.getChildren().clear();
        
        // Create and add squares for each board position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = createSquare(row, col);
                gridPane.add(square, col, row);
            }
        }
        
        // Update player info panels if controllers are available
        if (blackPlayerController != null && whitePlayerController != null) {
            Window window = gridPane.getScene() != null ? gridPane.getScene().getWindow() : null;
            Stage chessStage = window instanceof Stage ? (Stage) window : null;
            updatePlayerInfo(blackPlayerController, whitePlayerController, chessStage);
        }
    }
    
    /**
     * Create a square for the chess board
     */
    private StackPane createSquare(final int row, final int col) {
        StackPane square = new StackPane();
        // Add CSS class for animations
        square.getStyleClass().add("chess-square");
        
        // Explicitly set the size of the square to prevent layout issues
        square.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
        square.setMinSize(SQUARE_SIZE, SQUARE_SIZE);
        square.setMaxSize(SQUARE_SIZE, SQUARE_SIZE);
        // Clip content to ensure it stays within the square's bounds
        square.setClip(new Rectangle(SQUARE_SIZE, SQUARE_SIZE));
        
        // Create and color the square
        Rectangle background = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
        background.setFill((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);

        // Highlight last move
        Move lastMove = game.getLastMove();
        if (lastMove != null) {
            if ((lastMove.fromRow == row && lastMove.fromCol == col) ||
                (lastMove.toRow == row && lastMove.toCol == col)) {
                background.getStyleClass().add("last-move");
            }
        }
        
        // Get the piece at this square from the game model
        Piece piece = game.getPieceAt(row, col);
        
        // Highlight king in check
        if (piece instanceof King && game.isInCheck(piece.isWhite())) {
            background.getStyleClass().add("check-highlight");
        }
        // Highlight selected square (using model's selected piece state)
        else if (row == game.getSelectedPieceRow() && col == game.getSelectedPieceCol()) {
            background.getStyleClass().add("selected-piece");
        } else if (game.getSelectedPieceRow() != -1) {  // If a piece is selected in the model
            Piece selectedPiece = game.getPieceAt(game.getSelectedPieceRow(), game.getSelectedPieceCol());
            
            // Only proceed if selectedPiece is not null
            if (selectedPiece != null) {
            // Check for valid castling for highlighting
            boolean isValidCastlingForHighlight = false;
            if (selectedPiece instanceof King && Math.abs(col - game.getSelectedPieceCol()) == 2 && row == game.getSelectedPieceRow()) {
                isValidCastlingForHighlight = game.isValidCastling(game.getSelectedPieceRow(), game.getSelectedPieceCol(), row, col);
            }
            
            // If this is a valid move or capture, highlight it
            // Note: This logic duplicates makeMove validation for highlighting purposes.
            boolean isValidMove = (game.getPieceAt(row, col) == null && 
                                selectedPiece.isValidMove(row, col) && 
                                (selectedPiece instanceof Knight || game.isPathClear(game.getSelectedPieceRow(), game.getSelectedPieceCol(), row, col)) &&
                                !game.wouldBeInCheck(game.getSelectedPieceRow(), game.getSelectedPieceCol(), row, col)) ||
                                isValidCastlingForHighlight;
            
            boolean isValidCapture = game.getPieceAt(row, col) != null && 
                                   selectedPiece.canCapture(game.getPieceAt(row, col)) &&
                                   (selectedPiece instanceof Knight || game.isPathClear(game.getSelectedPieceRow(), game.getSelectedPieceCol(), row, col)) &&
                                   !game.wouldBeInCheck(game.getSelectedPieceRow(), game.getSelectedPieceCol(), row, col);
            
            if (isValidMove || isValidCastlingForHighlight) {
                    background.getStyleClass().add("valid-move");
            }
            if (isValidCapture) {
                    background.getStyleClass().add("valid-capture");
                }
            }
        }
        
        square.getChildren().add(background);
        
        // --- display symbols code ---
        Piece pieceToDisplay = game.getPieceAt(row, col);
        if (pieceToDisplay != null) {
            Text pieceText = new Text(String.valueOf(pieceToDisplay.getSymbol()));
            pieceText.setFont(Font.font("DynaPuff", 60));
            pieceText.setFill(pieceToDisplay.isWhite() ? WHITE_PIECE : BLACK_PIECE);
            pieceText.setMouseTransparent(false); // Allow mouse events for drag
            pieceText.setBoundsType(TextBoundsType.VISUAL);
            pieceText.getStyleClass().add("chess-piece");
            
            // Only allow interactions for pieces if it's the player's turn
            boolean isPlayersTurn = !isVsAI || (game.isWhiteTurn() == playerIsWhite);
            boolean isCorrectPieceColorForTurn = pieceToDisplay.isWhite() == game.isWhiteTurn();

            if (!isCorrectPieceColorForTurn || !isPlayersTurn) {
                pieceText.setMouseTransparent(true);
                pieceText.getStyleClass().add("opponent-piece");
            } else {
                pieceText.getStyleClass().add("current-player-piece");
            }
            
            StackPane.setAlignment(pieceText, Pos.CENTER);
            square.getChildren().add(pieceText);

            // Drag-and-drop handlers (only for the player's pieces on their turn)
            if (isCorrectPieceColorForTurn && isPlayersTurn) {
                pieceText.setOnDragDetected(e -> {
                    if (!gameStarted) return;
                    if (pieceToDisplay.isWhite() != game.isWhiteTurn()) return;
                    
                    // Create floating piece that follows cursor
                    draggingPiece = new Text(pieceText.getText());
                    draggingPiece.setFont(pieceText.getFont());
                    draggingPiece.setFill(pieceText.getFill());
                    draggingPiece.getStyleClass().addAll("chess-piece", "dragging");
                    draggingPiece.setOpacity(0.8);
                    draggingPiece.setMouseTransparent(true); // Make the dragging piece non-interactive
                    draggingPiece.setBoundsType(TextBoundsType.VISUAL); // Fix positioning issue
                    
                    // Position the floating piece at cursor (adjusted for better positioning)
                    Point2D scenePoint = pieceText.localToScene(e.getX(), e.getY());
                    Point2D gridPoint = gridPane.sceneToLocal(scenePoint.getX(), scenePoint.getY());
                    draggingPiece.setTranslateX(gridPoint.getX() - SQUARE_SIZE/2);
                    draggingPiece.setTranslateY(gridPoint.getY() - SQUARE_SIZE/2);
                    
                    gridPane.getChildren().add(draggingPiece);
                    draggingFromSquare = square;
                    
                    // Hide original piece
                    pieceText.setVisible(false);
                    
                    // Show valid moves
                    showValidMovesForPiece(row, col);
                    
                    e.consume();
                });
                
                pieceText.setOnMouseDragged(e -> {
                    if (draggingPiece != null) {
                        // Position piece at cursor (adjusted for better positioning)
                        Point2D scenePoint = pieceText.localToScene(e.getX(), e.getY());
                        Point2D gridPoint = gridPane.sceneToLocal(scenePoint.getX(), scenePoint.getY());
                        draggingPiece.setTranslateX(gridPoint.getX() - SQUARE_SIZE/2);
                        draggingPiece.setTranslateY(gridPoint.getY() - SQUARE_SIZE/2);
                    }
                    e.consume();
                });
                
                pieceText.setOnMouseReleased(e -> {
                    if (draggingPiece != null && draggingFromSquare != null) {
                        // Restore the original piece visibility BEFORE updating board
                        pieceText.setVisible(true);
                        
                        int fromRow = GridPane.getRowIndex(draggingFromSquare);
                        int fromCol = GridPane.getColumnIndex(draggingFromSquare);
                        
                        // Calculate target square from cursor position
                        Point2D gridPoint = gridPane.sceneToLocal(e.getSceneX(), e.getSceneY());
                        int toRow = (int)(gridPoint.getY() / SQUARE_SIZE);
                        int toCol = (int)(gridPoint.getX() / SQUARE_SIZE);
                        
                        // Remove the dragging piece BEFORE updating board
                        gridPane.getChildren().remove(draggingPiece);
                        
                        // Bounds check
                        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
                            if (game.makeMove(fromRow, fromCol, toRow, toCol)) {
                                updateBoard(); // Only update the board, do not animate for drag-and-drop
                                // Check for promotion
                                Piece movedPiece = game.getPieceAt(toRow, toCol);
                                if (movedPiece instanceof Pawn && (toRow == 0 || toRow == 7)) {
                                    showPromotionPopup(toRow, toCol);
                                    // AI turn is triggered from promotion callback
                                } else {
                                    if (game.isInCheckmate(game.isWhiteTurn())) {
                                        showVictoryPopup(!game.isWhiteTurn());
                                    } else if (game.isStalemate(game.isWhiteTurn())) {
                                        showStalematePopup();
                                    } else if (isVsAI) {
                                        triggerAIMove();
                                    }
                                }
                            } else {
                                updateBoard();
                            }
                        } else {
                            updateBoard();
                        }
                        
                        // Clean up
                        draggingPiece = null;
                        draggingFromSquare = null;
                        hideValidMoves();
                    }
                    e.consume();
                });
            }
        }
        
        // Add click handler to the square
        square.setOnMouseClicked(e -> handleSquareClick(row, col));
        
        return square;
    }
    
    /**
     * Handle click events on board squares
     */
    private void handleSquareClick(int row, int col) {
        if (!gameStarted) {
            return; // Ignore clicks if the game hasn't started
        }
        
        // If it's an AI game and not the player's turn, ignore click
        if (isVsAI && game.isWhiteTurn() != playerIsWhite) {
            return;
        }

        System.out.println("Square clicked at: " + row + "," + col);
        
        if (game.getSelectedPieceRow() == -1) {
            // Try to select a piece from the game model
            Piece clickedPiece = game.getPieceAt(row, col);
            if (clickedPiece != null && clickedPiece.isWhite() == game.isWhiteTurn()) {
                game.setSelectedPiece(row, col);
                System.out.println("Selected piece: " + game.getPieceAt(row, col).getSymbol());
                    updateBoard();
                }
            } else {
            // Try to move selected piece using the game model

            if (game.makeMove(game.getSelectedPieceRow(), game.getSelectedPieceCol(), row, col)) {
                // Check for promotion
                Piece movedPiece = game.getPieceAt(row, col);
                if (movedPiece instanceof Pawn && (row == 0 || row == 7)) {
                    showPromotionPopup(row, col);
                    game.clearSelectedPiece();
                    updateBoard();
                    return;
                }
                if (game.isInCheckmate(game.isWhiteTurn())) {
                    showVictoryPopup(!game.isWhiteTurn());
                } else if (game.isStalemate(game.isWhiteTurn())) {
                    showStalematePopup();
                } else if (isVsAI) {
                    triggerAIMove();
                }
                game.printBoard();
            } else {
                System.out.println("Invalid move!");
            }
            game.clearSelectedPiece();
            updateBoard();
        }
    }
    
    /**
     * Animate piece movement from one square to another
     */
    private void animatePieceMovement(int fromRow, int fromCol, int toRow, int toCol) {
        // Get the source and target squares
        StackPane fromSquare = getSquareAt(fromRow, fromCol);
        StackPane toSquare = getSquareAt(toRow, toCol);

        if (fromSquare == null || toSquare == null) {
            updateBoard(); // fallback
            return;
        }

        // Find the piece text in the source square (before updateBoard)
        Text sourcePieceText = null;
        for (javafx.scene.Node node : fromSquare.getChildren()) {
            if (node instanceof Text) {
                sourcePieceText = (Text) node;
                break;
            }
        }
        if (sourcePieceText == null) {
            updateBoard(); // fallback
            return;
        }

        // Get the board's scene
        Scene scene = gridPane.getScene();
        if (scene == null) {
            updateBoard();
            return;
        }

        // Convert source and target square positions to scene coordinates
        Point2D fromScene = fromSquare.localToScene(fromSquare.getWidth() / 2, fromSquare.getHeight() / 2);
        Point2D toScene = toSquare.localToScene(toSquare.getWidth() / 2, toSquare.getHeight() / 2);
        Point2D fromBoard = gridPane.sceneToLocal(fromScene);
        Point2D toBoard = gridPane.sceneToLocal(toScene);

        // Create a floating piece node
        Text floatingPiece = new Text(sourcePieceText.getText());
        floatingPiece.setFont(sourcePieceText.getFont());
        floatingPiece.setFill(sourcePieceText.getFill());
        floatingPiece.getStyleClass().addAll(sourcePieceText.getStyleClass());
        floatingPiece.setBoundsType(TextBoundsType.VISUAL);
        floatingPiece.setMouseTransparent(true);
        floatingPiece.setTranslateX(fromBoard.getX() - SQUARE_SIZE / 2);
        floatingPiece.setTranslateY(fromBoard.getY() - SQUARE_SIZE / 2);

        // Hide the piece in the source and destination squares (if any)
        sourcePieceText.setVisible(false);
        Text destPieceText = findPieceText(toSquare);
        if (destPieceText != null) destPieceText.setVisible(false);

        // Add the floating piece to the gridPane overlay
        gridPane.getChildren().add(floatingPiece);

        // Animate the floating piece from source to destination
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), floatingPiece);
        transition.setFromX(fromBoard.getX() - SQUARE_SIZE / 2);
        transition.setFromY(fromBoard.getY() - SQUARE_SIZE / 2);
        transition.setToX(toBoard.getX() - SQUARE_SIZE / 2);
        transition.setToY(toBoard.getY() - SQUARE_SIZE / 2);
        transition.setInterpolator(Interpolator.EASE_BOTH);

        transition.setOnFinished(e -> {
            // Remove the floating piece
            gridPane.getChildren().remove(floatingPiece);
            // Show the piece in the destination square (after updateBoard)
            updateBoard();
        });

        transition.play();
    }
    
    /**
     * Find the piece text in a square
     */
    private Text findPieceText(StackPane square) {
        for (javafx.scene.Node node : square.getChildren()) {
            if (node instanceof Text) {
                return (Text) node;
            }
        }
        return null;
    }
    
    /**
     * Get the square at the specified position
     */
    private StackPane getSquareAt(int row, int col) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                if (node instanceof StackPane) {
                    return (StackPane) node;
                }
            }
        }
        return null;
    }
    
    /**
     * Update the player info panel
     */
    private void updatePlayerInfo() {
        // Update player names if controllers exist
        if (blackPlayerController != null) {
            blackPlayerController.setPlayerName(blackPlayerName);
        }
        if (whitePlayerController != null) {
            whitePlayerController.setPlayerName(whitePlayerName);
        }
        
        // Update game status if controllers exist
        if (blackPlayerController != null && whitePlayerController != null) {
            Window window = gridPane.getScene() != null ? gridPane.getScene().getWindow() : null;
            Stage chessStage = window instanceof Stage ? (Stage) window : null;
            updatePlayerInfo(blackPlayerController, whitePlayerController, chessStage);
        }
    }
    
    /**
     * Update the player info panels with controllers
     */
    private void updatePlayerInfo(PlayerInfoPanelBlackController blackController, PlayerInfoPanelWhiteController whiteController, Stage chessStage) {
        boolean isWhiteTurn = game.isWhiteTurn();
        boolean isWhiteKingInCheck = game.isInCheck(true);
        boolean isBlackKingInCheck = game.isInCheck(false);
        boolean isWhiteKingInCheckmate = game.isInCheckmate(true);
        boolean isBlackKingInCheckmate = game.isInCheckmate(false);
        boolean isStalemate = game.isStalemate(isWhiteTurn);
        // Update black panel
        blackController.updateStatus(!isWhiteTurn, isBlackKingInCheck, isBlackKingInCheckmate, isStalemate, isWhiteKingInCheckmate);
        blackController.updateCapturedPieces(game.getWhiteCaptured());
        // Update white panel
        whiteController.updateStatus(isWhiteTurn, isWhiteKingInCheck, isWhiteKingInCheckmate, isStalemate, isBlackKingInCheckmate);
        whiteController.updateCapturedPieces(game.getBlackCaptured());
        // Ensure the window resizes to fit content
        if (chessStage != null) {
            chessStage.sizeToScene();
        }
    }
    
    /**
     * Show victory popup when game ends in checkmate
     */
    private void showVictoryPopup(boolean whiteWins) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VictoryPopup.fxml"));
            Parent root = loader.load();
            
            // Get controller
            VictoryController controller = loader.getController();
            controller.setWinner(whiteWins ? whitePlayerName : blackPlayerName);
            controller.setVictoryCondition("checkmate");
            controller.setOnNewGame(() -> {
                game.resetGame();
                updatePlayerInfo();
            updateBoard();
            });
            
            // Create stage
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            controller.setStage(popupStage);
        
        // Make window draggable
        final Delta dragDelta = new Delta();
        root.setOnMousePressed(e -> {
            dragDelta.x = popupStage.getX() - e.getScreenX();
            dragDelta.y = popupStage.getY() - e.getScreenY();
        });
        root.setOnMouseDragged(e -> {
            popupStage.setX(e.getScreenX() + dragDelta.x);
            popupStage.setY(e.getScreenY() + dragDelta.y);
        });
        
        // Show the popup
        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        popupStage.show();
        
        // center mainwindow popup
        Window mainWindow = gridPane.getScene().getWindow();
        popupStage.setX(mainWindow.getX() + mainWindow.getWidth()/2 - scene.getWidth()/2);
        popupStage.setY(mainWindow.getY() + mainWindow.getHeight()/2 - scene.getHeight()/2);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show stalemate popup when game ends in stalemate
     */
    private void showStalematePopup() {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StalematePopup.fxml"));
            Parent root = loader.load();
            
            // Get controller
            StalemateController controller = loader.getController();
            controller.setOnNewGame(() -> {
                game.resetGame();
                updatePlayerInfo();
            updateBoard();
            });
            
            // Create stage
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            controller.setStage(popupStage);
        
        // Make window draggable
        final Delta dragDelta = new Delta();
        root.setOnMousePressed(e -> {
            dragDelta.x = popupStage.getX() - e.getScreenX();
            dragDelta.y = popupStage.getY() - e.getScreenY();
        });
        root.setOnMouseDragged(e -> {
            popupStage.setX(e.getScreenX() + dragDelta.x);
            popupStage.setY(e.getScreenY() + dragDelta.y);
        });
        
        // Show the popup
        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        popupStage.show();
        
        // center mainwindow popup
        Window mainWindow = gridPane.getScene().getWindow();
        popupStage.setX(mainWindow.getX() + mainWindow.getWidth()/2 - scene.getWidth()/2);
        popupStage.setY(mainWindow.getY() + mainWindow.getHeight()/2 - scene.getHeight()/2);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add showPromotionPopup method
    private void showPromotionPopup(int row, int col) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PawnPromotionDialog.fxml"));
            Parent root = loader.load();
            
            // Get controller
            PawnPromotionController controller = loader.getController();
            controller.setPromotionPosition(row, col);
            controller.setPieceColor(game.isWhiteTurn());
            controller.setCallback(new PawnPromotionController.PawnPromotionCallback() {
                @Override
                public void onPromotion(int row, int col, String pieceType) {
                    game.promotePawn(row, col, pieceType);
                    updateBoard();
                    if (game.isInCheckmate(game.isWhiteTurn())) {
                        showVictoryPopup(!game.isWhiteTurn());
                    } else if (game.isStalemate(game.isWhiteTurn())) {
                        showStalematePopup();
                    } else if (isVsAI) {
                        triggerAIMove();
                    }
                }
            });
            
            // Create stage
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            controller.setStage(popupStage);
            
            // Make window draggable
            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = popupStage.getX() - e.getScreenX();
                dragDelta.y = popupStage.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                popupStage.setX(e.getScreenX() + dragDelta.x);
                popupStage.setY(e.getScreenY() + dragDelta.y);
            });
            
            // Show the dialog
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.show();
            
            // center mainwindow popup
            Window mainWindow = gridPane.getScene().getWindow();
            popupStage.setX(mainWindow.getX() + mainWindow.getWidth()/2 - scene.getWidth()/2);
            popupStage.setY(mainWindow.getY() + mainWindow.getHeight()/2 - scene.getHeight()/2);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Show valid moves for a piece during drag
     */
    private void showValidMovesForPiece(int row, int col) {
        Piece piece = game.getPieceAt(row, col);
        if (piece == null) return;
        
        // Highlight all valid moves
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (r == row && c == col) continue; // Skip current square
                
                StackPane square = getSquareAt(r, c);
                if (square == null) continue;
                
                // Check if this is a valid move
                boolean isValidMove = false;
                boolean isValidCapture = false;
                
                if (game.getPieceAt(r, c) == null) {
                    // Empty square - check if valid move
                    isValidMove = piece.isValidMove(r, c) && 
                                (piece instanceof Knight || game.isPathClear(row, col, r, c)) &&
                                !game.wouldBeInCheck(row, col, r, c);
                } else {
                    // Occupied square - check if valid capture
                    isValidCapture = piece.canCapture(game.getPieceAt(r, c)) &&
                                   (piece instanceof Knight || game.isPathClear(row, col, r, c)) &&
                                   !game.wouldBeInCheck(row, col, r, c);
                }
                
                // Add visual feedback
                Rectangle background = (Rectangle) square.getChildren().get(0);
                if (isValidMove) {
                    background.getStyleClass().add("valid-move");
                }
                if (isValidCapture) {
                    background.getStyleClass().add("valid-capture");
                }
            }
        }
    }
    
    /**
     * Hide all valid move highlights
     */
    private void hideValidMoves() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                StackPane square = getSquareAt(r, c);
                if (square != null && !square.getChildren().isEmpty()) {
                    Rectangle background = (Rectangle) square.getChildren().get(0);
                    background.getStyleClass().remove("valid-move");
                    background.getStyleClass().remove("valid-capture");
                }
            }
        }
    }
    
    private void triggerAIMove() {
        // Run AI move in a background thread to avoid freezing UI
        new Thread(() -> {
            Platform.runLater(() -> {
                // Show a "thinking" indicator
                if (playerIsWhite) {
                    blackPlayerController.updateStatusThinking(true);
                } else {
                    whitePlayerController.updateStatusThinking(true);
                }
            });

            String fen = game.toFEN();
            String bestMove = stockfishController.getBestMove(fen, aiSkillLevel, aiDepth, aiMultiPV);

            if (bestMove != null) {
                int[] coords = algebraicToCoords(bestMove);
                if (coords != null) {
                    Platform.runLater(() -> {
                        // Hide thinking indicator before making move
                        if (playerIsWhite) {
                            blackPlayerController.updateStatusThinking(false);
                        } else {
                            whitePlayerController.updateStatusThinking(false);
                        }

                        if (game.makeMove(coords[0], coords[1], coords[2], coords[3])) {
                            animatePieceMovement(coords[0], coords[1], coords[2], coords[3]);
                            // Check for promotion (for AI)
                            Piece movedPiece = game.getPieceAt(coords[2], coords[3]);
                            if (movedPiece instanceof Pawn && (coords[2] == 0 || coords[2] == 7)) {
                                // AI always promotes to Queen
                                game.promotePawn(coords[2], coords[3], "Queen");
                                updateBoard();
                            }
                            if (game.isInCheckmate(game.isWhiteTurn())) {
                                showVictoryPopup(!game.isWhiteTurn());
                            } else if (game.isStalemate(game.isWhiteTurn())) {
                                showStalematePopup();
                            }
                        }
                    });
                }
            } else {
                // AI failed to find a move, hide thinking indicator
                Platform.runLater(() -> {
                    if (playerIsWhite) {
                        blackPlayerController.updateStatusThinking(false);
                    } else {
                        whitePlayerController.updateStatusThinking(false);
                    }
                    System.err.println("AI failed to return a move.");
                });
            }
        }).start();
    }

    private int[] algebraicToCoords(String algebraic) {
        if (algebraic == null || algebraic.length() != 4) {
            return null;
        }
        int fromCol = algebraic.charAt(0) - 'a';
        int fromRow = 8 - (algebraic.charAt(1) - '0');
        int toCol = algebraic.charAt(2) - 'a';
        int toRow = 8 - (algebraic.charAt(3) - '0');
        return new int[]{fromRow, fromCol, toRow, toCol};
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private static class Delta {
        double x, y;
    }

    private void showGameModeDialog(Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameModeDialog.fxml"));
            javafx.scene.Parent root = loader.load();
            GameModeDialogController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.initOwner(parentStage);
            dialogStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            controller.setStage(dialogStage);
            controller.setCallback(new GameModeDialogController.GameModeDialogCallback() {
                @Override
                public void onAI() {
                    showAISetupDialog(parentStage);
                }
                @Override
                public void onPassNPlay() {
                    showPlayerNamesDialog(parentStage);
                }
            });
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUndo(Stage chessStage) {
        if (game.undoMove()) {
            updatePlayerInfo(blackPlayerController, whitePlayerController, chessStage);
            updateBoard();
        }
    }

    private void handleRedo(Stage chessStage) {
        if (game.redoMove()) {
            updatePlayerInfo(blackPlayerController, whitePlayerController, chessStage);
            updateBoard();
        }
    }
}