package com.EudyContreras.Snake.ParticleEffects;

import com.EudyContreras.Snake.AbstractModels.AbstractParticlesEffect;
import com.EudyContreras.Snake.Application.GameManager;
import com.EudyContreras.Snake.Application.GameSettings;
import com.EudyContreras.Snake.Identifiers.GameDebrisID;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class FruitSplashOne extends AbstractParticlesEffect {

	private GameDebrisID id;
	private Paint color;
	private double radius;
	private double decay;
	private double lifeTime = 1.0f;
	private double energyLoss = 0.9;

	public FruitSplashOne(GameManager game, Pane layer, Paint fill, double expireTime, double radius, double x,
			double y) {
		this.game = game;
		this.layer = layer;
		this.radius = radius / 2;
		this.shape = new Circle();
		this.shape.setRadius(this.radius);
		this.decay = 0.016 / expireTime;
		this.color = fill;
		this.velX = (Math.random() * (10 - -10 + 1) + -10) / 1.5;
		this.velY = (Math.random() * (10 - -10 + 1) + -10) / 1.5;
		this.x = x;
		this.y = y;
		this.shape.setFill(color);
		init();
	}

	public FruitSplashOne(GameManager game, Paint fill, double expireTime, double radius, double x, double y) {
		this.game = game;
		this.radius = radius / 2;
		this.shape = new Circle();
		this.shape.setRadius(this.radius);
		this.decay = 0.016 / expireTime;
		this.color = fill;
		this.velX = (Math.random() * (10 - -10 + 1) + -10) / 1.5;
		this.velY = (Math.random() * (10 - -10 + 1) + -10) / 1.5;
		this.x = x;
		this.y = y;
		this.shape.setFill(color);
		init();
	}

	public FruitSplashOne(GameManager game, Image image, double expireTime, double radius, double x, double y) {
		this.game = game;
		this.radius = radius / 2;
		this.imagePattern = new ImagePattern(image);
		this.shape.setRadius(this.radius);
		this.decay = 0.016 / expireTime;
		this.x = x;
		this.y = y;
		this.shape.setFill(imagePattern);
		init();
	}

	public void init() {
		if (layer == null)
			this.layer = game.getInnerParticleLayer();
		addToLayer(shape);
	}

	public void updateUI() {
		shape.setCenterX(x);
		shape.setCenterY(y);
		shape.setOpacity(lifeTime);
	}

	public void move() {
		super.move();
		lifeTime -= decay;
		velX *= energyLoss;
		velY *= energyLoss;
	}

	public void collide() {
	}

	public boolean isAlive() {

		return x < GameSettings.WIDTH && x > 0 && y < GameSettings.HEIGHT && y > 0 && lifeTime > 0;
	}

	public void draw() {

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
