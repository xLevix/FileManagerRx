package com.example.filemanagerrx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class View {

    @FXML
    private FontAwesomeIconView left;

    @FXML
    private ScrollPane pane;

    @FXML
    private TextFlow print;

    @FXML
    private TextField path;

    @FXML
    private FontAwesomeIconView right;

    Observable<String> scanPathObs(String path) throws IOException {
        return Observable.create(emitter -> {
            try {
                Files.list(Paths.get(path))
                        .filter(s -> s.toFile().isDirectory() || s.toFile().isFile())
                        .map(Path::toString)
                        .map(s -> s + "\n")
                        .forEach(emitter::onNext);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    @FXML
    void initialize() {
        path.setText("F:\\");

        JavaFxObservable.valuesOf(path.textProperty())
                .subscribe(
                        s -> {
                            try {
                                print.getChildren().clear();

                                scanPathObs(s)
                                        .subscribe(
                                                s1 -> {
                                                    String url = s1;
                                                    Text text = new Text(url);
                                                    if (!url.contains(".")) {
                                                        text.setFill(Color.BLUE);
                                                        JavaFxObservable.eventsOf(text, MouseEvent.MOUSE_CLICKED)
                                                                .subscribe(
                                                                        s2 -> {
                                                                            path.setText(url);
                                                                        }
                                                                );
                                                    }
                                                    text.fontProperty().setValue(text.getFont().font(20));
                                                    print.getChildren().add(text);
                                                }
                                        );

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );



        JavaFxObservable.eventsOf(left, MouseEvent.MOUSE_CLICKED)
                .subscribe(
                        s -> {
                            String url = path.getText();
                            if (url.contains("\\")) {
                                url = url.substring(0, url.lastIndexOf("\\"));
                                path.setText(url);
                            }else{
                                url = "F:\\";
                                path.setText(url);
                            }
                        }
                );


    }


}