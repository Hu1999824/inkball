package inkball;

public class Spawner {

    private int x;
    private int y;

    public Spawner(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(App app) {
        app.image(app.getSprite("entrypoint"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }

}
