package com.example.pain;

import java.awt.*;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.example.pain.HelloApplication;


/**
 * @author Charles Hanson
 * @author Charlie.hanson@valpo.edu
 * @version 10000.0
 */
public class PainTest {
    public PainTest(){
    }
    @Test
    public void testWidth(){
        Assert.assertEquals(HelloApplication.getWidth(),600);
    }
    @Test
    public void testColor(){
        int sides=4;
        Assert.assertSame(sides,HelloApplication.getSides());
    }
    @Test
    public void testFileType(){
        String fakeImageName="testing image.jpg";
        String temp="jpg";
        Assert.assertSame(temp,HelloApplication.getFileHandler().getExtension(fakeImageName),null);
    }

}
