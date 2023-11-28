module com.habibillina.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;

    opens com.habibullina.fx.chat to javafx.controls;
    exports com.habibullina.fx.chat;

    opens com.habibullina.fx.bot to javafx.controls;
    exports com.habibullina.fx.bot;

    opens com.habibullina.fxml to javafx.fxml;
    exports com.habibullina.fxml;

    opens com.habibullina.fxml.controller to javafx.fxml;
    exports com.habibullina.fxml.controller;

    opens com.habibullina.fxml.model to javafx.fxml;
    exports com.habibullina.fxml.model;
}