package application;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class DebrisEffect1 extends DebrisEffect{

	GameDebrisID id;
	BlendMode blendMode = BlendMode.ADD;
	Random rand = new Random();
	Paint color;
	double radius;
	double decay;
	float x;
	float y;
	float r;
	float velX = rand.nextInt(15 - -15 + 1) + -15;
	float velY = rand.nextInt(15 - -15 + 1) + -15;
	float velR;
	float lifeTime = 1.0f;
	double width;
	double height;
	boolean isAlive = false;
	boolean removable = false;
	int depth = 75;
	int amount = 200;
	double greenRange = rand.nextInt(180 - 40 + 1) +40;
	Point2D velocity = new Point2D((Math.random()*(15 - -15 + 1) + -15), Math.random()*(15 - -15 + 1) + -15);
	
	public DebrisEffect1(Game game, int amount, float x, float y,  Point2D velocity) {
		this.particleManager = game.getParticleManager();
		this.game = game;
		this.shape.setRadius(radius);
		this.velocity = velocity;
		this.decay = 0.016; 
		this.amount = amount;
		this.x = x;
		this.y = y;
		this.particleManager = game.getParticleManager();
		this.game = game;
		if(Settings.ADD_GLOW){
		borderGlow.setOffsetY(0f);
		borderGlow.setOffsetX(0f);
		borderGlow.setColor(Color.rgb(255, (int)greenRange, 0,1));
		borderGlow.setWidth(depth);
		borderGlow.setHeight(depth);
		borderGlow.setSpread(0.5);
		}
		init();
		
	}
	public void init(){
        RadialGradient gradient1 = new RadialGradient(0,
            	.1,
                100,
                100,
                250,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(0.3, Color.YELLOW),
                new Stop(1, Color.RED));
        game.getDebrisLayer().getChildren().add(shape);
        shape.setBlendMode(blendMode);
        shape.setFill(gradient1);
        if(Settings.ADD_GLOW)
        shape.setEffect(borderGlow);
	}
	public void update(){
		x += velocity.getX()*(Settings.FRAMECAP);
		y += velocity.getY()*(Settings.FRAMECAP);
		lifeTime -= decay*(Settings.FRAMECAP);
//		if(lifeTime<0.01 || x>Settings.WIDTH || x<0 || y>Settings.HEIGHT  || y<0){
//			game.getDebrisLayer().getChildren().remove(shape);
//		}
	}
	public void move(){

		shape.setCenterX(x);
		shape.setCenterY(y);

	}
	public void collide(){

	}
	public boolean isAlive() {
		return x<Settings.WIDTH && x>0 && y<Settings.HEIGHT  && y>0 && lifeTime>0;
	}

	public void draw(GraphicsContext gc) {
		 shape.setOpacity(lifeTime);
		
	}

	public Rectangle2D getBounds() {
		
		return null;
	}

	public Rectangle2D getBoundsTop() {
		
		return null;
	}

	public Rectangle2D getBoundsRight() {
		
		return null;
	}

	public Rectangle2D getBoundsLeft() {
		
		return null;
	}

	public GameDebrisID getID() {
		return id;
	}
	public void setID(GameDebrisID id) {
		this.id = id;	
	}		
}

