package grapheditor.model;

import javafx.beans.property.DoubleProperty;

//Edge with start node, end node, length, spring constant
public class Edge {
    //Properties
    private final DoubleProperty stretchStart;
    private final DoubleProperty springStart;
    private final DoubleProperty springConstant;

    //Fields
    private Node startNode;
    private Node endNode;
    public Edge(Node startNode, Node endNode, DoubleProperty stretchStart, DoubleProperty springStart, DoubleProperty springConstant) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.stretchStart = stretchStart;
        this.springStart = springStart;
        this.springConstant = springConstant;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public double getStretchStart() {
        return stretchStart.get();
    }

    public void setStretchStart(double stretchStart) {
        this.stretchStart.set(stretchStart);
    }

    public double getSpringConstant() {
        return springConstant.get();
    }

    public double getSpringStart() {
        return springStart.get();
    }
    public double getStretch() {
        double distance = Node.distance(startNode, endNode);
        if(distance > getStretchStart()){
            return distance - getStretchStart();
        }else if(distance < getSpringStart()){
            return distance - getSpringStart();
        }else{
            return 0;
        }
    }

}
