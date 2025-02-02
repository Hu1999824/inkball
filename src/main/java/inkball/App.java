package inkball;

import javafx.scene.input.MouseButton;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;


/**
 * The App class represents the main entry point of the Inkball game,
 * implemented using Processing. It handles the game logic, user input,
 * and rendering of elements such as balls, walls, holes,
 * and lines drawn by the player.
 *
 * The game settings, including levels, ball behaviors, score modifiers,
 * and time limits, are loaded from a configuration file in JSON format.
 */
public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;
    public String[] layout = new String[3];
    public int[] timeArray = new int[3];
    public int[] spawn_interval = new int[3];
    public int[] score_increase_from_hole_capture_modifier = new int[3];
    public int[] score_decrease_from_wrong_hole_modifier = new int[3];
    public int[] score_increase_from_hole_capture = new int[5]; // The amount of units score is increased for each ball type when they successfully enter a hole
    public int[] score_decrease_from_wrong_hole = new int[5]; // The amount of units score is decreased for each ball type when they enter the wrong hole

    public boolean[] game_win = new boolean[3];
    public int level;
    public String file_path = "";
    public int time;
    public int interval;
    public int increase_modifier;
    public int decrease_modifier;
    private ArrayList<ArrayList<String>> allLevelBalls = new ArrayList<>();
    public ArrayList<String> levelBalls = new ArrayList<>();  // A list of balls that store in level i
    public ArrayList<Integer> levelBallsColour = new ArrayList<>(); // A list of balls that store colour in level i
    public char[][] char2DArray = new char[(HEIGHT - TOPBAR) / CELLSIZE][WIDTH / CELLSIZE];

    public  boolean game_start;
    public boolean game_over;
    public int start_time;
    public int time_left;
    public float time_previous;
    public float time_current;
    public float countdown;
    public boolean time_over;
    public int time_pause;
    public boolean isPaused; // Time is paused or not
    public int score;
    public int lastBallSpawnTime = 0;  // Time when the last ball was spawned
    public int numberOfSpawnBall = 0;


    private Tile[][] board;
    private Ball ball;
    private Wall wall;
    private Hole hole;
    private Spawner spawner;
    private final HashMap<String,PImage> sprites = new HashMap<>();
    private ArrayList<Line> playerLines = new ArrayList<>();
    private ArrayList<Ball> balls = new ArrayList<>();  // Store balls that begin to spawn
    private ArrayList<Ball> alternativeBall = new ArrayList<>(); // Store balls that ready to spawn from spawner
    private ArrayList<Ball> displayBall = new ArrayList<>();
    private List<Ball> ballsToRemove = new ArrayList<>(); // Create a temporary list to store balls to be removed
    private ArrayList<Spawner> spawners = new ArrayList<>(); // Store all spawners
    private Line currentLine;  // The line being drawn
    private boolean isDrawing = false;


    // Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public void gameStart() {

        board = new Tile[(HEIGHT - TOPBAR) / CELLSIZE][WIDTH / CELLSIZE];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Tile(i, j);
            }
        }

        playerLines = new ArrayList<>();
//        ballVel = new PVector(randomVelocity(), randomVelocity());

        //default level is level1, if we win level1, enter level2 and so on until we win all levels.
        if (game_win[0] == false) {
            level = 0;
        } else if (game_win[0] == true && game_win[1] == false) {
            level = 1;
        } else if (game_win[0] == true && game_win[1] == true && game_win[2] == false) {
            level = 2;
        }

        file_path = layout[level];
        time = timeArray[level];
        interval = spawn_interval[level];
        levelBalls = allLevelBalls.get(level);
        increase_modifier = score_increase_from_hole_capture_modifier[level];
        decrease_modifier = score_decrease_from_wrong_hole_modifier[level];

        game_over = false;
        score = 0;

        // Initialize the start time
        start_time = millis(); // The start time in milliseconds
        time_over = false;
        countdown = (float) interval;
        time_previous = millis();

        // Initialize the isPaused
        isPaused = false;

        //read file based on "config.jason" in order to layout.
        read_filepath();

        for (int i = 0; i < char2DArray.length; i++) {
            for (int j = 0 ; j < char2DArray[i].length; j++) {
                if (char2DArray[i][j] == 'B') {
//                    Ball ball = createBall(j * CELLSIZE , i * CELLSIZE + TOPBAR, char2DArray[i][j+1]);
//                    balls.add(ball);
//                    ball = new Ball(j * CELLSIZE - Ball.radius, i * CELLSIZE + TOPBAR - Ball.radius);
                    ball = createBall(j * CELLSIZE + Ball.radius, i * CELLSIZE + TOPBAR + Ball.radius, Integer.parseInt(String.valueOf(char2DArray[i][j+1])));
                    ball.setVelocity(randomVelocity(), randomVelocity());
                    balls.add(ball);
                }
                else if (char2DArray[i][j] == 'S') {
                    spawner = new Spawner(j, i);
                    spawners.add(spawner);// Add spawner to the list
                }
            }
        }

        for (String ball : levelBalls) {
            convertColour(ball);
        }
