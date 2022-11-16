package grapheditor.controller;

import grapheditor.model.Edge;
import grapheditor.model.Graph;
import grapheditor.model.Node;
import grapheditor.view.View;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    private static final IntegerProperty recalcsPerFrame = new SimpleIntegerProperty(50);
    //Main
    @FXML
    private Canvas canvas;
    private Graph graph;
    private View view;
    @FXML
    private VBox canvasContainer;

    //Menu
    ////Graph generation
    @FXML
    private ScrollPane graphGenScroll;
    @FXML
    private TextField nodeNumberField;
    @FXML
    private TextArea edgesField;
    ////Physics
    //////Node
    @FXML
    private Slider magnetizeSlider;
    @FXML
    private Slider massSlider;
    @FXML
    private CheckBox bounceCheckBox;
    @FXML
    private Slider energyKeepSlider;
    //////Edge
    @FXML
    private Slider edgeLenSlider;
    @FXML
    private Slider dumpingSlider;
    @FXML
    private Slider springSlider;
    //////Environment
    @FXML
    private Slider wallForceSlider;
    @FXML
    private Slider airFrictionSlider;
    @FXML
    private Slider gravitySlider;
    @FXML
    private CheckBox bordersCheckBox;
    ////Appearance
    //////Node
    @FXML
    private Slider nodeRadiusSlider;
    //////Edge
    //////Other
    @FXML
    private Slider recalcsPerFrameSlider;

    //Mouse
    private double mouseXPos;
    private double mouseYPos;
    private double lastTime;

    private int getNextBlank(String text, int pos) {
        while (pos < text.length() && text.charAt(pos) != ' ' && text.charAt(pos) != '\n') {
            pos++;
        }
        return pos;
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        int nodeNumber = Integer.parseInt(nodeNumberField.getText());
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < nodeNumber; i++) {
            nodes.add(new Node(random.nextInt((int) canvas.getWidth()+1), random.nextInt((int) canvas.getHeight()+1),
                    Graph.defaultNodeMass, Graph.defaultNodeMagnetism));
        }
        for (int i = 0; i < edgesField.getText().length(); i++) {
            int start = Integer.parseInt(edgesField.getText().substring(i, i = getNextBlank(edgesField.getText(), i)));
            int end = Integer.parseInt(edgesField.getText().substring(i + 1, i = getNextBlank(edgesField.getText(), i + 1)));
            edges.add(new Edge(nodes.get(start - 1), nodes.get(end - 1), Graph.defaultEdgeLength, Graph.defaultSpringStart, Graph.defaultEdgeDumpingConstant));
        }
        graph = new Graph(nodes, edges, (int) canvas.getWidth(), (int) canvas.getHeight());
    }

    @FXML
    public void onDoubleClick(MouseEvent event){
        if(event.getButton() == javafx.scene.input.MouseButton.PRIMARY
        && event.getClickCount() == 2){
            for(Node node : graph.getNodes()){
                if(node.isInside(event.getX(), event.getY(), Graph.nodeRadius.get())){
                    node.setFixed(!node.isFixed());
                    break;
                }
            }
        }
    }
    @FXML
    public void lMousePressed(MouseEvent event) {
        if(event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
            for (Node node : graph.getNodes()) {
                if (node.isInside(event.getX(), event.getY(), Graph.nodeRadius.get())) {
                    node.setDragged(true);
                    break;
                }
            }
            mouseXPos = event.getX();
            mouseYPos = event.getY();
            lastTime = System.nanoTime();
        }
    }

    @FXML
    public void lMouseReleased(MouseEvent event) {
        if(event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
            for (Node node : graph.getNodes()) {
                node.setDragged(false);
            }
        }
    }

    @FXML
    public void lMouseDragged(MouseEvent event) {
        if(event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
            for (Node node : graph.getNodes()) {
                if (node.isDragged()) {
                    node.setX(node.getX() + event.getX() - mouseXPos);
                    node.setY(node.getY() + event.getY() - mouseYPos);
                    double time = (System.nanoTime() - lastTime) / 1000000000.0;
                    node.setXSpeed((event.getX() - mouseXPos) / time);
                    node.setYSpeed((event.getY() - mouseYPos) / time);
                }
            }
            mouseXPos = event.getX();
            mouseYPos = event.getY();
            lastTime = System.nanoTime();
        }
    }

    @FXML
    private void initialize() {
        graphGenScroll.setFitToHeight(true);
        graphGenScroll.setFitToWidth(true);
        view = new View(canvas);
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());

        magnetizeSlider.valueProperty().bindBidirectional(Graph.defaultNodeMagnetism);
        edgeLenSlider.valueProperty().bindBidirectional(Graph.defaultEdgeLength);
        massSlider.valueProperty().bindBidirectional(Graph.defaultNodeMass);
        dumpingSlider.valueProperty().bindBidirectional(Graph.defaultEdgeDumpingConstant);
        springSlider.maxProperty().bind(Graph.defaultEdgeLength);
        springSlider.valueProperty().bindBidirectional(Graph.defaultSpringStart);
        wallForceSlider.valueProperty().bindBidirectional(Graph.wallForce);
        airFrictionSlider.valueProperty().bindBidirectional(Graph.airFriction);
        gravitySlider.valueProperty().bindBidirectional(Graph.g);
        bordersCheckBox.selectedProperty().bindBidirectional(Graph.bordersOn);
        nodeRadiusSlider.valueProperty().bindBidirectional(Graph.nodeRadius);
        bounceCheckBox.selectedProperty().bindBidirectional(Graph.toBounce);
        energyKeepSlider.valueProperty().bindBidirectional(Graph.energyKeep);
        recalcsPerFrameSlider.valueProperty().bindBidirectional(recalcsPerFrame);
        //bind maxSpeed to recalcsPerFrame * 1000
        recalcsPerFrameSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node.maxSpeed.set(newValue.doubleValue() * 100);
        });


        new AnimationTimer() {
            long prevTime = System.nanoTime();

            @Override
            public void handle(long now) {
                double t = (now - prevTime) / 1000000000.0;
                prevTime = now;
                if(graph != null) {
                    for(int i = 0; i < recalcsPerFrame.get(); i++) {
                        graph.recalculate(t / recalcsPerFrame.get());
                    }
                    view.draw(graph);
                }
            }
        }.start();
    }
}