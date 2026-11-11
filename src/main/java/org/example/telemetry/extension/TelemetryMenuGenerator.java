package org.example.telemetry.extension;

import bluej.extensions2.BPackage;
import bluej.extensions2.MenuGenerator;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import org.example.telemetry.export.SessionExporter;

import java.nio.file.Path;

class TelemetryMenuGenerator extends MenuGenerator {

    private final SessionExporter exporter;

    TelemetryMenuGenerator(SessionExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public MenuItem getToolsMenuItem(BPackage aPackage) {
        MenuItem item = new MenuItem("Export Telemetry (JSON)");
        item.setOnAction(e -> {
            try {
                Path file = exporter.exportJson();
                new Alert(Alert.AlertType.INFORMATION, "Telemetry exported to:\n" + file).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Export failed: " + ex.getMessage()).showAndWait();
            }
        });
        return item;
    }
}