//        System.out.println("levelBalls = " + levelBalls);
//        System.out.println("levelBallsColour = " + levelBallsColour);

        // Create all the alternative balls in config file and ready to spawn
        for (int colour : levelBallsColour) {
            spawnBallAtRandomSpawner(colour);
        }
    }

    public PImage getSprite(String s){
        PImage result = sprites.get(s);
        if(result == null) {
            try {
                result = loadImage(URLDecoder.decode(this.getClass().getResource(s + ".png").getPath(), StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            sprites.put(s, result);
        }
        return result;
    }

    public void read_filepath() {
        try {
            File file = new File(file_path);
            Scanner scanner = new Scanner(file);
            StringBuilder file_contend = new StringBuilder();

            while(scanner.hasNextLine()) {
                file_contend.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            String content = file_contend.toString();
            String[] lines = content.trim().split("\n");
            for (int i = 0; i < lines.length; i++) {
                char2DArray[i] = lines[i].toCharArray();
            }
//            for (char[] row : char2DArray) {
//                for (char c : row) {
//                    System.out.print(c);
//                }
//                System.out.println();
//            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isWin(boolean[] game_win) {
        for (int i = 0; i < game_win.length; i++) {
            if (game_win[i] == false) return false;
        }
        return true;
    }

    // Generates a random velocity of either -2 or 2
    private int randomVelocity() {
        return random(1) > 0.5 ? 2 : -2;
    }


    public Ball createBall (int x, int y, int colour) {
        Ball ball = new Ball(x, y);
        ball.setColour(colour);
        return ball;
    }

    // Convert colour that store in levelBalls to integer
    public void convertColour(String colour) {
        switch (colour) {
            case "grey":
                levelBallsColour.add(0);
                break;
            case "orange":
                levelBallsColour.add(1);
                break;
            case "blue":
                levelBallsColour.add(2);
                break;
            case "green":
                levelBallsColour.add(3);
                break;
            case "yellow":
                levelBallsColour.add(4);
                break;
            default:
                throw new IllegalArgumentException("Unknown color: " + colour);
        }
    }


    // Method to spawn a ball at a random spawner
    public void spawnBallAtRandomSpawner(int colour) {
        // Pick a random spawner from the list
        Spawner randomSpawner = spawners.get((int) this.random(spawners.size()));
        // Spawn a ball using the randomly selected spawner
        ball = createBall(randomSpawner.getX() * CELLSIZE + Ball.radius, randomSpawner.getY() * CELLSIZE + TOPBAR + Ball.radius, colour);
//        ball = createBall(j * CELLSIZE + Ball.radius, i * CELLSIZE + TOPBAR + Ball.radius, Integer.parseInt(String.valueOf(char2DArray[i][j+1])));
        ball.setVelocity(randomVelocity(), randomVelocity());
//        balls.add(ball);
        alternativeBall.add(ball);
    }

    // Method to remove a line at the mouse's current position
    public void removeLineAtMousePosition(float mouseX, float mouseY) {
        for (int i  = playerLines.size() - 1; i >= 0; i++) {
            Line line = playerLines.get(i);
            if (line.isPointOnLine(mouseX, mouseY)) {
                playerLines.remove(i);  // Remove the line if the mouse is hovering over it
                break;  // Exit after removing one line
            }
        }
    }

    public int getDisplayBallColour(int index) {
        return levelBallsColour.get(index);
    }

    public void computeScore(Ball ball, Hole hole) {
        if (ball.getColour() == hole.getCoclour() || hole.getCoclour() == 0) {
            //
            score += score_increase_from_hole_capture[ball.getColour()] * increase_modifier;
        }else {
            score -= score_decrease_from_wrong_hole[ball.getColour()] * decrease_modifier;
        }
    }

    public boolean isPass() {
        if (balls.isEmpty()) return true;
        else return false;
    }

    /**
     * Constructor for the App class.
     * Initializes the path to the configuration file.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initializes the settings for the game window, such as the width and height.
     * This method is called by Processing at the start of the sketch.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Loads resources such as images, initializes game elements like levels, balls,
     * and score configurations from the JSON config file, and sets up the game environment.
     * It also initializes the player's lines and starts the game.
     */
    @Override
    public void setup() {
        frameRate(FPS);

        //See PApplet javadoc:
        //loadJSONObject(configPath)
        // the image is loaded from relative path: "src/main/resources/inkball/..."
		/*try {
            result = loadImage(URLDecoder.decode(this.getClass().getResource(filename+".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }*/
        JSONObject config = loadJSONObject(configPath);
        JSONArray levels = config.getJSONArray("levels");
        JSONObject scoreIncrease = config.getJSONObject("score_increase_from_hole_capture");
        JSONObject scoreDecrease = config.getJSONObject("score_decrease_from_wrong_hole");
        //Then we begin to read the txt file and layout it based on txt.

        score_increase_from_hole_capture[0] = scoreIncrease.getInt("grey");
        score_increase_from_hole_capture[1] = scoreIncrease.getInt("orange");
        score_increase_from_hole_capture[2] = scoreIncrease.getInt("blue");
        score_increase_from_hole_capture[3] = scoreIncrease.getInt("green");
        score_increase_from_hole_capture[4] = scoreIncrease.getInt("yellow");

        score_decrease_from_wrong_hole[0] = scoreDecrease.getInt("grey");
        score_decrease_from_wrong_hole[1] = scoreDecrease.getInt("orange");
        score_decrease_from_wrong_hole[2] = scoreDecrease.getInt("blue");
        score_decrease_from_wrong_hole[3] = scoreDecrease.getInt("green");
        score_decrease_from_wrong_hole[4] = scoreDecrease.getInt("yellow");

        for (int i = 0; i < levels.size(); i++) {
            JSONObject level = levels.getJSONObject(i);
            JSONArray ballsArray = level.getJSONArray("balls");  // Get the "balls" array for the level
            layout[i] = level.getString("layout");
            timeArray[i] = level.getInt("time");
            spawn_interval[i] = level.getInt("spawn_interval");
            score_increase_from_hole_capture_modifier[i] = level.getInt("score_increase_from_hole_capture_modifier");
            score_decrease_from_wrong_hole_modifier[i] = level.getInt("score_decrease_from_wrong_hole_modifier");

            // Create a new list for the current level's balls
            ArrayList<String> levelBalls = new ArrayList<>();

            // Add each ball from the ballsArray to the levelBalls list
            for (int j = 0; j < ballsArray.size(); j++) {
                levelBalls.add(ballsArray.getString(j));
            }

            // Add the current level's ball list to the master list
            allLevelBalls.add(levelBalls);

//            println("Level " + (i + 1) + ":");
//            println("Layout: " + layout);
//            println("Time: " + time);
//            println("Spawn Interval: " + spawnInterval);
//            println("allLevelBalls = " + allLevelBalls);
//            println("levelBalls = " + levelBalls);

        }

        //initialize game_start
        game_start = true;
        if (game_start) {
            for (int i = 0; i < game_win.length; i++) {
                game_win[i] = false;
            }
        }

//        game_win[0] = true;
//        game_win[1] = true;

        gameStart();

    }

    /**
     * Handles key press events. Allows the player to restart the level,
     * toggle pause state, or pass the level based on the key pressed.
     *
     * @param event The KeyEvent triggered by a key press.
     */
    @Override
    public void keyPressed(KeyEvent event){

        if (event.getKey() == 'r') {
            if (isPass()) {
                // Pass the level
                game_win[level] = true;
            } else {
                time_over = false;
                playerLines.clear();// Remove lines
                balls.clear();
                gameStart();
            }
        }
        else if (event.getKey() == ' ') {
            for (Ball ball : balls) {
                ball.changePause();
            }
            isPaused = !isPaused;
            if (isPaused) {
                time_pause = millis() - start_time;
            }
        }
    }

    /**
     * Handles key release events. Currently, no functionality is associated with key release.
     */
    @Override
    public void keyReleased(){

    }

    /**
     * Handles mouse press events. Allows the player to draw a new line with the left mouse button
     * and remove lines with the right mouse button or Ctrl + Left Click.
     *
     * @param e The MouseEvent triggered by a mouse press.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Start drawing a new line on left mouse press
        if (e.getButton() == LEFT && !e.isControlDown()) {
            isDrawing = true;
            currentLine = new Line();  // Create a new Line object
            currentLine.addPoint(mouseX, mouseY);  // Add the starting point
        }
        // Handle right-click or Ctrl + Left Click for removing lines
        if (e.getButton() == RIGHT || (e.getButton() == LEFT && e.isControlDown())) {
            removeLineAtMousePosition(mouseX, mouseY);
        }
    }

    /**
     * Handles mouse dragging events. Adds points to the current line being drawn as the mouse is dragged.
     *
     * @param e The MouseEvent triggered by a mouse drag.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // Continue adding points to the line as the mouse is dragged
        if (isDrawing) {
            currentLine.addPoint(mouseX, mouseY);
        }
    }

    /**
     * Handles mouse release events. Completes the current line being drawn and adds it to the player's lines.
     *
     * @param e The MouseEvent triggered by a mouse release.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Stop drawing when the mouse is released
        if (isDrawing && e.getButton() == LEFT) {
            playerLines.add(currentLine);  // Add the finished line to the list
            isDrawing = false;  // Stop the drawing
        }
    }

    /**
     * Renders all the game elements on the screen, including balls, walls, holes,
     * lines drawn by the player, and game messages. It also updates the state of the game
     * like time and ball positions in each frame.
     */
    @Override
    public void draw() {

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        //TODO
        background(200, 200, 200);

        // Draw the rectangle (storage area) at the top-left
        fill(0);
        stroke(200);
        rect(10, 10, 170, 50);  // Example position and size

        // Position of five balls
//        for (int i = 0; i < 5; i++) {
//            fill(255);
//            ellipse(30 + i * 30, 30, 12, 12);
//        }

        for (int i = 0; i < 5; i++) {
            if (i + numberOfSpawnBall < levelBallsColour.size()) {
                displayBall.add(createBall(30 + i * 30, 30, getDisplayBallColour(i + numberOfSpawnBall)));
            }
            else if (i >= displayBall.size()) {
                break;
            }
            // Remove balls
            else if (i + numberOfSpawnBall > levelBallsColour.size()) {
//                System.out.println("i = " + i);
                displayBall.remove(i);
            }
        }

        if (!displayBall.isEmpty()) {
            for (Ball ball : displayBall) {
                ball.draw(this);
            }
        }

        time_current = millis();

        if (!time_over && !isPaused) {
            // Calculate the elapsed time and update time_left
            int elapsedTime = (millis() - start_time) / 1000;
            time_left = time - elapsedTime;

            if (countdown > 0) {
                countdown -= (float) ((time_current - time_previous) / 1000.0);
            }

            if (millis() - lastBallSpawnTime >= interval * 1000 && !alternativeBall.isEmpty()) {
//                System.out.println(count + "th call");
//                System.out.println("alternativeBall size = " + alternativeBall.size());
                ball = alternativeBall.get(0);  // Get the next ball
                balls.add(ball);  // Add it to the active ball list
                alternativeBall.remove(0);  // Remove it from the ready-to-spawn list
                lastBallSpawnTime = millis();  // Update the last spawn time
                numberOfSpawnBall++;

            }

            if (time_left <= 0) {
                time_over = true;
                time_left = 0;
            }
        }
        else if (!time_over && isPaused) {
            start_time = millis() - time_pause;
        }

        // Display the remaining time in the top-right corner
        fill(0);
        textSize(20);
        textAlign(RIGHT, TOP);
        text("Time:" + time_left, WIDTH-10, 40);

        textAlign(CENTER, TOP);
        text(nf(countdown, 1, 1), WIDTH/2, 35);

        if (countdown <= 0) {
            countdown = (float) interval;
        }
        time_previous = time_current;

        // Display "TIME'S UP" message when the time is over
        if (time_over) {
            fill(255, 0, 0);  // Red color for the message
            textSize(30);
            textAlign(CENTER, TOP);
            text("=== TIME'S UP ===", WIDTH / 2, 10);
        }

        if (isPaused) {
            fill(255, 0, 0);
            textSize(30);
            textAlign(CENTER, TOP);
            text("*** PAUSED ***", WIDTH / 2 + 30, 10);
        }


        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board.length; j++) {
                image(getSprite("tile"), j * CELLSIZE, i * CELLSIZE + TOPBAR
                        , CELLSIZE, CELLSIZE);
            }
        }

        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board.length; j++) {
                if (!Character.isDigit(char2DArray[i][j])) {
                    if (char2DArray[i][j] == 'X') {
                        wall = new Wall(j,i);
//                        image(getSprite("wall0"), j * CELLSIZE, i * CELLSIZE + TOPBAR
//                                , CELLSIZE, CELLSIZE);
                        int colour = 0;
                        wall.setColour(colour);
                        wall.draw(this);
                        for (Ball ball : balls) {
                            wall.checkCollision(ball);
                        }
                    } else if (char2DArray[i][j] == 'S') {
                        spawner = new Spawner(j,i);
                        spawner.draw(this);
                    } else if (char2DArray[i][j] == 'B') {
//                        int colour = Character.getNumericValue(char2DArray[i][j+1]);
//                        ball = new Ball(j * CELLSIZE , i * CELLSIZE + TOPBAR);
//                        ball.setColour(colour);
                        continue;
                    } else if (char2DArray[i][j] == 'H') {
                        int colour = Character.getNumericValue(char2DArray[i][j+1]);
                        hole = new Hole(j,i);
                        hole.setCoclour(colour);
                        hole.draw(this);
//                        PVector holeCenter = hole.getCenter();
//                        ball.applyAttraction(holeCenter);
                        for (Ball ball : balls) {
//                            ball.applyAttraction(holeCenter);
                            ball.isBallOverHole(hole.getX() * CELLSIZE + CELLSIZE, hole.getY() * CELLSIZE + CELLSIZE + CELLSIZE / 2 + TOPBAR);
//                            ball.isBallOverHole(hole.getX() * App.CELLSIZE + App.CELLSIZE / 2, hole.getY() * App.CELLSIZE + App.CELLSIZE / 2 + App.TOPBAR);
                            if (ball.isCaptured()) { // A ball fell in the hole
                                // Calculate score
                                computeScore(ball, hole);
                                ballsToRemove.add(ball);
                            }
                        }
                    }
                }
                else {
                    if (j != 0 && char2DArray[i][j-1] == 'B') continue;
                    else if (j != 0 && char2DArray[i][j-1] == 'H') continue;
                    else {
                        int colour = Character.getNumericValue(char2DArray[i][j]);
                        wall = new Wall(j,i);
                        wall.setColour(colour);
                        wall.draw(this);
                        for (Ball ball : balls) {
                            wall.checkCollision(ball);
                        }
                    }
                }
            }
        }



        if (!isWin(game_win) && !time_over) {

            for (Ball ball : balls) {
                if (ball.isCaptured()) continue;
                ball.draw(this);
                ball.updatePosition();
            }

            balls.removeAll(ballsToRemove);

            // Draw all player-drawn lines
            for (int i = playerLines.size() - 1; i >= 0; i--) {
                Line line = playerLines.get(i);
                line.draw(this);  // Draw the line

                for (Ball ball : balls) {
                    // Check if the ball collides with the line
                    if (line.ballHitsLine(ball)) {
                        ball.xVelocity *= -1;
                        ball.yVelocity *= -1;
                        playerLines.remove(i);  // Remove the line after collision
                    }
                }
            }

        }

//        System.out.println("balls size = " + balls.size());

        //----------------------------------
        //display score
        //----------------------------------
        //TODO

        fill(0);
        textSize(20);
        textAlign(RIGHT, TOP);
        text("Score:" + score, WIDTH-10, 20);


        //----------------------------------
        //----------------------------------
        //display game end message

        if (level == 3) {
            // Win the game
            game_over = true;
            fill(255, 0, 0);
            textSize(30);
            textAlign(CENTER, TOP);
            text("*** GAME WIN ***", WIDTH / 2 + 30, 10);
        }


    }

    /**
     * The main entry point of the application, which starts the Processing sketch.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
