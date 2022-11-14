package grapheditor.model;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;

//Node with x, y, x speed, y speed, mass, color and magnetism
public class Node {
    private double x;
    private double y;
    private double xSpeed;
    private double ySpeed;
    private final double mass;
    private final DoubleProperty magnetism;
    private final Color color;

    private boolean isDragged;
    private boolean isFixed;

    public Node(double x, double y, double xSpeed, double ySpeed, double mass, DoubleProperty magnetism) {
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

    public Node(double x, double y, double mass, DoubleProperty magnetism) {
        this(x, y, 0, 0, mass, magnetism);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
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
        this.xSpeed = xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public void setYSpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    public double getMass() {
        return mass;
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
        this.isFixed = fixed;
    }

    public void move(double passed) {
        x += xSpeed * passed;
        y += ySpeed * passed;
    }

    private void accelerate(double xAcceleration, double yAcceleration, double passed) {
        xSpeed += xAcceleration * passed;
        ySpeed += yAcceleration * passed;
    }
    public void applyForce(double xForce, double yForce, double passed) {
        accelerate(xForce / mass, yForce / mass, passed);
    }

    public static double distance(Node node1, Node node2) {
        return Math.sqrt(Math.pow(node1.getX() - node2.getX(), 2) + Math.pow(node1.getY() - node2.getY(), 2));
    }
    public static double angle(Node node1, Node node2) {
        return Math.atan2(node2.getY() - node1.getY(), node2.getX() - node1.getX());
    }

    public boolean isInside(double x, double y, double radius) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2)) < radius;
    }
}
