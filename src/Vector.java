/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
* @author Pavel Malkov
 */
public class Vector {

    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double length(Vector v) {
        return Math.sqrt(v.x * v.x + v.y * v.y);
    }

    public static Vector scaleToOne(Vector v) {
        double lengthX = v.x / Math.sqrt(v.x * v.x + v.y * v.y);
        double lengthY = v.y / Math.sqrt(v.x * v.x + v.y * v.y);

        return new Vector(lengthX, lengthY);
    }

    public static Vector scaleToLength(Vector v, double scale) {
        double lengthX = scale * scaleToOne(v).x;
        double lengthY = scale * scaleToOne(v).y;

        return new Vector(lengthX, lengthY);
    }

    public static Vector subtract(Vector v1, Vector v2) {
        double vx = v1.x - v2.x;
        double vy = v1.y - v2.y;

        return new Vector(vx, vy);
    }

    public static Vector addUp(Vector v1, Vector v2) {
        double vx = v1.x + v2.x;
        double vy = v1.y + v2.y;

        return new Vector(vx, vy);
    }
    
    public static Vector rotateByAngle(Vector v, double alpha) {
        Double rx, ry;
        
        rx = v.x * Math.cos(alpha) - v.y * Math.sin(alpha);
        ry = v.x * Math.sin(alpha) + v.y * Math.cos(alpha);
        
        return new Vector(rx, ry);   
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
   public void setY(double y) {
        this.y = y;
    }
}
