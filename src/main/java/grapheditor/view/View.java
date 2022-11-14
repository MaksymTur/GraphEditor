package grapheditor.view;

import grapheditor.model.Edge;
import grapheditor.model.Graph;
import grapheditor.model.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static java.lang.Math.max;

public class View {
    private final Canvas canvas;

    public View(Canvas canvas) {
        this.canvas = canvas;
    }

    //Draw nodes and edges
    public void draw(Graph graph) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Edge edge : graph.getEdges()) {
            gc.setStroke(Color.BLACK);
            gc.strokeLine(edge.getStartNode().getX(), edge.getStartNode().getY(),
                    edge.getEndNode().getX(), edge.getEndNode().getY());
        }
        for (Node node : graph.getNodes()) {
            gc.setFill(node.getColor());
            gc.fillOval(node.getX() - graph.nodeRadius, node.getY() - graph.nodeRadius, graph.nodeRadius * 2, graph.nodeRadius * 2);
        }
    }
}
