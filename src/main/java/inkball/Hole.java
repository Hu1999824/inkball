package inkball;

public class Hole {

    private int x;
    private int y;
    private int coclour;

    public Hole(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCoclour() {
        return coclour;
    }

    public void setCoclour(int coclour) {
        this.coclour = coclour;
    }

    public void draw(App app) {
        switch (this.coclour) {
            case 0:
                app.image(app.getSprite("hole0"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 1:
                app.image(app.getSprite("hole1"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 2:
                app.image(app.getSprite("hole2"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 3:
                app.image(app.getSprite("hole3"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            case 4:
                app.image(app.getSprite("hole4"),x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
                break;
            default: break;
        }
    }

}
