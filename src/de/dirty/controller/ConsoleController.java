package de.dirty.controller;

import de.dirty.command.Command;
import de.dirty.command.CommandManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ConsoleController implements Initializable {

  public static ConsoleController consoleController;
  private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  private List<String> lastInputs = new ArrayList<>();
  private String lastResult = "";
  private boolean infoText = false;
  private int currentLast = 0;

  @FXML private TextArea textArea;

  @FXML private TextField textField;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    consoleController = this;
    textArea.setText("Advanced Console [Version 1.0]\n(c) 2020 DasDirt. All rights reserved.\n");
    textArea.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
    textArea.addEventFilter(
        MouseEvent.MOUSE_CLICKED,
        event -> {
          if (event.getButton().equals(MouseButton.SECONDARY)) {
            if (!textArea.getSelectedText().equals("")) {
              setClipboardString(textArea.getSelectedText());
            }
          }
        });

    textField.setOnMouseClicked(
        mouseEvent -> {
          if (infoText) {
            textField.setText("");
          }
        });
    textField.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            currentLast = 0;
            if (textField.getText().length() > 0) {
              if (!lastInputs.contains(textField.getText())) {
                Collections.reverse(lastInputs);
                lastInputs.add(textField.getText());
                Collections.reverse(lastInputs);
              }
              String tmpText = textField.getText();
              Command command =
                  CommandManager.INSTANCE.getCommand(textField.getText().split(" ")[0]);

              textArea.appendText(
                  "\nExecute command: "
                      + textField.getText()
                      + (command != null ? " [" + command.getCommand() + "]" : "")
                      + "\n");
              textArea.appendText(CommandManager.INSTANCE.handleInput(textField.getText()) + "\n");
              if (textField.getText().equals(tmpText)) {
                textField.setText("");
              }
            }
          } else if (event.getCode().equals(KeyCode.UP)) {
            if (currentLast < lastInputs.size()) {
              currentLast++;
            }
            textField.setText(lastInputs.get(currentLast - 1));
            if (currentLast == lastInputs.size()) {
              currentLast = lastInputs.size() - 1;
            }
          } else if (event.getCode().equals(KeyCode.DOWN)) {
            if (currentLast > 0) {
              currentLast--;
              textField.setText(lastInputs.get(currentLast));
            } else {
              textField.setText("");
            }
          } else if (infoText) {
            infoText = false;
            textField.setText("");
          }
        });
  }

  /** This method sets a text in the clipboard */
  public void setClipboardString(String s) {
    StringSelection stringSelection = new StringSelection(s);
    clipboard.setContents(stringSelection, stringSelection);
    setInfoText("Copied text: '" + s + "' into your clipboard");
  }

  public void setInfoText(String s) {
    infoText = true;
    textField.setText(s);
  }

  public void focusTextField(String pressed) {
    if (!textField.isFocused()) {
      if (pressed != null) {
        textField.appendText(pressed);
      }
      textField.requestFocus();
    }
  }

  /** Returns the instance of the console controller. */
  public static ConsoleController getConsoleController() {
    return consoleController;
  }

  /** Gets the last result */
  public String getLastResult() {
    return lastResult;
  }

  /** Sets the last result */
  public void setLastResult(String lastResult) {
    this.lastResult = lastResult;
  }
}
