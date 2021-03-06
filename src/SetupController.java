import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class SetupController extends Thread {

    //non-fxml global variables
    private static double xOffset = 0;
    private static double yOffset = 0;
    private final HashSet<String> libraries = new HashSet<>();
    private MainMenuController parent;

    //fxml global variables
    @FXML
    private ProgressBar progress;
    @FXML
    private Button Start;
    @FXML
    private Button Close;
    @FXML
    private Label gameName;
    @FXML
    private ComboBox<String> librarySelect;
    @FXML
    private TextField Path;
    @FXML
    private Stage dialogStage;
    @FXML
    private Pane grabBar;
    @FXML
    private Button removeLibrary;
    @FXML
    private Button updateLibrary;
    @FXML
    private Button Open;

    //non-fxml methods
    public SetupController() {
    }

    public void setParent(MainMenuController parent) {
        Path.requestFocus();
        libraries.clear();
        this.parent = parent;
        setLibraries();
        grabBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = dialogStage.getX() - event.getScreenX();
                yOffset = dialogStage.getY() - event.getScreenY();
            }
        });
        grabBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dialogStage.setX(event.getScreenX() + xOffset);
                dialogStage.setY(event.getScreenY() + yOffset);
            }
        });
    }

    private void setLibraries() {
        for (Game test : parent.games) {
            String temp = new StringBuilder(test.directory).reverse().toString();
            temp = temp.split("\\\\", 2)[1];
            temp = new StringBuilder(temp).reverse().toString();
            temp = temp + "\\";
            libraries.add(temp);
        }
        librarySelect.getItems().clear();
        if (libraries != null) {
            librarySelect.getItems().addAll(libraries);
        }

    }

    public void handlehover(final MouseEvent event) {
        popUp((Node) event.getSource());
    }

    private void popUp(Node pop) {
        pop.toFront();
        pop.setScaleX(1.1);
        pop.setScaleY(1.1);
    }

    private void popDown(Node source) {
        source.toFront();
        source.setScaleX(1);
        source.setScaleY(1);
    }

    public void handleclick(final MouseEvent event) {
        popDown((Node) event.getSource());
    }

    public void handlemousexit(final MouseEvent event) {
        popDown((Node) event.getSource());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void SetProgress(float percent) {
        progress.setProgress(percent / 100);
    }

    public void run() {
        new GameScanner(Path.getText(), this, parent);
    }

    public void SetGameName(String string) {
        gameName.setText(string);
    }

    public void EnableInterface() {
        Start.setDisable(false);
        librarySelect.setDisable(false);
        Open.setDisable(false);
        removeLibrary.setDisable(false);
        updateLibrary.setDisable(false);
        Path.setDisable(false);
    }

    public void DisableInterface() {
        Start.setDisable(true);
        librarySelect.setDisable(true);
        Open.setDisable(true);
        removeLibrary.setDisable(true);
        Path.setDisable(true);
        updateLibrary.setDisable(true);
    }

    //fxml methods
    @FXML
    private void HandleStart() {
        Thread th;

        if (Path.getText() != null) {
            File f = new File(Path.getText());

            if (f.exists()) {
                libraries.add(Path.getText());
                setLibraries();
                gameName.setText("Starting Scan...");
                SetProgress(0);
                th = new Thread(this, "Game Scanner");
                th.start();
            } else {
                Path.setText("Path not found");
            }
            Path.requestFocus();
        } else {
            Path.setText("Path not found");
        }
        Path.requestFocus();
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }

    @FXML
    private void HandleOpen() {
        popUp(Open);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);

        if (file == null) {
            Path.setText("No Directory selected");
        } else {
            char[] dir = file.getAbsolutePath().toCharArray();
            if (dir[dir.length - 1] == '\\') {
                Path.setText(file.getAbsolutePath());
            } else {
                Path.setText(file.getAbsolutePath() + "\\");
            }
        }
        Path.requestFocus();
    }

    @FXML
    private void HandleRemoveLibrary() {
        popUp(removeLibrary);
        ArrayList<Game> remove = new ArrayList<>();
        for (Game curGam : parent.games) {
            String temp = new StringBuilder(curGam.directory).reverse().toString();
            temp = temp.split("\\\\", 2)[1];
            temp = new StringBuilder(temp).reverse().toString();
            temp = temp + "\\";

            if (temp.equals(librarySelect.getValue())) {
                remove.add(curGam);
            }
        }
        for (Game curGame : remove) {
            File f = new File("SaveData\\" + curGame.getID() + ".png");
            f.delete();
            parent.games.remove(curGame);
        }

        libraries.clear();
        parent.setData(new ArrayList<>());
        librarySelect.setValue(null);
        librarySelect.getSelectionModel().clearSelection();
        librarySelect.getItems().clear();
        setLibraries();

    }

    @FXML
    private void handleUpdateLibrary() {
        popUp(updateLibrary);
        Path.setText(librarySelect.getValue());
        HandleStart();
    }
}