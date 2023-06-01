import java.io.*;
import java.util.*;
import javafx.geometry.Insets;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.image.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Invaders extends Application{
    double WIDTH = 800;
    double HEIGHT = 600;
    double PLANET_POSITION = 550;
    boolean GAME_OVER = false;

    int time = 0;
    int score = 0;
    int live = 3;

    private int spawnDelay = 200;
    long TickCounter = 0;

    Entity player = new Entity(WIDTH/2, 500, 8);

    Image ALIEN_IMAGE = new Image(new FileInputStream("enemy.png"), player.getENTITY_WIDTH(), player.getENTITY_HEIGHT(), false, false);
    Image ROCKET_IMAGE = new Image(new FileInputStream("rocket.png"), player.getENTITY_WIDTH(), player.getENTITY_HEIGHT(), false, false);

    private final ArrayList<Rectangle> projectiles = new ArrayList<>();
    private final ArrayList<Entity> enemies = new ArrayList<>();

    public Invaders() throws FileNotFoundException {
    }

    private boolean isGameRunning(){
        return !GAME_OVER;
    }

    @Override
    public void start(Stage primaryStage){
        PlayGround screen = new PlayGround();
        primaryStage.setScene(new Scene(new Pane(screen), WIDTH, HEIGHT));
        primaryStage.setTitle("Invaders");
        screen.requestFocus();

        screen.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case LEFT:
                    if (player.getX() < 0) break;
                    player.move(Entity.Move.LEFT);
                    break;
                case RIGHT:
                    if (player.getX()+ player.getENTITY_WIDTH() > WIDTH) break;
                    player.move(Entity.Move.RIGHT);
                    break;
                case SPACE:
                    Rectangle projectile = new Rectangle(5, 10, Color.WHITE);
                    projectile.relocate(player.getX()+10, player.getY() - 10);
                    projectiles.add(projectile);
                    break;

            }
        });

        Timeline game_thread = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            if (isGameRunning()){
                gameLogic();
                TickCounter++;
            }
            screen.paint();
        }));
        game_thread.setCycleCount(Timeline.INDEFINITE);
        game_thread.play();
        primaryStage.show();
    }

    /**
     * tick every second.
     */
    private void timer(){
        if (TickCounter % 100 == 0){
            time++;
            reduceSpawnDelay();
        }
    }

    private void reduceSpawnDelay(){
        if (time %10 == 0){
            if (spawnDelay > 50){
                spawnDelay -= 10;
            }
        }
    }

    private void gameLogic(){
        timer();
        moveAliens();
        alienHitPlanet();
        moveProjectiles();
        alienHit();
        livesCheck();
        cleanOutOfScreen();
        spawnEnemy();
    }

    private void stopGame(){
        GAME_OVER = true;
    }

    private void moveAliens(){
        for (Entity enemy :enemies){
            enemy.move(Entity.Move.DOWN);
        }
    }

    private void alienHitPlanet(){
        boolean damage = enemies.removeIf(enemy -> enemy.hitbox.getY() + enemy.hitbox.getHeight() > PLANET_POSITION);
        if (damage){
            live--;
        }
    }

    private void livesCheck(){
        if (live <= 0){
            stopGame();
        }
    }

    private void moveProjectiles(){
        for (Rectangle projectile :projectiles) {
            int projectileSpeed = 1;
            projectile.relocate(projectile.getLayoutX(), (projectile.getLayoutY() - projectileSpeed));
        }
    }

    private void alienHit(){
        for (Rectangle projectile :projectiles) {
            boolean hit = enemies.removeIf(enemy -> enemy.hitbox.contains(projectile.getLayoutX(), projectile.getLayoutY()));
            if (hit){
                increaseLives();
            }
        }
    }

    private void increaseLives(){
        score++;
        if (score %10 == 0){
            live++;
        }
    }

    private void cleanOutOfScreen() {
        projectiles.removeIf(projectile -> projectile.getLayoutY() < 0);
    }

    private void spawnEnemy() {
        double RANDOM_SEED = Math.random();
        double randomPoz = (int) ((WIDTH - 20) * RANDOM_SEED);
        if (TickCounter % spawnDelay == 0) {
            Entity enemy = new Entity(randomPoz, 10, 0.1 + RANDOM_SEED * 0.5);
            enemies.add(enemy);
        }
    }

    class PlayGround extends Pane {
        public PlayGround() {
            setWidth(WIDTH);
            setHeight(HEIGHT);
            setMaxSize(WIDTH, HEIGHT);
            setFocusTraversable(true);
            setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(-100))));
        }

        public void paintPlayer(){
            ImageView image = new ImageView(ROCKET_IMAGE);
            image.setX(player.getX());
            image.setY(player.getY());
            this.getChildren().add(image);
        }

        public void paintProjectiles(){
            for (Rectangle projectile :projectiles) {
                this.getChildren().add(projectile);
            }
        }

        public void paintAliens(){
            for (Entity enemy:enemies) {
                ImageView imageView = new ImageView(ALIEN_IMAGE);
                imageView.setX(enemy.getX());
                imageView.setY(enemy.getY());
                this.getChildren().add(imageView);
            }
        }

        public void paintUI(){
            Line planet = new Line(0, PLANET_POSITION,WIDTH, PLANET_POSITION);
            planet.setStroke(Color.WHITE);
            planet.setStrokeWidth(5);
            this.getChildren().add(planet);

            Text cas = new Text(WIDTH*(1.0/10), 580, "Time:" + time);
            cas.setStroke(Color.WHITE);
            cas.setFont(new Font(20));
            this.getChildren().add(cas);

            Text score = new Text(WIDTH*(1.0/2)-50, 580, "Score:" + Invaders.this.score);
            score.setStroke(Color.WHITE);
            score.setFont(new Font(20));
            this.getChildren().add(score);

            Text live = new Text(650, 580, "Lives:" + Invaders.this.live);
            live.setStroke(Color.WHITE);
            live.setFont(new Font(20));
            this.getChildren().add(live);
        }

        public void paintRETRO(){
            for (int i = 0; i < HEIGHT; i=i+3) {
                Line l = new Line(0,i,WIDTH, i);
                this.getChildren().add(l);
            }
        }

        public void paintGameOver(){
            Text rip = new Text("GAME OVER");
            rip.setFont(new Font(40));
            rip.relocate((WIDTH/2) - rip.getBoundsInLocal().getWidth()/2 ,HEIGHT/2 + (rip.getLayoutBounds().getHeight() / 4));
            rip.setStroke(Color.WHITE);
            this.getChildren().add(rip);
        }

        public void paint() {
            this.getChildren().clear();
            paintPlayer();
            paintProjectiles();
            paintAliens();
            paintUI();
            if (!isGameRunning()){
                paintGameOver();
            }
            paintRETRO();
        }
    }
}