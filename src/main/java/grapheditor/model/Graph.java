package grapheditor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.List;

import static java.lang.Math.*;

//Graph with nodes and edges with ability to recalculate node accelerations and speeds and air friction
public class Graph {
    public final static double defaultNodeMass = 1;
    public final static DoubleProperty defaultNodeMagnetism = new SimpleDoubleProperty(500);
    public final static double defaultEdgeLength = 40;
    public final static double defaultEdgeSpringConstant = 45;
    private final static double maxForce = 100;
    private final static double wallForce = 0;
    private final static double minDistance = 1;
    private final static double airFriction = 1;
    public final float nodeRadius = 10;
    private final static double g = 500;
    private List<Node> nodes;
    private List<Edge> edges;
    private int width;
    private int height;
    private final static boolean bordersOn = true;

    public Graph(List<Node> nodes, List<Edge> edges, int width, int height) {
        this.nodes = nodes;
        this.edges = edges;
        this.width = width;
        this.height = height;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    //Recalculate node accelerations and speeds
    public void recalculate(double passed) {
        //for every pair of nodes push them from each other with their magnetisms
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                double distance = Node.distance(nodes.get(i), nodes.get(j));
                double angle = Node.angle(nodes.get(i), nodes.get(j));
                double force = -min(maxForce, nodes.get(i).getMagnetism() * nodes.get(j).getMagnetism() /
                        (max(distance, minDistance) * max(distance, minDistance)));
                double xForce = force * Math.cos(angle);
                double yForce = force * Math.sin(angle);
                nodes.get(i).applyForce(xForce, yForce, passed);
                nodes.get(j).applyForce(-xForce, -yForce, passed);
            }
        }

        for (Edge edge : edges) {
            //for every edge pull nodes to each other with their spring constants
            double distance = Node.distance(edge.getStartNode(), edge.getEndNode());
            if (distance < edge.getLength()) {
                continue;
            }
            double angle = Node.angle(edge.getStartNode(), edge.getEndNode());
            double force = -edge.getSpringConstant() * (distance - edge.getLength());
            double xForce = force * Math.cos(angle);
            double yForce = force * Math.sin(angle);
            edge.getStartNode().applyForce(-xForce, -yForce, passed);
            edge.getEndNode().applyForce(xForce, yForce, passed);
        }
        for (Node node : nodes) {
            //apply air friction
            node.applyForce(-airFriction * node.getXSpeed(), -airFriction * node.getYSpeed(), passed);
        }
        //apply gravity
        for (Node node : nodes) {
            node.applyForce(0, g * node.getMass(), passed);
        }

        //borders of screen should push nodes to center

        for (Node node : nodes) {
            node.applyForce(min(maxForce, wallForce / pow(abs(node.getX()), 2)),
                    min(maxForce, wallForce / pow(abs(node.getY()), 2)), passed);
            node.applyForce(-min(maxForce, wallForce / pow(abs(width - node.getX()), 2)),
                    -min(maxForce, wallForce / pow(abs(height - node.getY()), 2)), passed);
        }

        for (Node node : nodes) {
            if (node.isDragged() || node.isFixed()) {
                continue;
            }
            node.move(passed);
        }
        for (Node node : nodes) {
            if (bordersOn) {
                if (node.getX() - nodeRadius < 0) {
                    node.setX(nodeRadius);
                    node.setXSpeed(-node.getXSpeed());
                }
                if (node.getX() + nodeRadius > width) {
                    node.setX(width - nodeRadius);
                    node.setXSpeed(-node.getXSpeed());
                }
                if (node.getY() - nodeRadius < 0) {
                    node.setY(nodeRadius);
                    node.setYSpeed(-node.getYSpeed());
                }
                if (node.getY() + nodeRadius > height) {
                    node.setY(height - nodeRadius);
                    node.setYSpeed(-node.getYSpeed());
                }
            }
        }
    }
}
