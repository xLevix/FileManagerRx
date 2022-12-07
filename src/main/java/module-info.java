module com.example.filemanagerrx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires de.jensd.fx.glyphs.fontawesome;
    requires rxjavafx;
    requires io.reactivex.rxjava2;

    opens com.example.filemanagerrx to javafx.fxml;
    exports com.example.filemanagerrx;
}