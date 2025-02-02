package inkball;

public class Wall {

    private int x;
    private int y;
    private int colour;

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public void checkCollision(Ball ball) {
        // Get wall boundaries
        int wallLeft = x * App.CELLSIZE;
        int wallRight = wallLeft + App.CELLSIZE;
        int wallTop = y * App.CELLSIZE + App.TOPBAR;
        int wallBottom = wallTop + App.CELLSIZE;

        // Get ball boundaries
        int ballLeft = ball.x - Ball.radius;
        int ballRight = ball.x + Ball.radius;
        int ballTop = ball.y - Ball.radius;
        int ballBottom = ball.y + Ball.radius;

        // Horizontal collision (left and right sides of the wall)
        if (wallLeft < ballRight && wallRight > ballLeft) {
            if (ball.y > wallTop && ball.y < wallBottom) {
//            if(ballBottom > wallTop && ballTop < wallBottom) {
                ball.xVelocity *= -1;  // Reverse x velocity (rebound) for horizontal collision
                if (ball.getColour() != colour && colour != 0) { // Change ball colour when it collides with the wall
                    ball.setColour(colour);
                }
            }
        }
        // Vertical collision (top and bottom sides of the wall)
        if (wallTop < ballBottom && wallBottom > ballTop) {
            if (ball.x > wallLeft && ball.x < wallRight) {
//            if (ball.y > wallTop && ball.y < wallBottom) {
                ball.yVelocity *= -1;  // Reverse y velocity (rebound) for vertical collision
                if (ball.getColour() != colour && colour != 0) {
                    ball.setColour(colour);
                }
            }
        }
    }

    public void draw(App app) {
        switch (this.colour) {
            case 0:
                app.image(app.getSprite("wall0"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 1:
                app.image(app.getSprite("wall1"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 2:
                app.image(app.getSprite("wall2"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 3:
                app.image(app.getSprite("wall3"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 4:
                app.image(app.getSprite("wall4"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            default: break;
        }
    }

}
