import javax.swing.*;
import java.awt.*;

class TrafficLightComponent extends JComponent {
    private final Color bodyColor = new Color(60, 60, 60);
    private final Color visorColor = new Color(255, 255, 255);
    private final Color redLightColor = new Color(228, 6, 17);
    private final Color yellowLightColor = new Color(255, 247, 67);
    private final Color greenLightColor = new Color(59, 170, 52);
    private final int triangleRadius = 10;

    public void paint(Graphics g) {
        paintBody(g);
        paintLightWithVisor(g, redLightColor, 50);
        paintLightWithVisor(g, yellowLightColor, 215);
        paintLightWithVisor(g, greenLightColor, 380);
        paintLeg(g);
        paintTriangles(g);
        paintHat(g);
    }

    private void paintBody(Graphics g) {
        g.setColor(bodyColor);
        g.fillRoundRect(110, 40, 180, 520, 40, 40);
    }

    private void paintLeg(Graphics g) {
        g.setColor(bodyColor);
        g.fillRoundRect(160, 570, 80, 90, 20, 20);
    }

    private void paintLightWithVisor(Graphics g, Color color, int y) {
        paintVisor(g, y);
        paintLight(g, color, y + 30);
    }

    private void paintLight(Graphics g, Color color, int y) {
        g.setColor(color);
        g.fillOval(140, y, 120, 120);
    }

    private void paintVisor(Graphics g, int y) {
        g.setColor(visorColor);
        g.fillOval(125, y, 150, 150);
        g.setColor(bodyColor);
        g.fillOval(125, y + 20, 150, 150);
    }

    private void paintHat(Graphics g) {
        g.setColor(bodyColor);
        Rectangle rectangle = g.getClipBounds();
        g.setClip(140, 0, 120, 30);
        g.fillOval(140, 0, 120, 60);

        g.setClip(rectangle);
    }

    private void paintTriangles(Graphics g) {
        fillRoundedTriangle(g, -15, 85, false);
        fillRoundedTriangle(g, -15, 245, false);
        fillRoundedTriangle(g, -15, 410, false);
        fillRoundedTriangle(g, 300, 85, true);
        fillRoundedTriangle(g, 300, 245, true);
        fillRoundedTriangle(g, 300, 410, true);
    }

    private void fillRoundedTriangle(Graphics g, int x, int y, boolean isMirrored) {
        Point[] points = {
                new Point(x, y),
                new Point(x + 115, y),
                new Point(x + (isMirrored ? 0 : 115), y + 115)
        };
        fillRoundedPolygon(g, points, bodyColor);
    }

    private void fillRoundedPolygon(Graphics g, Point[] points, Color color) {
        Polygon p = new Polygon();
        for (Point i : points) {
            p.addPoint((int) Math.round(i.x), (int) Math.round(i.y));
        }
        g.setColor(color);
        g.fillPolygon(p);

        roundCorner(g, points, color);
    }

    private void roundCorner(Graphics g, Point[] points, Color color) {
        for (int i = 0; i < points.length; i++) {
            Point a = points[i % points.length];
            Point b = points[(i + 1) % points.length];
            Point c = points[(i + 2) % points.length];
            cutCorner(g, a, b, c);
            drawRoundCorner(g, a, b, c, color);
        }
    }

    private void cutCorner(Graphics g, Point a, Point b, Point c) {
        Vector ba = new Vector(b, a);
        Vector bc = new Vector(b, c);

        double angle = ba.getAngleWith(bc);
        int cutRadius = (int) Math.round(triangleRadius / Math.tan(angle / 2));

        g.setColor(getBackground());
        int arcX = (int) Math.round(b.x) - cutRadius;
        int arcY = (int) Math.round(b.y) - cutRadius;

        int startAngle = (int) Math.toDegrees(ba.getAngle());
        int arcAngle = (int) Math.toDegrees(angle);
        g.fillArc(arcX, arcY, 2 * cutRadius, 2 * cutRadius, startAngle, arcAngle);
    }

    private void drawRoundCorner(Graphics g, Point a, Point b, Point c, Color color) {
        Vector ba = new Vector(b, a);
        Vector bc = new Vector(b, c);

        double angle = ba.getAngleWith(bc);
        Vector axis = ba.rotate(- angle / 2).setLength(triangleRadius / Math.sin(angle / 2));
        g.setColor(color);
        int x = (int) Math.round(b.x + axis.x);
        int y = (int) Math.round(b.y + axis.y);
        g.fillOval(x - triangleRadius, y - triangleRadius, 2 * triangleRadius, 2 * triangleRadius);
    }

    private record Point(double x, double y) {
    }

    private static class Vector {
        private final double x;
        private final double y;

        Vector(Point p1, Point p2) {
            x = p2.x - p1.x;
            y = p2.y - p1.y;
        }

        public double getAngle() {
            if (x == 0) {
                return y < 0 ? Math.PI / 2 : 3 * Math.PI / 2;
            } else if (y == 0) {
                return x > 0 ? 0 : Math.PI;
            }
            double angle = Math.atan(y / x);
            angle = (x > 0 ? 2 * Math.PI - angle : Math.PI - angle) % (2 * Math.PI);
            return angle > 0 ? angle : 2 * Math.PI + angle;
        }

        public double getAngleWith(Vector vector) {
            double angle = vector.getAngle() - getAngle();
            return angle < 0 ? 2 * Math.PI + angle : angle;
        }

        public Vector setLength(double length) {
            double angle = 2 * Math.PI - getAngle();
            return new Vector(new Point(0, 0), new Point(length * Math.cos(angle), length * Math.sin(angle)));
        }

        public Vector rotate(double radian) {
            double cos = Math.cos(radian);
            double sin = Math.sin(radian);
            return new Vector(new Point(0, 0), new Point(x * cos - y * sin, x * sin + y * cos));
        }
    }
}

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Traffic Light");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = frame.getLayeredPane();

        JComponent trafficLight = new TrafficLightComponent();
        trafficLight.setBounds(312, 182, 410, 660);
        layeredPane.add(trafficLight);

        frame.setSize(1024, 1024);
        frame.setVisible(true);
    }
}
