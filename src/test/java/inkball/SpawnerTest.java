package inkball;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpawnerTest {

    @Test
    void testSpawnerInitialization() {
        Spawner spawner = new Spawner(100, 200);  // Spawner at position (100, 200)

        // Verify the spawner's position is set correctly
        assertEquals(100, spawner.getX());
        assertEquals(200, spawner.getY());
    }
}
