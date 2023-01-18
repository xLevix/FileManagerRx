package com.example.filemanagerrx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import io.reactivex.rxjavafx.observables.JavaFxObservable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class View {

    @FXML
    private ScrollPane dirPane;

    @FXML
    private TextFlow dirPrint;

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


    //get all drives and return using Observable
    public Observable<File> getDrives() {
        return Observable.fromArray(File.listRoots());
    }

    Observable<String> scanPathObs(String path) throws IOException {
        return Observable.create(emitter -> {
            try {
                Files.list(Paths.get(path))
                        .filter(s -> s.toFile().isDirectory() || s.toFile().isFile())
                        .map(Path::toString)
                        .map(s -> s + "\n")
                        .sorted()
                        .forEachOrdered(emitter::onNext);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    @FXML
    void initialize() {
        path.setText("C:\\");

        JavaFxObservable.valuesOf(path.textProperty())
                .subscribe(
                        s -> {
                            try {
                                print.getChildren().clear();

                                scanPathObs(s)
                                        .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                                        .observeOn((JavaFxScheduler.platform()))
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
                                                    }else{
                                                        JavaFxObservable.eventsOf(text, MouseEvent.MOUSE_CLICKED)
                                                                .subscribe(
                                                                        s2 -> {
                                                                            try {
                                                                                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url.trim());
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
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
//                            String url = path.getText();
//                            if (url.contains("\\")) {
//                                url = url.substring(0, url.lastIndexOf("\\"));
//                                path.setText(url);
//                            }else{
//                                url = "F:\\";
//                                path.setText(url);
//                            }
                            print.getChildren().clear();
                            String[] split = path.getText().split("\\\\");
                            String newPath = "";
                            for (int i = 0; i < split.length - 1; i++) {
                                newPath += split[i] + "\\";
                            }
                            path.setText(newPath);

                        }
                );




        Disposable drives = getDrives()
                .subscribe(s -> {
                    Text text = new Text(s.toString()+"\n");
                    text.setFill(Color.BLUE);
                    text.fontProperty().setValue(text.getFont().font(20));
                    dirPrint.getChildren().add(text);

                    JavaFxObservable.eventsOf(text, MouseEvent.MOUSE_CLICKED)
                            .subscribe(
                                    s1 -> {
                                        path.setText(s.toString());
                                    }
                            );
                });

    }


}
