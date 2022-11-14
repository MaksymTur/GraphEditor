package grapheditor.model;

//Edge with start node, end node, length, spring constant
public class Edge {
    private Node startNode;
    private Node endNode;
    private double length;
    private final double springConstant;

    public Edge(Node startNode, Node endNode, double length, double springConstant) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.length = length;
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

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getSpringConstant() {
        return springConstant;
    }
    public double getStretch() {
        return Node.distance(startNode, endNode) / length;
    }
}
