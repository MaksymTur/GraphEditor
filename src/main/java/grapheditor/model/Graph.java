package grapheditor.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.List;

import static java.lang.Math.*;

//Graph with nodes and edges with ability to recalculate node accelerations and speeds and air friction
public class Graph {

    //Constants
    private final static double minDistance = 1;

    //Properties
    ////Node
    public final static DoubleProperty defaultNodeMass = new SimpleDoubleProperty(1);
    public final static DoubleProperty defaultNodeMagnetism = new SimpleDoubleProperty(500);
    public final static BooleanProperty toBounce = new SimpleBooleanProperty(true);
    ////Edge
    public final static DoubleProperty defaultEdgeLength = new SimpleDoubleProperty(40);
    public final static DoubleProperty defaultEdgeDumpingConstant = new SimpleDoubleProperty(45);
    public final static DoubleProperty defaultSpringStart = new SimpleDoubleProperty(0);
    ////Environment
    public final static DoubleProperty wallForce = new SimpleDoubleProperty(100000);
    public final static DoubleProperty airFriction = new SimpleDoubleProperty(1);
    public final static DoubleProperty g = new SimpleDoubleProperty(1000);
    public final static BooleanProperty bordersOn = new SimpleBooleanProperty(true);
    ////Appearance
    public final static DoubleProperty nodeRadius = new SimpleDoubleProperty(8);
    public final static DoubleProperty energyKeep = new SimpleDoubleProperty(0.9);

