package org.example.telemetry.adapter;

import bluej.extensions2.BPackage;
import bluej.extensions2.event.ClassEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;

final class AdapterNames {
    private AdapterNames() {}

    static String project(BPackage pkg) {
        try { return pkg==null ? "" : pkg.getProject().getName();} 
        catch (Exception e) { return "";}
    }

    static String className(ClassEvent event) {
        try { return event.getBClass()==null ? "" : event.getBClass().getName();} 
        catch (Exception e) {return ""; }
    }

    static boolean isNewlyCreated(BPackage pkg) {
        try {
            File dir = pkg.getDir();
            BasicFileAttributes attrs = Files.readAttributes(dir.toPath(), BasicFileAttributes.class);
            Instant created = attrs.creationTime().toInstant();
            return Duration.between(created,Instant.now()).abs().getSeconds() < 15;
        } 
        catch (Exception e) { return false; }
    }
}