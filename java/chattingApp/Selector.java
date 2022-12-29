package chattingApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class Selector extends Application {
    //data field
    private Stage stage = new Stage();
    private String IPAddress;
    private Socket socket;
    private Thread clientTh, serverTh, inputTh, outputTh;

    //override start method
    @Override
    public void start(Stage stageDummy) {

        //to define the pane and scene
        Button server = new Button("ACCEPT REQUEST");   //the server button
        server.setFont(new Font("Verdana", 15));
        server.setBorder(new Border(new BorderStroke(Color.web("293535FF"), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(3))));
        server.setPrefWidth(200);
        server.setPrefHeight(60);
        Button client = new Button("SEND REQUEST"); //the client button
        client.setFont(new Font("Verdana", 15));
        client.setBorder(new Border(new BorderStroke(Color.web("293535FF"), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(3))));
        client.setPrefWidth(200);
        client.setPrefHeight(60);
        VBox pane = new VBox(server, client);//this arranges the two button in vertical order
        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(40);
        StackPane paneHolder = new StackPane(pane);//this holds the pane in the center of the screen

        //stage definition
        stage.setScene(new Scene(paneHolder, 430, 400));
        stage.setTitle("CONNECT TO INTERNET BEFORE USING");
        stage.show();

        //handling methods
        client.setOnAction(actionEvent -> clientMethod());
        server.setOnAction(actionEvent -> serverMethod());
    }

    //method for the server
    private void serverMethod() {
        //to create a window to show ip address
        try {
            IPAddress = InetAddress.getLocalHost().getHostAddress(); //get ip address and display in screen
        } catch (UnknownHostException e) {
            Platform.exit();    //rare case
        }
        runningMethod(false);
    }


    //method for the client
    private void clientMethod() {
        TextField ipField = new TextField("ENTER THE IP ADDRESS OF YOUR FRIEND");
        ipField.setMaxWidth(stage.getWidth() * 0.75);
        ipField.setBorder(new Border(new BorderStroke(Color.web("293535FF"), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(3))));
        StackPane pane = new StackPane(ipField);
        stage.setScene(new Scene(pane, 430, 400));
        stage.centerOnScreen();
        ipField.setOnAction(actionEvent -> {
            IPAddress = ipField.getText().trim();
            runningMethod(true);
        });
    }

    //the main running method when the user interract
    private void runningMethod(boolean client) {
        Display display = new Display(); //to define the display

        //=================================================================================//
        //the threads
        clientTh = new Thread(() -> {//this run when the client button is clicked
            try {
                socket = new Socket(IPAddress, 8111);
            } catch (IOException e) {
                Platform.exit();
            }
            if (socket.isConnected()) {
                Platform.runLater(() -> display.getAndDisplay("CONNECTED", false, true));
                inputTh.start();
                outputTh.start();
            }
        });

        //server thread
        serverTh = new Thread(() -> {
            //to display the ip address of yours
            Platform.runLater(() -> display.getAndDisplay("IP address: " + IPAddress + "\n wait for your friend to get connected", false, true));
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(8111);
                socket = serverSocket.accept();
            } catch (IOException e) {
                Platform.exit();
            }
            if (socket.isConnected()) {
                Platform.runLater(() -> display.getAndDisplay("CONNECTED", false, true));
                inputTh.start();
                outputTh.start();
            }
        });

        //input thread
        inputTh = new Thread(() -> {
            DataInputStream input = null;
            String message = "";
            //create input stream
            try {
                input = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                Platform.exit();
            }
            //run endless loop to get input
            boolean endRun = false;
            while (!endRun) {
                message = "";
                try {
                    message = input.readUTF();
                } catch (IOException e) {
                    Platform.runLater(() -> display.getAndDisplay("YOUR FRIEND LOST OR CLOSSED CONNECTION", false, true));
                    endRun = true;
                }
                //copy message to final variable to use in the lambda expression
                final String tempMessage = message;
                //display the input message
                if (tempMessage.trim().length() > 0) {
                    Platform.runLater(() -> display.getAndDisplay(tempMessage, false, false));
                }
            }
        });

        //output thread
        outputTh = new Thread(() -> {
            DataOutputStream output = null;
            String message = "";
            //create input stream
            try {
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Platform.exit();
            }
            //run endless loop to get input
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                message = "";
                //to get the typed message
                message = display.getTypedMessage();
                display.clearTypedMessage();    //to clear the typed message once sent
                //display the output message
                if (message.trim().length() > 0) {
                    try {
                        output.writeUTF(message);
                    } catch (IOException e) {
                    }
                }

                try {
                    output.flush();
                } catch (IOException e) {
                }
            }
        });
        //=================================================================================//


        if (client) {
            clientTh.start();
        } else {
            serverTh.start();
        }
        //the display
        stage.setScene(new Scene(display));
        stage.centerOnScreen();
        stage.setResizable(false);
    }

    //stop method
    @Override
    public void stop() {
        System.exit(2);//to end everything if the window is closed
    }

    //main method
    public static void main(String[] args) {
        launch(args);
    }

}
