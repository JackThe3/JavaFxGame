import javafx.scene.shape.Rectangle;

class Entity {
    enum Move {
        RIGHT,
        LEFT,
        DOWN
    }

    Rectangle hitbox;
    double speed;
    int ENTITY_WIDTH = 20;
    int ENTITY_HEIGHT = 20;

    public Entity(double x , double y , double speed) {
        this.hitbox = new Rectangle(x, y, ENTITY_WIDTH, ENTITY_HEIGHT);
        this.speed = speed;
    }

    public double getX(){
        return hitbox.getX();
    }

    public double getY(){
        return hitbox.getY();
    }

    public int getENTITY_WIDTH() {
        return ENTITY_WIDTH;
    }

    public int getENTITY_HEIGHT() {
        return ENTITY_HEIGHT;
    }

    public void move(Move direction){
        if (direction == Move.LEFT){
            hitbox.setX(hitbox.getX()-speed);
        }
        else if (direction == Move.DOWN){
            hitbox.setY(hitbox.getY()+speed);
        }
        else{
            hitbox.setX(hitbox.getX()+speed);
        }
    }
}
