package com.example.pain;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static com.example.pain.HelloApplication.canvasToImage;

public class FileHandler {
    private final Canvas canvas;

    public FileHandler(Canvas canvas) {
        this.canvas = canvas;
    }

    public void saveImage(){
        Image image= canvasToImage(0,0,canvas.getHeight(),canvas.getWidth());
        File f= new File("C:/250/New_File.png"); //creates a new file with this default location
        BufferedImage bImage=SwingFXUtils.fromFXImage(image,null); //gets the image from the canvas
        try{
            ImageIO.write(bImage,"png",f); //writes the image to the file
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        System.out.println("saved!");
    }
    public void saveAsImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPG Files (very lossy)", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP Files", "*.bmp")
        );
        File f = fileChooser.showSaveDialog(null);

        if (f != null) {
            String extension = getExtension(f.getName());
            WritableImage wImage= new WritableImage((int) canvas.getWidth(),(int) canvas.getHeight());
            BufferedImage bImage=new BufferedImage((int)canvas.getWidth(),(int) canvas.getHeight(),BufferedImage.TYPE_INT_RGB);
            canvas.snapshot(null,wImage);
            System.out.println(extension);
            try {//finding extension
                if ("png".equalsIgnoreCase(extension)) {
                    ImageIO.write(SwingFXUtils.fromFXImage(wImage, bImage), "png", f);
                } else if ("jpg".equalsIgnoreCase(extension)) {
                    ImageIO.write(SwingFXUtils.fromFXImage(wImage, bImage), "jpg", f);
                } else if ("bmp".equalsIgnoreCase(extension)) {
                    ImageIO.write(SwingFXUtils.fromFXImage(wImage, bImage), "bmp", f);
                } else {
                    // Handle unsupported formats
                    System.err.println("Unsupported format: " + extension);
                }
            } catch (IOException ex) {
                ex.printStackTrace(); //in case of error
            }
        }
    }
    public String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "png"; // Default to PNG if extension is not recognized
    }
    public void openNewWindow() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Save?");

        Button saveButton = new Button("Save");
        Button dontSave=new Button("Close");
        saveButton.setOnAction(e -> saveAsImage());
        dontSave.setOnAction(e->popupStage.close());


        javafx.scene.text.Text text = new javafx.scene.text.Text("Save before closing?");
        javafx.scene.text.Text text2 = new javafx.scene.text.Text("My coffee machine is wifi connected, \n so I can never get behind on java updates"); //funny java joke


        VBox vbox = new VBox(text, saveButton,dontSave,text2);
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

}
