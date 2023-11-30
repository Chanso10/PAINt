package com.example.pain;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;

import java.io.File;

import static com.example.pain.CanvasUtils.canvasToImage;
import static com.example.pain.HelloApplication.*;

public class DrawingManager {
    private static GraphicsContext gc;

    public DrawingManager(GraphicsContext gc) {
        DrawingManager.gc = gc;
        pushInitialState(); //push a blank canvas state initially
    }

    private void pushInitialState() {
        Image initialState = canvasToImage(canvas,0, 0, canvas.getHeight(), canvas.getWidth());
        undoStack.push(initialState);
    }

    public void drawLine(double startX, double startY, double endX, double endY) {
        gc.strokeLine(startX, startY, endX, endY);
    }

    public void drawCircle(double endX, double endY, double startX, double startY) {
        double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) / 2;
        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;

        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public void drawSquare(double startX, double startY, double endX, double endY) {
        double size = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
        double squareX = Math.min(startX, endX);
        double squareY = Math.min(startY, endY);
        gc.strokeRect(squareX, squareY, size, size);

    }

    public void drawRectangle(double startX, double startY, double endX, double endY) {
        double width = Math.abs(endX - startX);

        double rectangleX = Math.min(startX, endX);
        double rectangleY = Math.min(startY, endY);

        gc.strokeRect(rectangleX, rectangleY, width, width);
    }

    public void drawTriangle(double startX, double startY, double endX, double endY) {
        double midX = (startX + endX) / 2;
        double triangleHeight = Math.abs(endY - startY);

        double[] xPoints = { startX, endX, midX };
        double[] yPoints = { startY, startY, startY - triangleHeight };

        gc.strokePolygon(xPoints, yPoints, 3);
    }

    public void drawEllipse(double startX, double startY, double endX, double endY) {
        double ellipseX = Math.min(startX, endX);
        double ellipseY = Math.min(startY, endY);
        double ellipseWidth = Math.abs(endX - startX);
        double ellipseHeight = Math.abs(endY - startY);
        gc.strokeOval(ellipseX, ellipseY, ellipseWidth, ellipseHeight);
    }

    public void drawText(String text, double x, double y) {
        gc.strokeText(text, x, y);
    }

    public void drawNgon(int sides, double endX, double endY, double startX,double startY) {
        double centerX = (endX-(.5*(endX-startX)));
        double centerY = (endY-(.5*(endY-startY)));
        double radius = Math.min(endX-startX, (endY-startY)) * 0.4;
        double[] xPoints = new double[sides];
        double[] yPoints = new double[sides];

        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides;
            xPoints[i] = centerX + radius * Math.cos(angle);
            yPoints[i] = centerY - radius * Math.sin(angle);
        }
        gc.strokePolygon(xPoints, yPoints, sides);
    }

    public void drawRotatedImage(GraphicsContext gc, Image image, double tlpx, double tlpy) {
        gc.save(); // saves the current state on stack, including the current transform
        rotate(gc, 90, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.drawImage(image, tlpx, tlpy);
        gc.restore(); // back to original state (before rotation)
    }
    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }
    public static void redo() {
        if (!redoStack.isEmpty()) {
            Image nextState = redoStack.pop();
            applyState(nextState);

            undoStack.push(nextState); // Push the state back to the undo stack
        }
    }
    public static void undo() {
        Image currentState = undoStack.pop();
        if (!undoStack.isEmpty()) {
            redoStack.push(currentState);
            gc.drawImage(undoStack.peek(),0,0);
        }else{
            gc.drawImage(currentState,0,0);
            undoStack.push(currentState);
        }
    }
    public void addImageFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());

            // Resize the canvas to fit the image
            canvas.setWidth(image.getWidth());
            canvas.setHeight(image.getHeight());

            // Clear the canvas and draw the image
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(image, 0, 0);
        }
    }
}
