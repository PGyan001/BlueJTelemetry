package org.example.telemetry.adapter;

import bluej.extensions2.BPackage;
import bluej.extensions2.event.ClassEvent;

final class AdapterNames {

    private AdapterNames() {}

    static String project(BPackage pkg) {
        try {
            return pkg == null ? "" : pkg.getProject().getName();
        } catch (Exception e) {
            return "";
        }
    }

    static String className(ClassEvent event) {
        try {
            return event.getBClass() == null ? "" : event.getBClass().getName();
        } catch (Exception e) {
            return "";
        }
    }
}