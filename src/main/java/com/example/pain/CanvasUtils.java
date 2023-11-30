package com.example.pain;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

public class CanvasUtils {
    public static WritableImage canvasToImage(Canvas canvas, double x1, double y1, double x2, double y2) {
        SnapshotParameters snap = new SnapshotParameters();
        WritableImage temp = new WritableImage((int) Math.abs(x1 - x2), (int) Math.abs(y1 - y2));
        snap.setViewport(new Rectangle2D(
                (Math.min(x1, x2)),
                (Math.min(y1, y2)),
                Math.abs(x1 - x2),
                Math.abs(y1 - y2)));
        canvas.snapshot(snap, temp);
        return temp;
    }

}
