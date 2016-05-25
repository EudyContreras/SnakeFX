package com.SnakeGame.Particles;

import com.SnakeGame.FrameWork.GameLoader;
import com.SnakeGame.FrameWork.Settings;
import com.SnakeGame.FrameWork.SnakeGame;
import com.SnakeGame.IDenums.GameDebrisID;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class RainStorm extends AbstractDebrisEffect {

	private GameDebrisID id;
	private double radius;
	private double decay;
	private double lifeTime = 4.0f;
	private double width = Settings.SAND_SIZE;
	private double height = Settings.SAND_SIZE;
	private double expireTime = Math.random() * (1 - 0.01 + 1) + 0.01;

	public RainStorm(SnakeGame game, Image image, double expireTime, double radius, double x, double y) {
		this.game = game;
		this.radius = radius / 2;
		this.shape = new Circle(radius, x, y);
		this.imagePattern = new ImagePattern(image);
		this.shape.setRadius(this.radius);
		this.decay = 0.016 / expireTime;
		this.x = x;
		this.y = y;
		this.velY = Math.random() * (8 - 2 + 1) + 2 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		this.velX = Math.random() * (8 - -5 + 1) + -5 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		init();
	}

	public RainStorm(SnakeGame game, Image image, double expireTime, double x, double y) {
		this.game = game;
		this.view = new ImageView(image);
		this.view.setFitWidth(width);
		this.view.setFitHeight(height);
		this.decay = 0.016 / this.expireTime;
		this.x = x;
		this.y = y;
		this.velY = Math.random() * (6 - 2 + 1) + 2 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		this.velX = Math.random() * (8 - -5 + 1) + -5 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		init();
	}

	public void init() {
		if (shape != null) {
			shape.setFill(imagePattern);
			game.getSixthLayer().getChildren().add(shape);
		}
		if (view != null) {
			game.getSixthLayer().getChildren().add(view);
		}
	}

	public void update() {
		super.move();
		lifeTime -= decay;
		velY += Settings.WIND_SPEED / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		velX -= 0.002;
	}

	public void move() {
		if (shape != null) {
			shape.setCenterX(x);
			shape.setCenterY(y);
			shape.setOpacity(lifeTime);
		}
		if (view != null) {
			view.setTranslateX(x);
			view.setTranslateY(y);
		}

	}

	public void collide() {
	}

	public boolean isAlive() {

		return x < Settings.WIDTH && y < Settings.HEIGHT  && lifeTime > 0;
	}

	public void draw(GraphicsContext gc) {

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