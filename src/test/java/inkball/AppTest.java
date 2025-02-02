package inkball;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;


class AppTest {

    static App app;

    @BeforeAll
    public static void setup() {
        app = new App();
        app.loop();
        PApplet.runSketch(new String[] {"App"}, app);
    }

    @Test
    public void testFramerate() {
        app.setup();
        assertTrue(
                App.FPS - 2 <= app.frameRate &&
                        App.FPS + 2 >= app.frameRate
        );
    }

    @Test
    void testWrongHoleCapture() {
        App app = new App();
        app.score = 100;  // Initial score

        Ball ball = new Ball(100, 100);
        ball.setColour(0);  // Grey ball

        Hole hole = new Hole(100, 100);
        hole.setCoclour(1);  // Orange hole (wrong hole)

        app.computeScore(ball, hole);  // Calculate score for wrong hole

        assertEquals(95, app.score);  // Assuming score_decrease_from_wrong_hole[0] = 5
    }

    @Test
    void testGameWinCondition() {
        App app = new App();
        boolean[] gameWin = {true, true, true, true};  // Simulate all levels won

        assertTrue(app.isWin(gameWin));  // Game should be won
    }

    @Test
    void testKeyPress() {
        App app = new App();
        KeyEvent keyEvent = new KeyEvent(null, 0, 0, 'r', 0);  // Simulate pressing 'r'
        app.keyPressed();

        assertTrue(app.game_win[app.level]);  // Validate game win state
    }

    @Test
    void testGamePause() {
        App app = new App();
        app.isPaused = false;
        app.start_time = 5000;  // Simulate the start time (in millis)
        app.time_pause = 0;

        app.keyPressed();  // Press 'space' to pause the game

        assertTrue(app.isPaused);  // The game should now be paused
        assertTrue(app.time_pause > 0);  // time_pause should capture the paused time
    }

    @Test
    void testGameResume() {
        App app = new App();
        app.isPaused = true;
        app.time_pause = 3000;  // Simulate the game being paused after 3 seconds

        app.keyPressed();  // Press 'space' to resume

        assertFalse(app.isPaused);  // The game should resume
        assertTrue(app.start_time > 0);  // start_time should now account for the pause time
    }

    @Test
    void testTimeOver() {
        App app = new App();
        app.start_time = 0;
        app.time = 60;  // Game has a 60-second timer
        app.time_over = false;

        app.time_current = 70000;  // 70 seconds have passed

        app.draw();  // Run the draw method which handles timing

        assertTrue(app.time_over);  // The game should be over due to time-out
        assertEquals(0, app.time_left);  // Time left should be zero
    }

}

// gradle run						Run the program
// gradle test						Run the testcases

// Please ensure you leave comments in your testcases explaining what the testcase is testing.
// Your mark will be based off the average of branches and instructions code coverage.
// To run the testcases and generate the jacoco code coverage report: 
// gradle test jacocoTestReport
