package grapheditor.view;

import grapheditor.model.Edge;
import grapheditor.model.Graph;
import grapheditor.model.Node;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class View {
    private final Canvas canvas;

    //Nodes
    public static final BooleanProperty chromatic = new SimpleBooleanProperty(false);
    public static final BooleanProperty showNumbers = new SimpleBooleanProperty(false);
    public static final BooleanProperty nodeBorders = new SimpleBooleanProperty(false);
    public static final IntegerProperty traceSize = new SimpleIntegerProperty(1);
    //Edges
    public static final IntegerProperty edgeWidth = new SimpleIntegerProperty(1);
    public static final BooleanProperty showEdges = new SimpleBooleanProperty(true);

    private static class NodeTrace {
        double x;
        double y;
        double radius;
        Color color;

        NodeTrace(double x, double y, double radius, Color color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }

        NodeTrace(Node node) {
            this(node.getX(), node.getY(), node.getRadius(), node.getColor());
        }
    }

    //remembered traces of nodes
    private final Deque<List<NodeTrace>> traces = new ArrayDeque<>();

    public View(Canvas canvas) {
        this.canvas = canvas;
    }

    //Draw nodes and edges
    public void draw(Graph graph) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //draw traces
        int i = traces.size();
        for (var tracesI : traces) {
            i -= 1;
            //gc.setGlobalAlpha(1.0 - (double) i / (traceSize.get()+1));
            for (NodeTrace trace : tracesI) {
                gc.setFill(Color.hsb(trace.color.getHue(), trace.color.getSaturation() * (1.0 - (double) i / (traceSize.get() + 1)), trace.color.getBrightness()));
                gc.fillOval(trace.x - trace.radius, trace.y - trace.radius, trace.radius * 2, trace.radius * 2);
            }
        }
        gc.setGlobalAlpha(1.0);


        // draw edges
        for (Edge edge : graph.getEdges()) {
            if (showEdges.get()) {
                gc.setLineWidth(edgeWidth.get());
                gc.strokeLine(edge.getStartNode().getX(), edge.getStartNode().getY(), edge.getEndNode().getX(), edge.getEndNode().getY());
            }
        }

        //draw nodes with traces
        for (Node node : graph.getNodes()) {
            gc.setFill(node.getColor());
            if (chromatic.get()) {
                gc.setFill(node.getColor());
            } else {
                gc.setFill(Color.BLACK);
            }
            gc.fillOval(node.getX() - node.getRadius(), node.getY() - node.getRadius(), node.getRadius() * 2, node.getRadius() * 2);
            if (nodeBorders.get()) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(node.getRadius() / 10);
                gc.strokeOval(node.getX() - node.getRadius(), node.getY() - node.getRadius(), node.getRadius() * 2, node.getRadius() * 2);
            }
            if (showNumbers.get()) {
                if (chromatic.get()) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.setFont(javafx.scene.text.Font.font(node.getRadius()));
                gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                gc.fillText(Integer.toString(node.getNumber()), node.getX(), node.getY() + node.getRadius() / 3);
            }

        }
        //add nodes to traces
        traces.addLast(graph.getNodes().stream().map(NodeTrace::new).toList());
        while (traces.size() > traceSize.get()) {
            traces.removeFirst();
        }
//            gc.setFill(node.getColor());
//            gc.fillOval(node.getX() - Graph.nodeRadius.get(), node.getY() - Graph.nodeRadius.get(), Graph.nodeRadius.get() * 2, Graph.nodeRadius.get() * 2);
    }
}
