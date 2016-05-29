package com.SnakeGame.AbstractModels;

import com.SnakeGame.FrameWork.GameManager;
import com.SnakeGame.FrameWork.GameSettings;
import com.SnakeGame.IDenums.GameDebrisID;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public abstract class AbstractDebrisEffect {

	protected GameManager game;
	protected Circle shape;
	private ImageView view;
	protected ImagePattern imagePattern;
	protected DropShadow borderGlow;
	protected Bloom bloom;
	protected BoxBlur motionBlur;
	protected double x;
	protected double velX;
	protected double y;
	protected double velY;
	protected double r;
	protected double velR;

	public abstract void update();

	public abstract void draw(GraphicsContext gc);

	public void move() {
		x = x + velX * GameSettings.FRAME_SCALE;
		y = y + velY * GameSettings.FRAME_SCALE;
		r = r + velR * GameSettings.FRAME_SCALE;
	}

	public abstract void collide();

	public abstract boolean isAlive();

	public abstract GameDebrisID getID();

	public abstract void setID(GameDebrisID id);

	public abstract Rectangle2D getBoundsTop();

	public abstract Rectangle2D getBounds();

	public abstract Rectangle2D getBoundsRight();

	public abstract Rectangle2D getBoundsLeft();

	public Circle getShape() {
		return shape;
	}

	public ImageView getView() {
		return view;
	}

	public void setView(ImageView view) {
		this.view = view;
	}
}