package com.EudyContreras.Snake.DebrisEffects;

import com.EudyContreras.Snake.AbstractModels.AbstractDebrisEffect;
import com.EudyContreras.Snake.EnumIDs.GameDebrisID;
import com.EudyContreras.Snake.FrameWork.GameLoader;
import com.EudyContreras.Snake.FrameWork.GameManager;
import com.EudyContreras.Snake.FrameWork.GameSettings;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class SandStorms extends AbstractDebrisEffect {

	private GameDebrisID id;
	private double radius;
	private double decay;
	private double lifeTime = 6.0f;
	private double width = GameSettings.SAND_SIZE/GameLoader.ResolutionScaleX;
	private double height = GameSettings.SAND_SIZE/GameLoader.ResolutionScaleY;
	private double expireTime = Math.random() * (1 - 0.01 + 1) + 0.01;

	public SandStorms(GameManager game, Image image, double expireTime, double radius, double x, double y) {
		this.game = game;
		this.radius = (radius / 2)/ (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		this.shape = new Circle(radius, x, y);
		this.imagePattern = new ImagePattern(image);
		this.shape.setRadius(this.radius);
		this.decay = 0.016 / expireTime;
		this.x = x;
		this.y = y;
		this.velX = Math.random() * (8 - 2 + 1) + 2 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		this.velY = Math.random() * (8 - -5 + 1) + -5 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		init();
	}

	public SandStorms(GameManager game, Image image, double expireTime, double x, double y) {
		this.game = game;
		this.setView(new ImageView(image));
		this.getView().setFitWidth(width);
		this.getView().setFitHeight(height);
		this.decay = 0.016 / this.expireTime;
		this.x = x;
		this.y = y;
		this.velX = Math.random() * (6 - 2 + 1) + 2 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		this.velY = Math.random() * (8 - -5 + 1) + -5 / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		init();
	}

	public void init() {
		if (getShape() != null) {
			shape.setFill(imagePattern);
			game.getOuterParticleLayer().getChildren().add(getShape());
		}
		if (getView() != null) {
			game.getOuterParticleLayer().getChildren().add(getView());
		}
	}

	public void update() {
		super.move();
		lifeTime -= decay;
		velX += GameSettings.WIND_SPEED / (GameLoader.ResolutionScaleX + GameLoader.ResolutionScaleY / 2);
		velY -= 0.002;
	}

	public void move() {
		if (shape != null) {
			shape.setCenterX(x);
			shape.setCenterY(y);
			shape.setOpacity(lifeTime);
		}
		if (getView() != null) {
			getView().setTranslateX(x);
			getView().setTranslateY(y);
			// view.setOpacity(lifeTime);
		}

	}

	public void collide() {
	}

	public boolean isAlive() {

		return x < GameSettings.WIDTH && y < GameSettings.HEIGHT && y > 0 && lifeTime > 0;
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