package grapheditor.model;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.isNaN;
import static java.lang.Math.min;

//Node with x, y, x speed, y speed, mass, color and magnetism
public class Node {

    //Constants
    private final static double maxSpeed = 30000;

    //Properties
    private final DoubleProperty mass;
    private final DoubleProperty magnetism;
    private final Color color;

    //Fields
    private double x;
    private double y;
    private double xSpeed;
    private double ySpeed;
    private boolean isDragged;
    private boolean isFixed;

    //Methods
    public Node(double x, double y, double xSpeed, double ySpeed, DoubleProperty mass, DoubleProperty magnetism) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.mass = mass;
        this.magnetism = magnetism;
        this.color = Color.hsb(Math.random() * 360, 1.0, 1.0);
        isDragged = false;
        isFixed = false;
    }

    public Node(double x, double y, DoubleProperty mass, DoubleProperty magnetism) {
        this(x, y, 0, 0, mass, magnetism);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setPhysicX(double x){
        if(!isFixed() && !isDragged()){
            setX(x);
        }
    }

    public void setPhysicY(double y){
        if(!isFixed() && !isDragged()){
            setY(y);
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public void setXSpeed(double xSpeed) {
        if(!isFixed())
            this.xSpeed = min(xSpeed, maxSpeed);
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public void setYSpeed(double ySpeed) {
        if(!isFixed())
            this.ySpeed = min(ySpeed, maxSpeed);
    }

    public double getMass() {
        return isFixed() ? 100000000000000000000.0 :mass.get();
    }

    public double getMagnetism() {
        return magnetism.get();
    }

    public Color getColor() {
        return color;
    }

    public boolean isDragged() {
        return isDragged;
    }

    public void setDragged(boolean dragged) {
        this.isDragged = dragged;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        this.setXSpeed(0);
        this.setYSpeed(0);
        this.isFixed = fixed;
    }

    public void move(double passed) {
        setPhysicX(getX() + xSpeed * passed);
        setPhysicY(getY() + ySpeed * passed);
        //System.out.println("xSpeed: " + xSpeed + " ySpeed: " + ySpeed);
    }

    private void accelerate(double xAcceleration, double yAcceleration, double passed) {
        setXSpeed(getXSpeed() + xAcceleration * passed);
        setYSpeed(getYSpeed() + yAcceleration * passed);
        //System.out.println("xAcceleration: " + xAcceleration + " yAcceleration: " + yAcceleration);
    }
    public void applyForce(double xForce, double yForce, double passed) {
        //System.out.println("xForce: " + xForce + " yForce: " + yForce);
        accelerate(xForce / mass.get(), yForce / mass.get(), passed);
    }

    public static double distance(Node node1, Node node2) {
        return Math.sqrt(Math.pow(node1.getX() - node2.getX(), 2) + Math.pow(node1.getY() - node2.getY(), 2));
    }
    public static double angle(Node node1, Node node2) {
        return Math.atan2(node2.getY() - node1.getY(), node2.getX() - node1.getX());
    }

    public boolean isInside(double x, double y, double radius) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2)) < radius;
    }
}
