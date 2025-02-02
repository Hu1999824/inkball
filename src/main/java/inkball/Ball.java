package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.dist;

public class Ball {

    public static int radius = 12;
    public int xVelocity = 1;
    public int yVelocity = 1;
    public int x;
    public int y;
    public int colour;
    private boolean captured = false; // Track if ball is captured
    private boolean paused = false;
    public PVector position;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        this.position = new PVector(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setxVelocity(int xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setyVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }

    public int getxVelocity() {
        return xVelocity;
    }

    public int getyVelocity() {
        return yVelocity;
    }

    public void changePause() {
        this.paused = !this.paused;
    }

    public void setVelocity(int xVelocity, int yVelocity) {
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
    }

    public void updatePosition() {
        if (!captured && !paused) {
            x += xVelocity;
            y += yVelocity;
        }
    }

    // Return the position of the ball as a PVector
    public PVector getPosition() {
        return position;
    }

    public void applyAttraction(PVector holeCenter) {
        PVector toHole = PVector.sub(holeCenter, this.position); // Vector from ball to hole
        float distance = toHole.mag();
//        System.out.println("distance = " + distance);

        if (distance < radius) {
            captured = true;
            // Stop movement and update the position
            this.xVelocity = 0;
            this.yVelocity = 0;
        }
    }

    public void isBallOverHole (int holeX, int holeY) {
        float distance = dist(this.x, this.y, holeX, holeY);
//        System.out.println("distance = " + distance);
        if (distance < radius) {
            captured = true;
            this.xVelocity = 0;
            this.yVelocity = 0;
        } else {
            captured = false;
        }
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public int getColour() {
        return colour;
    }

    public void checkCollision(int width, int height, int topbar) {
        if (x - radius < 0 || x + radius > width) {
            xVelocity *= -1;
        }
        else if (y - radius < topbar || y + radius > height) {
            yVelocity *= -1;
        }
    }

    public void draw(App app) {
        switch (this.colour) {
            case 0:
                app.image(app.getSprite("ball0"), x - radius, y - radius, radius * 2, radius * 2);
                break;
            case 1:
                app.image(app.getSprite("ball1"), x - radius, y - radius, radius * 2, radius * 2);
                break;
            case 2:
                app.image(app.getSprite("ball2"), x - radius, y - radius, radius * 2, radius * 2);
                break;
            case 3:
                app.image(app.getSprite("ball3"), x - radius, y - radius, radius * 2, radius * 2);
                break;
            case 4:
                app.image(app.getSprite("ball4"), x - radius, y - radius, radius * 2, radius * 2);
                break;
            default: break;
        }
//        app.image(app.getSprite("ball" + Integer.toString(colour)), x - radius, y - radius, radius * 2, radius * 2);

    }

}
