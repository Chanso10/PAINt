package com.example.pain;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.canvas.Canvas;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.Stack;


/**
 * @author Charles Hanson
 * @author Charlie.hanson@valpo.edu
 * @version 100000.0
 */
public class HelloApplication extends Application {
    public static WritableImage selectedImage;
    public static String shape="pen";
    public static GraphicsContext gc;
    public static int sides=4;
    public static Stack<Image> undoStack;
    public static Stack<Image> redoStack;
    public static Canvas canvas =new Canvas(400,400);
    //rosasco image
    //https://marvel-b1-cdn.bc0a.com/f00000000181213/www.valpo.edu/computing-information-sciences/files/2015/02/20150204-JLH-Nick-Rosasco-004-800x800.jpg
    static Image pic;
    private static Boolean isDragging=false;
    private static int width;
    private static double startX;
    private static double startY;
    private static DrawingManager drawingManager;
    private static FileHandler fileHandler;
    public Group group=new Group();
    public int autosaveTime=10;
    public Stage stage1=new Stage();
    private int currentTab=1;
    private ArrayList<WritableImage> tabs;
    private String texter;
    private Color color=Color.BLACK;
    private double endX, endY;
    private Label countdownLabel;
    private MyLogger logger;




    public static int getWidth() {
        return width;
    }

    public static void applyState(Image state) {
        gc.drawImage(state, 0, 0);
    }

    public static String getShape(){
        return shape;
    }

    public static GraphicsContext getGc() {
        return gc;
    }

    /**
     * creates an image out of a snapshot for the purpose of saving
     *
     * @param x1 starting x (usually 0)
     * @param y1 starting y (usually 0)
     * @param x2 ending x
     * @param y2 ending y
     * @return an image usable by the save function
     */
    public static WritableImage canvasToImage(double x1, double y1, double x2, double y2){
        SnapshotParameters snap = new SnapshotParameters();
        WritableImage temp = new WritableImage((int)Math.abs(x1 - x2),(int)Math.abs(y1 - y2));
        snap.setViewport(new Rectangle2D(
                (Math.min(x1, x2)),
                (Math.min(y1, y2)),
                Math.abs(x1 - x2),
                Math.abs(y1 - y2)));
        canvas.snapshot(snap, temp);
        return temp;
    }

    public static FileHandler getFileHandler(){
        return fileHandler;
    }

