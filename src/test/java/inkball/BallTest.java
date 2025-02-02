package inkball;

import processing.core.PApplet;
import processing.event.KeyEvent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

public class BallTest {

    @Test
    void testBallPositionUpdate() {
        Ball ball = new Ball(100, 100);  // Initial ball position
        ball.setVelocity(5, 5);  // Set velocity to move the ball

        ball.updatePosition();  // Update position based on velocity

        assertEquals(105, ball.getX());  // New X position should be 100 + 5
        assertEquals(105, ball.getY());  // New Y position should be 100 + 5
    }





}
