
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Pavel Malkov
 */
public class Simulation implements MathPainter, Runnable {

    private Thread step;
    public JMath animation;
    protected boolean next;
    protected boolean hit = false;
    private long timePassed;
    private long stepTime;
    public PropertyChangeSupport pcs;
    private Double alpha;
    public boolean mouseControlEnabled = false;
    public double animationScale = 1;
    protected double stepLengthRunner = 0.01;
    protected double stepLengthChaserBlue = 0.01;
    protected double stepLengthChaserRed = 0.01;
    protected double riverCurrentVelocity = 0.005;
    private double stepLengthRiver = 0.001;
    protected double radius = 0.15;
    private double timeScale = 1;
    private Graphics2D g;
    private final double guesswork = 65.0; //Faulheit
    private double boundingBox_x, boundingBox_y;
    protected ArrayList<Vector> dataRunner = new ArrayList<>();
    protected ArrayList<Vector> dataChaserBlue = new ArrayList<>();
    protected ArrayList<Vector> dataChaserRed = new ArrayList<>();
    protected boolean chaserBlueEnabled = true;
    protected boolean chaserRedEnabled = true;
    protected boolean paintRunnerDirection = false;
    protected boolean paintRunnerDirectionDone = true;
    protected boolean riverEnabled = false;
    protected String runnerMode = "Gerade";
    private Vector runnerDirection;
    protected Line2D.Double runnerDirectionLine = new Line2D.Double();

    //Declaraions pulled out of run()
    private double hitbox = 0.03;
    private double nextTrigonX, nextTrigonY;
    private double amplitude;
    private double scale;
    private Vector nextChaserRed;
    private Vector nextChaserBlue;
    private Vector nextRunner;
    private Vector stepRunner;
    private Vector stepChaserRed;
    private Vector stepChaserBlue;

    //Declarations pulled out of paint()
    private Rectangle2D.Double rectangle;
    private Line2D.Double line;
    private Ellipse2D.Double circle;

//    private long timeStop = System.currentTimeMillis();
//    private long timeStart = System.currentTimeMillis();
//    private long timeBetweenRepaints = 0;
    
    /**
     * @return the timeScale
     */
    protected double getTimeScale() {
        return timeScale;
    }

    public Simulation(JMath anim) {
        this.animation = anim;
        anim.setEnableMouse(false);
        pcs = new PropertyChangeSupport(this);
    }

    
    public void addPropertyChangeListener(PropertyChangeListener interessent) {
        pcs.addPropertyChangeListener(interessent);
    }

    public void removePropertyChangeListener(PropertyChangeListener interessent) {
        pcs.addPropertyChangeListener(interessent);
    }

    protected void fillCircle(Vector lowerLeftCorner, double radius) {
        circle = new Ellipse2D.Double(lowerLeftCorner.getX() - radius, lowerLeftCorner.getY() - radius,
                2 * radius, 2 * radius);
        g.fill(circle);
    }

    protected void drawCircle(Vector lowerLeftCorner, double radius) {
        circle = new Ellipse2D.Double(lowerLeftCorner.getX() - radius, lowerLeftCorner.getY() - radius,
                2 * radius, 2 * radius);
        g.draw(circle);
    }

    protected void drawLine(Vector startingPoint, Vector endPoint) {
        line = new Line2D.Double(startingPoint.getX(), startingPoint.getY(),
                endPoint.getX(), endPoint.getY());
        g.draw(line);
    }

    protected void drawLine(Line2D.Double line) {
        g.draw(line);
    }

    protected void drawRectangle(double x1, double y1, double x2, double y2) {
        rectangle = new Rectangle2D.Double(x1, y1, x2, y2);
        g.draw(rectangle);
    }

    protected void drawBoundlingBox() {
        rectangle = new Rectangle2D.Double(-animation.getWidth() / guesswork, -animation.getHeight() / guesswork,
                2 * animation.getWidth() / guesswork,
                2 * animation.getHeight() / guesswork);
        g.draw(rectangle);
    }

    protected void plotCoordinateList(ArrayList<Vector> list, double radius) {
        for (int i = 0; i < (list.size() - 1); i++) {
            drawLine(list.get(i), list.get(i + 1));
        }
        fillCircle(list.get(list.size() - 1), radius);
    }

    private void paintRiver() {
        g.setColor(Color.cyan);
        drawRectangle(-boundingBox_x, -boundingBox_y + 0.3 * boundingBox_y, 2 * boundingBox_x, boundingBox_y - 0.6 * boundingBox_y);
    }

