package chattingApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Display extends VBox {

    //data field
    //panes
    private final Pane centerHolder = new Pane();
    private final VBox centerPane = new VBox();
    private final HBox bottomPane = new HBox();

    //objects and variables
    private double moveUP = 0;
    private double moveDOWN = 0;
    private double paneHeight = 0;
    private TextField inputArea;
    private String typedMessage = "";

    //constructor
    public Display() {
        //the message display area
        defineCenter();
        //the input area
        defineBottom();

        //whole screen
        getChildren().add(centerHolder);
        getChildren().add(bottomPane);
        //handler
        handlerMethod();
    }

    //bottom
    private void defineBottom() {
        //the input field for the user
        inputArea = new TextField();
        inputArea.setPadding(new Insets(10));
        inputArea.setBorder(new Border(
                new BorderStroke(Color.ALICEBLUE, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(3), new Insets(0))
        ));
        inputArea.setBackground(new Background(
                new BackgroundFill(Color.web("CBDCFCFF"), new CornerRadii(2), new Insets(0))
        ));
        inputArea.setFont(new Font(18));

        inputArea.setMinWidth(430);
        //fixing in the pane
        //movable buttons

        //when scrolled down
        this.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() < 0)
                if ((centerPane.getHeight() > 610) && (moveUP < (centerPane.getHeight() - 700))) {
                    centerPane.setLayoutY(centerPane.getLayoutY() - 30);
                    moveUP += 30;
                    moveDOWN -= 30;
                }
        });

        //when scrolled up
        this.addEventHandler(ScrollEvent.SCROLL, event -> {

            if (event.getDeltaY() > 0)
                //move the screen when necessary
                if ((centerPane.getHeight() > 610) && (moveDOWN < (centerPane.getHeight() - 610))) {
                    centerPane.setLayoutY(centerPane.getLayoutY() + 30);
                    moveDOWN += 30;
                    moveUP -= 30;
                }
        });
//hi
        bottomPane.getChildren().add(inputArea);
        bottomPane.setPadding(new Insets(2));
    }

    //center
    private void defineCenter() {
        centerPane.setBackground(new Background(
                new BackgroundFill(Color.web("F0FBEDF3"), new CornerRadii(0), new Insets(0))
        ));
        centerHolder.getChildren().add(centerPane);
        centerPane.setMinHeight(610);
        centerPane.setMinWidth(435);
    }

    //get input and display
    public synchronized void getAndDisplay(String input, boolean right, boolean machine) {
        //the message
        HBox holder = new HBox();
        Label inputMessage = new Label(input);
        inputMessage.setFont(new Font("Verdana", 18));
        inputMessage.setTextAlignment(TextAlignment.JUSTIFY);
        inputMessage.setWrapText(true);
        inputMessage.setMaxWidth(370);
        inputMessage.setBackground(new Background(
                new BackgroundFill(Color.web("F5D3F4FF"), new CornerRadii(3), new Insets(0))
        ));
        inputMessage.setBorder(new Border(
                new BorderStroke(
                        Color.web("B8B6B61A"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(3),
                        new BorderWidths(2),
                        new Insets(4))
        ));

        if (machine) {
            inputMessage.setBackground(new Background(
                    new BackgroundFill(Color.web("8B8BF1FF"), new CornerRadii(3), new Insets(0))
            ));
        }
        //the box is displayed right if you type and on left if it is received message
        if ((right)) {
            VBox.setMargin(holder, new Insets(2, 5, 2, 60));
            holder.setAlignment(Pos.CENTER_RIGHT);
        } else {
            VBox.setMargin(holder, new Insets(2, 60, 2, 5));
            holder.setAlignment(Pos.CENTER_LEFT);
        }
        paneHeight = (centerPane.getHeight() > 610) ? centerHolder.getHeight() - 40 : centerHolder.getHeight();
        holder.getChildren().add(inputMessage);
        centerPane.getChildren().add(holder);
        centerPane.setLayoutY(paneHeight - centerPane.getHeight());
        paneHeight = centerHolder.getHeight();
    }

    //event handler
    public void handlerMethod() {
        //store the typed message and display it
        inputArea.setOnAction(event -> {
            typedMessage = inputArea.getText().trim();
            inputArea.clear();
            getAndDisplay(typedMessage, true, false);
        });
    }

    //function to return the typed message
    public synchronized String getTypedMessage() {
        return typedMessage;
    }

    //clear the typed message
    public synchronized void clearTypedMessage() {
        typedMessage = "";
    }

}
