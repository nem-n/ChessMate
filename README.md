# ChessMate — A Chess Game Application

ChessMate is a modern, feature-rich chess game built with JavaFX. It offers a beautiful user interface and a powerful AI opponent powered by the Stockfish chess engine.

## Features

- Play chess against another human (Pass n Play) or the Stockfish AI.
- Modern, animated JavaFX interface with custom themes.
- Full chess rules: pawn promotion, castling, en passant, and more.
- Move history with undo/redo and captured pieces display.
- Endgame detection: victory and stalemate popups.
- Built-in help and about dialogs.

## Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yourusername/ChessMate.git
   cd ChessMate
   ```

2. **Install requirements:**

   - [Java JDK 21+](https://www.oracle.com/java/technologies/downloads/) (ensure `java` and `javac` are in your PATH)
   - [Maven 3.6+](https://maven.apache.org/download.cgi) (ensure `mvn` is in your PATH)

3. **Stockfish Engine Setup:**

   - Download the latest Stockfish binary for Windows from [stockfishchess.org](https://stockfishchess.org/download/).
   - Place `stockfish.exe` in the `engine/` directory at the project root:
     ```
     ChessMate/
       engine/
         stockfish.exe
     ```

4. **Build and run the application:**

   ```bash
   mvn clean javafx:run
   ```

   Or, to build a runnable JAR:

   ```bash
   mvn clean package
   java -jar target/chessn-1.0-SNAPSHOT-shaded.jar
   ```

## Usage

1. **Launch the app** using Maven or the JAR file.
2. **Choose your game mode**: play against another human or the AI.
3. **Enjoy chess** with a modern interface and full rules support.
4. **Undo/redo moves** and view captured pieces as you play.
5. **End the game** to see victory or stalemate popups.

## Project Structure

```plaintext
ChessMate/
├── pom.xml                # Maven build file
├── engine/
│   └── stockfish.exe      # Stockfish engine binary (required for AI)
├── src/
│   └── main/
│       ├── java/          # Java source code (chess logic, UI, controllers)
│       └── resources/
│           ├── fxml/      # JavaFX FXML UI layouts
│           ├── css/       # JavaFX CSS styles
│           └── images/    # UI images (e.g., logo)
```

## Dependencies

- JavaFX 24
- Maven
- Stockfish (external binary)
- JDK 21 or newer

## Notes

- The Stockfish engine is required for AI play. Make sure `stockfish.exe` is in the correct location.
- For other operating systems, download the appropriate Stockfish binary and update the path in `StockfishController.java` if needed.
