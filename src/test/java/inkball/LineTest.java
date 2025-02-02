package inkball;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LineTest {

    @Test
    void testBallLineCollision() {
        Line line = new Line();
        line.addPoint(100, 100);
        line.addPoint(200, 200);  // Diagonal line

        Ball ball = new Ball(150, 150);  // Ball near the line
        ball.setVelocity(2, 2);  // Ball moving towards the line

        assertTrue(line.ballHitsLine(ball));  // Check if the ball collides with the line
    }

}
