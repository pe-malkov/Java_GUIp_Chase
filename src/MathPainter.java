import java.awt.*;
/*
 * MathPainter.java
 *
 * Eine Schnittstelle f&uuml;r die Klasse JMath.java. Wird sie implementiert
 * und f&uuml;r ein JMath-Objekt die Methode setPaintClient(this) aufgerufen,
 * so ruft das JMath-Objekt diese Methode auf, wodurch man in der mathematischen
 * Umgebung zeichnen kann.
 */

/**
 * Die zu implementierende Methode
 * 
 * @author chentsch
 */
public interface MathPainter {
    public void mathPaint(Graphics2D g);
}