    //Fields
    private List<Node> nodes;
    private List<Edge> edges;
    private int width;
    private int height;

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
//                System.out.println("maxForce: " + maxForce);
//                System.out.println("distance: " + distance);
//                System.out.println("minDistance: " + minDistance);
                double force = -nodes.get(i).getMagnetism() * nodes.get(j).getMagnetism() /
                        (max(distance, minDistance) * max(distance, minDistance));
//                System.out.println("force: " + force);
                double xForce = force * Math.cos(angle);
                double yForce = force * Math.sin(angle);
                nodes.get(i).applyForce(xForce, yForce, passed);
                nodes.get(j).applyForce(-xForce, -yForce, passed);
            }
        }

        for (Edge edge : edges) {
            //for every edge pull nodes to each other with their spring constants
            double angle = Node.angle(edge.getStartNode(), edge.getEndNode());
            double force = -edge.getSpringConstant() * edge.getStretch();
            double xForce = force * Math.cos(angle);
            double yForce = force * Math.sin(angle);
            edge.getStartNode().applyForce(-xForce, -yForce, passed);
            edge.getEndNode().applyForce(xForce, yForce, passed);
        }
        for (Node node : nodes) {
            //apply air friction
            node.applyForce(-airFriction.get() * node.getXSpeed(), -airFriction.get() * node.getYSpeed(), passed);
        }
        //apply gravity
        for (Node node : nodes) {
            node.applyForce(0, g.get() * node.getMass(), passed);
        }

        //borders of screen should push nodes to center

        for (Node node : nodes) {
            node.applyForce(wallForce.get() / pow(abs(node.getX()), 2),
                    wallForce.get() / pow(abs(node.getY()), 2), passed);
            node.applyForce(-wallForce.get() / pow(abs(width - node.getX()), 2),
                    -wallForce.get() / pow(abs(height - node.getY()), 2), passed);
        }

        //bounce nodes from each other with impulse and energy loss
        if(toBounce.get()){
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = i + 1; j < nodes.size(); j++) {
                    double distance = Node.distance(nodes.get(i), nodes.get(j));
                    if(distance < 2 * nodeRadius.get()){
                        double angle = Node.angle(nodes.get(i), nodes.get(j));
                        double u1 = nodes.get(i).getXSpeed() * Math.cos(angle) + nodes.get(i).getYSpeed() * Math.sin(angle);
                        double u2 = nodes.get(j).getXSpeed() * Math.cos(angle) + nodes.get(j).getYSpeed() * Math.sin(angle);
                        double m1 = nodes.get(i).getMass();
                        double m2 = nodes.get(j).getMass();

                        double u = (m1*u1 + m2*u2)/(m1+m2);
                        u1 -= u;
                        u2 -= u;

                        double v1 = (1 / (m1 * (m1 + m2))) *
                                (-sqrt(m1 * m2 * ((energyKeep.get() - 1) * m1 * m1 * u1 * u1 + m1 * m2 *
                                (energyKeep.get() * (u1 * u1 + u2 * u2) - 2 * u1 * u2) + (energyKeep.get() - 1) * m2 * m2 * u2 * u2)) +
                                m1 * m1 * u1 + m1 * m2 * u2);
                        double v2 = (1 / (m2 * (m1 + m2))) *
                                (sqrt(m1 * m2 * ((energyKeep.get() - 1) * m1 * m1 * u1 * u1 + m1 * m2 *
                                (energyKeep.get() * (u1 * u1 + u2 * u2) - 2 * u1 * u2) + (energyKeep.get() - 1) * m2 * m2 * u2 * u2)) +
                                m1 * m2 * u1 + m2 * m2 * u2);

//                        double v1 = (m1 - m2) / (m1 + m2) * u1 + 2 * m2 / (m1 + m2) * u2;
//                        double v2 = 2 * m1 / (m1 + m2) * u1 + (m2 - m1) / (m1 + m2) * u2;

                        v1 += u;
                        v2 += u;

                        u1 += u;
                        u2 += u;

                        nodes.get(i).setXSpeed(nodes.get(i).getXSpeed() + (v1 - u1) * Math.cos(angle));
                        nodes.get(i).setYSpeed(nodes.get(i).getYSpeed() + (v1 - u1) * Math.sin(angle));
                        nodes.get(j).setXSpeed(nodes.get(j).getXSpeed() + (v2 - u2) * Math.cos(angle));
                        nodes.get(j).setYSpeed(nodes.get(j).getYSpeed() + (v2 - u2) * Math.sin(angle));

//                        double xImpulse = 2 * (nodes.get(i).getXSpeed() * nodes.get(i).getMass()
//                                + nodes.get(j).getXSpeed() * nodes.get(j).getMass()) /
//                                (nodes.get(i).getMass() + nodes.get(j).getMass());
//                        double yImpulse = 2 * (nodes.get(i).getYSpeed() * nodes.get(i).getMass()
//                                + nodes.get(j).getYSpeed() * nodes.get(j).getMass()) /
//                                (nodes.get(i).getMass() + nodes.get(j).getMass());
//                        nodes.get(i).setXSpeed(xImpulse * nodes.get(i).getMass());
//                        nodes.get(i).setYSpeed(yImpulse * nodes.get(i).getMass());
//                        nodes.get(j).setXSpeed(-xImpulse * nodes.get(j).getMass());
//                        nodes.get(j).setYSpeed(-yImpulse * nodes.get(j).getMass());
                    }
                }
            }
        }


        for (Node node : nodes) {
            node.move(passed);
        }
        //forbid node intersection
        if(toBounce.get()){
            for(Node node : nodes){
                for(Node node1 : nodes){
                    if(node != node1){
                        double distance = Node.distance(node, node1);
                        if(distance < 2 * nodeRadius.get()){
                            double angle = Node.angle(node, node1);
                            double dist = (nodeRadius.get() - distance / 2);
                            node.setPhysicX(node.getX() - dist * Math.cos(angle));
                            node.setPhysicY(node.getY() - dist * Math.sin(angle));
                            node1.setPhysicX(node1.getX() + dist * Math.cos(angle));
                            node1.setPhysicY(node1.getY() + dist * Math.sin(angle));
                        }
                    }
                }
            }
        }
        for (Node node : nodes) {
            if (bordersOn.get()) {
                if (node.getX() - nodeRadius.get() < 0) {
                    node.setX(nodeRadius.get());
                    node.setXSpeed(-node.getXSpeed() * sqrt(energyKeep.get()));
                }
                if (node.getX() + nodeRadius.get() > width) {
                    node.setX(width - nodeRadius.get());
                    node.setXSpeed(-node.getXSpeed() * sqrt(energyKeep.get()));
                }
                if (node.getY() - nodeRadius.get() < 0) {
                    node.setY(nodeRadius.get());
                    node.setYSpeed(-node.getYSpeed() * sqrt(energyKeep.get()));
                }
                if (node.getY() + nodeRadius.get() > height) {
                    node.setY(height - nodeRadius.get());
                    node.setYSpeed(-node.getYSpeed() * sqrt(energyKeep.get()));
                }
            }
        }
    }
}
