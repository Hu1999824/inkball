package inkball;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WallTest {

    @Test
    void testBallWallCollision() {
        Wall wall = new Wall(10, 10);  // Wall at position (10, 10)
        wall.setColour(0);  // Assuming this sets a color, you may have logic for different walls

        Ball ball = new Ball(15, 15);  // Ball moving towards the wall
        ball.setVelocity(-5, 0);  // Moving left towards the wall

        wall.checkCollision(ball);  // Check for collision

        assertEquals(5, ball.getxVelocity());  // Velocity should reverse after hitting the wall
        assertEquals(0, ball.getyVelocity());  // Y velocity remains unchanged
    }
}