    @Override
    public void mathPaint(Graphics2D g) {
        this.g = g;
        animation.setZero(animation.getWidth() / 2, animation.getHeight() / 2);
        animation.setBackground(Color.darkGray);

        if (mouseControlEnabled && paintRunnerDirection && (runnerMode.equals("Gerade") || runnerMode.equals("Sinus"))) {
            g.setColor(Color.lightGray);
            drawLine(runnerDirectionLine);
        }
        if (!next && !hit) {
            if (paintRunnerDirectionDone && (runnerMode.equals("Gerade") || runnerMode.equals("Sinus"))) {
                g.setColor(Color.lightGray);
                drawLine(runnerDirectionLine);
            }
        }

        g.setColor(Color.green);
        drawBoundlingBox();

        if (riverEnabled) {
            paintRiver();
        }

        if (dataRunner.size() > 1) {
            g.setColor(Color.white);
            plotCoordinateList(dataRunner, radius);
        } else if (dataRunner.size() == 1) {
            g.setColor(Color.white);
            fillCircle(dataRunner.get(0), radius);
        }
        if (chaserBlueEnabled) {
            if (dataChaserBlue.size() > 1) {
                g.setColor(Color.blue);
                plotCoordinateList(dataChaserBlue, radius);
            } else if (dataChaserBlue.size() == 1) {
                g.setColor(Color.blue);
                fillCircle(dataChaserBlue.get(0), radius);
            }
        } else if (!dataChaserBlue.isEmpty()) {
            g.setColor(Color.blue);
            drawCircle(dataChaserBlue.get(dataChaserBlue.size() - 1), radius);
        }

        if (chaserRedEnabled) {
            if (dataChaserRed.size() > 1) {
                g.setColor(Color.red);
                plotCoordinateList(dataChaserRed, radius);
            } else if (dataChaserRed.size() == 1) {
                g.setColor(Color.red);
                fillCircle(dataChaserRed.get(0), radius);
            }
        } else if (!dataChaserRed.isEmpty()) {
            g.setColor(Color.red);
            drawCircle(dataChaserRed.get(dataChaserRed.size() - 1), radius);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    public void fireCoordinateChange() {
        pcs.firePropertyChange("nextRunner", null, dataRunner.get(dataRunner.size() - 1));
        pcs.firePropertyChange("nextChaserBlue", null, dataChaserBlue.get(dataChaserBlue.size() - 1));
        pcs.firePropertyChange("nextChaserRed", null, dataChaserRed.get(dataChaserRed.size() - 1));
    }

    protected void init() {
        stepTime = 20; //Curve Resolution
        timeScale = 2 * stepTime / 10.0;
        next = false;
        timePassed = 0;
        runnerDirection = new Vector(1, 0);
        hitbox = 0.03 * getTimeScale();

        if (dataRunner.isEmpty()) {
            runnerDirectionLine.setLine(-animationScale * Math.PI, 0, 100, 0);
        }

        boundingBox_x = animation.getWidth() / guesswork;
        boundingBox_y = animation.getHeight() / guesswork;

        //runner
        if (dataRunner.isEmpty()) {
            Vector runnerStartingPosition = new Vector(-animationScale * Math.PI, 0);
            if (runnerStartingPosition.x < -boundingBox_x) {
                dataRunner.add(new Vector(-boundingBox_x + 2 * hitbox, 0));
                runnerDirectionLine.setLine(-boundingBox_x, 0, boundingBox_x, 0);
                alpha = 0.0;
            } else {
                dataRunner.add(runnerStartingPosition);
                runnerDirectionLine.setLine(-animationScale * Math.PI, 0, boundingBox_x, 0);
                alpha = 0.0;
            }
        }
        //chaser1
        if (dataChaserBlue.isEmpty()) {
            dataChaserBlue.add(new Vector(-2 * animationScale * Math.PI, 4 * animationScale / Math.sqrt(2)));
        }
        //chaser2
        if (dataChaserRed.isEmpty()) {
            dataChaserRed.add(new Vector(2 * animationScale * Math.PI, -4 * animationScale / Math.sqrt(2)));
        }

        fireCoordinateChange();
    }

    protected void reset() {
        dataRunner.clear();
        dataChaserBlue.clear();
        dataChaserRed.clear();

//        stepLengthRunner = 0.01;
        animation.repaint();
    }

    private Vector applyRiverCurrent(Vector next) {
        if (next.y >= -boundingBox_y + 0.3 * boundingBox_y
                && next.y <= -boundingBox_y + 0.3 * boundingBox_y + boundingBox_y - 0.6 * boundingBox_y) {
            next = Vector.addUp(next, new Vector(stepLengthRiver, 0.0));
        }
        return next;
    }

    @Override
    public void run() {
        while (next) {
            try {
                Thread.sleep(stepTime);
            } catch (InterruptedException ie) {
                //tue nichts
            }

//            timeStart = System.currentTimeMillis();
//            timeBetweenRepaints = (timeStart - timeStop);
//            System.out.println("" + timeBetweenRepaints);
//            stepTime = timeBetweenRepaints;
//            timeScale = stepTime / 10.0;
            Vector runnerLastInList = dataRunner.get(dataRunner.size() - 1);
            Vector chaserBlueLastInList = dataChaserBlue.get(dataChaserBlue.size() - 1);
            Vector chaserRedLastInList = dataChaserRed.get(dataChaserRed.size() - 1);

            //hit detection
            if (Vector.length(Vector.subtract(chaserBlueLastInList, runnerLastInList)) <= hitbox) {
                pcs.firePropertyChange("hit", null, "Der blaue Verfolger");
                hit = true;
            }

            if (Vector.length(Vector.subtract(chaserRedLastInList, runnerLastInList)) <= hitbox) {
                pcs.firePropertyChange("hit", null, "Der rote Verfolger");
                hit = true;
            }

            //out detection
            if (Math.abs(runnerLastInList.getX()) >= boundingBox_x || Math.abs(runnerLastInList.getY()) >= boundingBox_y) {
                pcs.firePropertyChange("hit", null, "Grenze");
                hit = true;
            }

            //calculating next runner point
            scale = 0.00025 * stepLengthRunner * 100 * (1 / animationScale);
            amplitude = 0.01 * stepLengthRunner * 100 * Math.PI;

            alpha = Math.atan2(runnerDirectionLine.y2 - runnerDirectionLine.y1, runnerDirectionLine.x2 - runnerDirectionLine.x1);
            //River
            stepLengthRiver = getTimeScale() * riverCurrentVelocity;
            //line
            if (runnerMode.equals("Gerade")) {
                stepRunner = Vector.scaleToLength(runnerDirection, getTimeScale() * stepLengthRunner);
                nextRunner = Vector.addUp(runnerLastInList, Vector.rotateByAngle(stepRunner, alpha));
                if (riverEnabled) {
                    nextRunner = applyRiverCurrent(nextRunner);
                    pcs.firePropertyChange("velocityChangeRunner", null, Vector.length(Vector.subtract(nextRunner, runnerLastInList)));
                }
            //sine
            } else if (runnerMode.equals("Sinus")) {
                nextTrigonY = amplitude * (Math.cos((4 * Math.PI * scale) * timePassed));
                runnerDirection = new Vector(stepLengthRunner, nextTrigonY);
                stepRunner = Vector.scaleToLength(runnerDirection, getTimeScale() * stepLengthRunner);
                nextRunner = Vector.addUp(runnerLastInList, Vector.rotateByAngle(stepRunner, alpha));
                if (riverEnabled) {
                    nextRunner = applyRiverCurrent(nextRunner);
                    pcs.firePropertyChange("velocityChangeRunner", null, Vector.length(Vector.subtract(nextRunner, runnerLastInList)));
                }
            //circle
            } else if (runnerMode.equals("Kreis")) {
                nextTrigonX = amplitude * (Math.sin((4 * Math.PI * scale / 5) * timePassed));
                nextTrigonY = amplitude * (Math.cos((4 * Math.PI * scale / 5) * timePassed));
                runnerDirection = new Vector(nextTrigonX, nextTrigonY);
                stepRunner = Vector.scaleToLength(runnerDirection, getTimeScale() * stepLengthRunner);
                nextRunner = Vector.addUp(runnerLastInList, stepRunner);
                if (riverEnabled) {
                    nextRunner = applyRiverCurrent(nextRunner);
                    pcs.firePropertyChange("velocityChangeRunner", null, Vector.length(Vector.subtract(nextRunner, runnerLastInList)));
                }
            } else {
                nextRunner = runnerLastInList;
            }
            timePassed += stepTime;

            //calculating next chaserBlue point
            stepChaserBlue = Vector.scaleToLength(Vector.subtract(runnerLastInList, chaserBlueLastInList), getTimeScale() * stepLengthChaserBlue);
            if (chaserBlueEnabled) {
                nextChaserBlue = (Vector.addUp(chaserBlueLastInList, stepChaserBlue));
            } else {
                nextChaserBlue = chaserBlueLastInList;
            }
            if (riverEnabled) {
                nextChaserBlue = applyRiverCurrent(nextChaserBlue);
                pcs.firePropertyChange("velocityChangeChaserBlue", null, Vector.length(Vector.subtract(nextChaserBlue, chaserBlueLastInList)));
            }
            //calculating next chaserRed point
            stepChaserRed = Vector.scaleToLength(Vector.subtract(runnerLastInList, chaserRedLastInList), getTimeScale() * stepLengthChaserRed);
            if (chaserRedEnabled) {
                nextChaserRed = (Vector.addUp(chaserRedLastInList, stepChaserRed));
            } else {
                nextChaserRed = chaserRedLastInList;
            }
            if (riverEnabled) {
                nextChaserRed = applyRiverCurrent(nextChaserRed);
                pcs.firePropertyChange("velocityChangeChaserRed", null, Vector.length(Vector.subtract(nextChaserRed, chaserRedLastInList)));
            }
            
            //building arraylists
            dataRunner.add(nextRunner);
            dataChaserBlue.add(nextChaserBlue);
            dataChaserRed.add(nextChaserRed);

            fireCoordinateChange();
            // System.out.println("" + dataRunner.size());
            if (dataRunner.size() > 1E6) {
                dataRunner.clear();
                dataChaserBlue.clear();
                dataChaserRed.clear();
                dataRunner.add(nextRunner);
                dataChaserBlue.add(nextChaserBlue);
                dataChaserRed.add(nextChaserRed);
            }

            animation.repaint();

//            timeStop = System.currentTimeMillis();
        }
        step = null;
    }

    public void start() {
        if (step == null) {
            step = new Thread(this);
            next = true;
            step.start();
        }
    }

    public void stop() {
        next = false;
    }
}