    public static int getSides(){
        return sides;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        //main objects
        Scene scene = new Scene(group, 600, 600);
        canvas.setHeight(400);
        canvas.setWidth(400);
        gc = canvas.getGraphicsContext2D();
        gc.drawImage(pic,0,25);
        group.getChildren().addAll(canvas);
        ColorPicker picker=new ColorPicker();
        picker.setValue(color);
        picker.setOnAction(e -> color = picker.getValue());
        picker.getStyleClass().add("button");
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        tabs=new ArrayList<>();
        for(int i=0;i<6;i++){
            WritableImage temp=new WritableImage(300,200);
            tabs.add(temp);
        }
        drawingManager=new DrawingManager(gc);
        fileHandler=new FileHandler(canvas);

        //making buttons and menus
        CustomMenuItem help=new CustomMenuItem(new Label("help"));
        MenuItem closeButton=new MenuItem("Close");
        MenuItem saveButton=new MenuItem("Save");
        MenuItem saveAsButton=new MenuItem("Save As");
        MenuItem blue=new MenuItem("Blue");
        MenuItem red=new MenuItem("Red");
        MenuItem black=new MenuItem("Black");
        MenuItem erase=new MenuItem("Erase");
        MenuItem rgb=new MenuItem("Choose...");//soon...
        MenuItem grabber=new MenuItem("Grab");
        rgb.setGraphic(picker);
        MenuItem Width1=new MenuItem("1");
        MenuItem Width2=new MenuItem("2");
        MenuItem Width3=new MenuItem("3");
        MenuItem drawSquare=new MenuItem("Square");
        MenuItem drawCircle=new MenuItem("Circle");
        MenuItem drawTriangle=new MenuItem("Triangle");
        MenuItem drawRect=new MenuItem("Rectangle");
        MenuItem drawEllipse=new MenuItem("Ellipse");
        MenuItem drawLine=new MenuItem("Line");
        MenuItem drawNGon=new MenuItem("NGon");
        MenuItem selector=new MenuItem("Select");
        MenuItem textDraw=new MenuItem("Text");
        MenuItem drawPen=new MenuItem("Pen");
        MenuItem dashBool=new MenuItem("Dashed");
        MenuItem clear=new MenuItem("Clear");
        MenuItem undoButton = new MenuItem("Undo");
        undoButton.setOnAction(e -> DrawingManager.undo());
        MenuItem redoButton = new MenuItem("Redo");
        redoButton.setOnAction(e -> DrawingManager.redo());
        MenuItem paste=new MenuItem("Paste");
        MenuItem tab1=new MenuItem("1");
        MenuItem tab2=new MenuItem("2");
        MenuItem tab3=new MenuItem("3");
        MenuItem tab4=new MenuItem("4");
        MenuItem tab5=new MenuItem("5");
        MenuItem tab6=new MenuItem("6");
        MenuItem timerToggle=new MenuItem("Timer");
        MenuItem addImageButton =new MenuItem("Import Image");
        CustomMenuItem rotate=new CustomMenuItem(new Label("rotate"));
        CustomMenuItem rotateSelect=new CustomMenuItem(new Label("Rotate Selected"));
        CustomMenuItem mirror=new CustomMenuItem(new Label("Mirror"));

        width=1;
        Menu penColor=new Menu("Pen");
        Menu Menu1=new Menu("File");
        Menu penWidth=new Menu("Width");
        Menu draw=new Menu("Draw");
        Menu tabMenu=new Menu("Tab");
        MenuBar bart =new MenuBar();
        countdownLabel=new Label();

        bart.getMenus().addAll(Menu1,penColor,penWidth,draw,tabMenu);
        penColor.getItems().addAll(dashBool,blue,red,black,rgb,grabber,erase);
        penWidth.getItems().addAll(Width1,Width2,Width3);
        Menu1.getItems().addAll(closeButton,saveButton,saveAsButton,addImageButton,clear,undoButton,redoButton,paste,timerToggle);
        draw.getItems().addAll(drawPen,drawCircle,drawEllipse,drawRect,drawSquare,drawTriangle,drawLine,drawNGon,selector,textDraw,rotate,rotateSelect,mirror);
        tabMenu.getItems().addAll(tab1,tab2,tab3,tab4,tab5,tab6);
        group.getChildren().addAll(bart,countdownLabel);

        countdownLabel.setTranslateX((canvas.getWidth()/3)*2);
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseReleased(this::handleMouseReleased);

        //button zone
        Image penIcon=new Image("https://cdn-icons-png.flaticon.com/512/1860/1860115.png");
        ImageView penView=new ImageView(penIcon);
        penView.setFitWidth(15);
        penView.setFitHeight(15);
        drawPen.setGraphic(penView);

        Image circleIcon=new Image("https://d1nhio0ox7pgb.cloudfront.net/_img/o_collection_png/green_dark_grey/512x512/plain/shape_circle.png");
        ImageView circleView=new ImageView(circleIcon);
        circleView.setFitWidth(15);
        circleView.setFitHeight(15);
        drawCircle.setGraphic(circleView);

        Image closeIcon=new Image("https://cdn4.iconfinder.com/data/icons/ionicons/512/icon-close-512.png");
        ImageView closeView=new ImageView(closeIcon);
        closeView.setFitWidth(15);
        closeView.setFitHeight(15);
        closeButton.setGraphic(closeView);

        Image saveIcon=new Image("https://cdn-icons-png.flaticon.com/512/2550/2550221.png");
        ImageView saveView=new ImageView(saveIcon);
        saveView.setFitWidth(15);
        saveView.setFitHeight(15);
        saveButton.setGraphic(saveView);

        Image saveAsIcon=new Image("https://cdn-icons-png.flaticon.com/512/2550/2550221.png");
        ImageView saveAsView=new ImageView(saveAsIcon);
        saveAsView.setFitWidth(15);
        saveAsView.setFitHeight(15);
        saveAsButton.setGraphic(saveAsView);

        Image redoIcon=new Image("https://cdn-icons-png.flaticon.com/512/44/44650.png");
        ImageView redoView=new ImageView(redoIcon);
        redoView.setFitWidth(15);
        redoView.setFitHeight(15);
        redoButton.setGraphic(redoView);

        Image undoIcon=new Image("https://www.shutterstock.com/image-vector/return-button-icon-isolatedvector-illustration-260nw-1431161813.jpg");
        ImageView undoView=new ImageView(undoIcon);
        undoView.setFitWidth(15);
        undoView.setFitHeight(15);
        undoButton.setGraphic(undoView);

        Image pasteIcon=new Image("https://cdn-icons-png.flaticon.com/512/6583/6583091.png");
        ImageView pasteView=new ImageView(pasteIcon);
        pasteView.setFitWidth(15);
        pasteView.setFitHeight(15);
        paste.setGraphic(pasteView);

        Image elipIcon=new Image("https://cdn-icons-png.flaticon.com/512/1014/1014918.png");
        ImageView elipView=new ImageView(elipIcon);
        elipView.setFitWidth(15);
        elipView.setFitHeight(15);
        drawEllipse.setGraphic(elipView);

        Image rectIcon=new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Square_symbol.svg/1200px-Square_symbol.svg.png");
        ImageView rectView=new ImageView(rectIcon);
        ImageView squareView=new ImageView(rectIcon);
        ImageView selectView=new ImageView(rectIcon);
        rectView.setFitWidth(15);
        rectView.setFitHeight(15);
        squareView.setFitHeight(15);
        squareView.setFitWidth(15);
        selectView.setFitWidth(15);
        selectView.setFitHeight(15);
        drawRect.setGraphic(rectView);
        selector.setGraphic(selectView);
        drawSquare.setGraphic(squareView);

        Image grabIcon=new Image("https://cdn-icons-png.flaticon.com/512/483/483909.png");
        ImageView grabView=new ImageView(grabIcon);
        grabView.setFitWidth(15);
        grabView.setFitHeight(15);
        grabber.setGraphic(grabView);

        Image eraseIcon=new Image("https://cdn-icons-png.flaticon.com/512/1827/1827954.png");
        ImageView eraseView=new ImageView(eraseIcon);
        eraseView.setFitWidth(15);
        eraseView.setFitHeight(15);
        erase.setGraphic(eraseView);

        Image triIcon=new Image("https://d1nhio0ox7pgb.cloudfront.net/_img/o_collection_png/green_dark_grey/512x512/plain/shape_triangle.png");
        ImageView triView=new ImageView(triIcon);
        triView.setFitWidth(15);
        triView.setFitHeight(15);
        drawTriangle.setGraphic(triView);

        Image textIcon=new Image("https://cdn-icons-png.flaticon.com/512/3721/3721901.png");
        ImageView textView=new ImageView(textIcon);
        textView.setFitWidth(15);
        textView.setFitHeight(15);
        textDraw.setGraphic(textView);

        Image lineIcon=new Image("https://static.thenounproject.com/png/1181533-200.png");
        ImageView lineView=new ImageView(lineIcon);
        lineView.setFitWidth(15);
        lineView.setFitHeight(15);
        drawLine.setGraphic(lineView);

        Tooltip mirrorTip=new Tooltip("mirror canvas");
        Tooltip.install(mirror.getContent(),mirrorTip);
        Tooltip rotateTip=new Tooltip("Rotate canvas");
        Tooltip.install(rotate.getContent(),rotateTip);
        mirror.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mirrorImage(gc);
                MyLogger.logCustomMessage("Image Mirrored");
            }
        });
        paste.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.drawImage(selectedImage,endX,endY);
                MyLogger.logCustomMessage("selection pasted");
            }
        });
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.clearRect(0,0,canvas.getHeight(),canvas.getWidth());
                MyLogger.logCustomMessage("canvas cleared");
            }
        });
        help.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        stage1.show();
                    }
                });
        grabber.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="grab";
                MyLogger.logCustomMessage(shape+ " selected");
            }
        });
        dashBool.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.setLineDashes(4);
                MyLogger.logCustomMessage("dash toggled");
            }
        });
        Width3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                width=3;
                gc.setLineWidth(3);
                MyLogger.logCustomMessage("width 3 selected");
            }
        });
        Width2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                width=2;
                gc.setLineWidth(2);
                MyLogger.logCustomMessage("width 2 selected");
            }
        });
        Width1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.setLineWidth(1);
                width=1;
                MyLogger.logCustomMessage("width 2 selected");
            }
        });
        saveAsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fileHandler.saveAsImage();//saves the image in imageview to this file location
                autosaveTime=10;
                MyLogger.logCustomMessage("image saved");
            }
        });
        addImageButton.setOnAction(e -> drawingManager.addImageFromFile());
        EventHandler<ActionEvent> event =new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fileHandler.openNewWindow();
                stage.close();//closes the scene
                MyLogger.logCustomMessage("stage closed");
            }
        };
        blue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.setStroke(Color.BLUE);
                gc.setFill(Color.BLUE);
                MyLogger.logCustomMessage("blue selected");
            }
        });
        red.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.setStroke(Color.RED);
                gc.setFill(Color.RED);
                MyLogger.logCustomMessage("red selected");
            }
        });
        erase.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.setStroke(Color.WHITE);
                gc.setFill(Color.WHITE);
                MyLogger.logCustomMessage("eraser selected");
            }
        });
        black.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gc.setStroke(Color.BLACK);
                gc.setFill(Color.BLACK);
                MyLogger.logCustomMessage("black selected");
            }
        });
        rgb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                color=picker.getValue();
                gc.setStroke(color);
                gc.setFill(color);
                MyLogger.logCustomMessage("custom color selected");
            }
        });
        rotate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawingManager.drawRotatedImage(gc,canvasToImage(0,0, canvas.getHeight(), canvas.getWidth()),0,0);
                MyLogger.logCustomMessage("image rotated");
            }
        });
        drawLine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="line";
                MyLogger.logCustomMessage(shape+" selected");
            }
        });
        drawPen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="pen";
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        drawCircle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="circle";
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        drawRect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="rect";
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        drawTriangle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="triangle";
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        drawEllipse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="ellipse";
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        drawNGon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="ngon";
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Input Number");
                dialog.setHeaderText("Please enter a number:");
                dialog.setContentText("Number:");

                // Get the response value (user input)
                dialog.showAndWait().ifPresent(result -> {
                    try {
                        sides = Integer.parseInt(result);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                });
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        rotateSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawingManager.drawRotatedImage(gc,  selectedImage,   endX,endY);
                MyLogger.logCustomMessage("selected rotated");

            }
        });
        selector.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="select";
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        textDraw.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shape="text";
                TextInputDialog dialog2 = new TextInputDialog();
                dialog2.setTitle("Input Text");
                dialog2.setHeaderText("Please enter your Text");
                dialog2.setContentText("Text:");
                        dialog2.showAndWait().ifPresent(result -> {
                            try {
                                texter = (result);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please enter valid text.");
                    }
                });
                MyLogger.logCustomMessage(shape+" selected");

            }
        });
        tab1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switchTab(1);
            }
        });
        tab2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switchTab(2);
            }
        });
        tab3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switchTab(3);
            }
        });
        tab4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switchTab(4);
            }
        });
        tab5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switchTab(5);
            }
        });
        tab6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switchTab(6);
            }
        });
        timerToggle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                countdownLabel.setVisible(!countdownLabel.isVisible());
                MyLogger.logCustomMessage("timer toggled");
            }
        });

        Timer autosaveCD = new Timer();
        autosaveCD.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Platform.runLater(()->{
                    if(autosaveTime==-1){
                        countdownLabel.setText(null);
                    }
                    else if(autosaveTime==0) {
                        fileHandler.saveImage();
                        autosaveTime=-1;
                        MyLogger.logCustomMessage("canvas saved");

                    }else{
                        autosaveTime--;
                        countdownLabel.setText("Autosave in "+autosaveTime);
                    }
                });
            }
        },0,1000);

        saveAsButton.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN));
        closeButton.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN));
        clear.setAccelerator(new KeyCodeCombination(KeyCode.K,KeyCodeCombination.CONTROL_DOWN));
        undoButton.setAccelerator(new KeyCodeCombination(KeyCode.Z,KeyCodeCombination.CONTROL_DOWN));
        redoButton.setAccelerator(new KeyCodeCombination(KeyCode.Y,KeyCodeCombination.CONTROL_DOWN));
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V,KeyCodeCombination.CONTROL_DOWN));
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if(shape.equalsIgnoreCase("pen")){
                            gc.lineTo(e.getX(),e.getY());
                            gc.stroke();
                        }
                            if (isDragging) {
                                double offsetX = e.getX() - startX;
                                double offsetY = e.getY() - startY;
                                gc.drawImage(selectedImage, e.getX(), e.getY());
                        }

                    }
                });
        stage.setScene(scene);
        stage.show();

        closeButton.setOnAction(event);
        saveButton.setOnAction(e -> fileHandler.saveAsImage());

    }

    private void handleMousePressed(MouseEvent event) { //gets mouse starting location when held
        startX = event.getX();
        startY = event.getY();
        if(shape.equalsIgnoreCase("pen")){
            gc.beginPath();
            gc.stroke();
        }

        if (selectedImage != null) {
            double selectedImageX = canvas.getWidth() / 2 - selectedImage.getWidth() / 2;
            double selectedImageY = canvas.getHeight() / 2 - selectedImage.getHeight() / 2;
            if (startX >= selectedImageX && startX <= selectedImageX + selectedImage.getWidth()
                    && startY >= selectedImageY && startY <= selectedImageY + selectedImage.getHeight()) {
                isDragging = true;
            }
        }
    }

    /**
     * handles all functions on the mouse released event.
     * reads the shape string and executes the respective function.
     * @param event for mouse location
     */
    private void handleMouseReleased(MouseEvent event) { //hellish shape maker
        double endX = event.getX();
        double endY = event.getY();
        if(shape.equalsIgnoreCase("line")) {
            gc.strokeLine(startX, startY, endX, endY);
            MyLogger.logCustomMessage(shape+" drawn");
        } else if (shape.equalsIgnoreCase("circle")) {
            drawingManager.drawCircle(endX,endY,startX,startY);
            MyLogger.logCustomMessage(shape+" drawn");
        }else if(shape.equalsIgnoreCase("square")){
           drawingManager.drawSquare(endX,endY,startX,startY);
            MyLogger.logCustomMessage(shape+" drawn");
        } else if (shape.equalsIgnoreCase("rect")) {
            drawingManager.drawRectangle(endX,endY,startX,startY);
            MyLogger.logCustomMessage(shape+" drawn");
        }else if(shape.equalsIgnoreCase("triangle")){
            drawingManager.drawTriangle(endX,endY,startX,startY);
            MyLogger.logCustomMessage(shape+" drawn");
        }else if (shape.equalsIgnoreCase("ellipse")){
            drawingManager.drawEllipse(endX,endY,startX,startY);
            MyLogger.logCustomMessage(shape+" drawn");
        } else if (shape.equalsIgnoreCase("grab")) {
            Image snapshot = canvas.snapshot(new SnapshotParameters(), null);
            PixelReader reader=snapshot.getPixelReader();
            gc.setStroke(reader.getColor((int)endX,(int)endY));
            gc.setFill(reader.getColor((int)endX,(int)endY));
            MyLogger.logCustomMessage("color grabbed");
        }else if (shape.equalsIgnoreCase("ngon")){
            drawingManager.drawNgon(sides,endX,endY,startX,startY);
            MyLogger.logCustomMessage(shape+" drawn");
        }else if(shape.equalsIgnoreCase("select")){
            double minX = Math.min(startX, endX);
            double minY = Math.min(startY, endY);
            double maxX = Math.max(startX, endX);
            double maxY = Math.max(startY, endY);
            double width = maxX - minX;
            double height = maxY - minY;
            selectedImage = new WritableImage((int) width, (int) height);
            PixelReader pixelReader = canvas.snapshot(null, null).getPixelReader();
            for (int y = 0; y < height-2; y++) {
                for (int x = 0; x < width-2; x++) {
                    Color color = pixelReader.getColor((int) (minX + x), (int) (minY + y));
                    selectedImage.getPixelWriter().setColor(x, y, color);
                    shape="null";
                }
            } MyLogger.logCustomMessage("section selected");
        }else if(shape.equalsIgnoreCase("text")){
            gc.strokeText(texter,event.getX(),event.getY());
            MyLogger.logCustomMessage(shape+" drawn");
        }
        Image currentState = canvasToImage(0,0,canvas.getHeight(),canvas.getWidth());
        undoStack.push(currentState);
        isDragging=false;
        autosaveTime=10;
    }

    public void switchTab(int newTab){
        WritableImage temp=(canvas.snapshot(null,null));
        tabs.set(currentTab-1,temp);
        currentTab=newTab;
        gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        gc.drawImage(tabs.get(newTab-1),0,0);
        MyLogger.logCustomMessage("switched to tab "+currentTab);
    }

    public void mirrorImage(GraphicsContext gc){
        gc.drawImage(canvasToImage(0,0,canvas.getHeight(),canvas.getWidth()), 0,0,canvas.getWidth(), canvas.getHeight(),canvas.getWidth(),0,-canvas.getWidth(),canvas.getHeight());
    }
}