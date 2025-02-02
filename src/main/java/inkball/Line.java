package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Line {
    private ArrayList<PVector> points;

    public Line() {
        points = new ArrayList<>();
    }

    // Add points as the player drags the mouse
    public void addPoint(float x, float y) {
        points.add(new PVector(x, y));
    }

    // Draw the line based on the trajectory (points)
    public void draw(PApplet app) {
        app.stroke(0);
        app.strokeWeight(10);
        app.noFill();
        app.beginShape();

        for (PVector point : points) {
            app.vertex(point.x, point.y);
        }

        app.endShape();
    }

    // Detect if the ball hits any segment of the line
    public boolean ballHitsLine(Ball ball) {
//        if (ball == null) {
//            System.out.println("ball is null");
//        } else {
//            System.out.println("ball is not null");
//        }
        for (int i = 0; i < points.size() - 1; i++) {
            PVector p1 = points.get(i);
            PVector p2 = points.get(i + 1);

            // Check if the ball collides with the line segment (p1, p2)
            PVector closestPoint = closestPointOnLine(ball.getPosition(), p1, p2);
//            float distance = PVector.dist(ball.getPosition(), closestPoint);
            float distance = PVector.dist(new PVector(ball.x, ball.y), closestPoint);

//            System.out.println("distance = " + distance);

            // Return true if the distance is less than the ball's radius (indicating a collision)
            if (distance <= Ball.radius) {
                return true;
            }
        }
        return false;
    }

    // Helper method to get the closest point on the line segment to the ball
    private PVector closestPointOnLine(PVector p, PVector a, PVector b) {
        PVector ap = PVector.sub(p, a);
        PVector ab = PVector.sub(b, a);
        ab.normalize();
        ab.mult(ap.dot(ab));
        PVector closest = PVector.add(a, ab);

        // Constrain the closest point to be within the line segment
        float minX = Math.min(a.x, b.x), maxX = Math.max(a.x, b.x);
        float minY = Math.min(a.y, b.y), maxY = Math.max(a.y, b.y);
        closest.x = PApplet.constrain(closest.x, minX, maxX);
        closest.y = PApplet.constrain(closest.y, minY, maxY);

        return closest;
    }

    // Check if a point is on the line
    public boolean isPointOnLine(float x, float y) {
        PVector point = new PVector(x, y);

        // Check if the point is close to any line segment
        for (int i = 0; i < points.size() - 1; i++) {
            PVector p1 = points.get(i);
            PVector p2 = points.get(i + 1);

            // Get the closest point on the segment
            PVector closest = closestPointOnLine(point, p1, p2);
            float distance = PVector.dist(point, closest);

            if (distance < 10) {
                return true;
            }
        }
        return false;
    }


}
