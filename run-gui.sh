#!/bin/bash
cd /home/julia/transporte-app
java --module-path /home/julia/javafx-sdk-21.0.11/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp "bin:lib/sqlite-jdbc-3.36.0.3.jar" \
     ui.MainApp
