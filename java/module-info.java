module com.example.chattingapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens chattingApp to javafx.fxml;
    exports chattingApp;
}