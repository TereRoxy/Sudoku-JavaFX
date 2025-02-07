module view.sudoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens view to javafx.fxml;
    exports view;
    exports start;
    opens start to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
}