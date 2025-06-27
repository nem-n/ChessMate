// Module definition for the ChessN application
module com.chessn {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.chessn to javafx.fxml;
    opens com.chessn.controllers to javafx.fxml;
    exports com.chessn;
    exports com.chessn.controllers;
}