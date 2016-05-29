
package com.SnakeGame.GameObjects;

import com.SnakeGame.AbstractModels.AbstractTile;
import com.SnakeGame.FrameWork.GameManager;
import com.SnakeGame.FrameWork.GameSettings;
import com.SnakeGame.IDEnums.GameLevelObjectID;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class represents a cactus which creates a moving
 * or wind caused waving illusion.
 * @author Eudy Contreras
 *
 */
public class DesertTrap extends AbstractTile {

	GameManager game;
	Rectangle bounds;
	Rectangle2D bounds2D;
	float speed;
	float oldX;

	public DesertTrap(GameManager game, float x, float y, float velX, float velR, Image image, GameLevelObjectID id) {
		super(x, y, image, id);
		this.oldX = x;
		if (GameSettings.SAND_STORM)
			this.velX = velX;
		this.velR = velR;
		this.game = game;
		this.view.setTranslateX(x);
		this.view.setTranslateY(y);
		this.draw();
		this.setBounds();
	}
	public void setBounds(){
		 bounds2D = new Rectangle2D(x+width*0.15, y+height*0.45, width*0.5, height*0.45);
	}
	/**
	 * Method which moves this object
	 */
	public void move() {
		super.move();

	}
	/**
	 * Method which makes this object
	 * move or rotate
	 */
	public void wave() {
		if (x > oldX + GameSettings.WIND_FORCE) {
			velX -= Math.random() * (0.35 - 0.01 + 1) + 0.01;
		}
		if (x <= oldX) {
			if (velX < GameSettings.WIND_FORCE)
				velX += 0.4f;
		}
	}

	/**
	 * Methods which draws a bounding box
	 */
	public void draw() {
		drawBoundingBox();
	}
	/**
	 * Method which creates and draws a bounding box
	 * for debugging purposes
	 */
	public void drawBoundingBox() {

		if (GameSettings.DEBUG_MODE) {
			bounds = new Rectangle(x+width*0.15, y+height*0.45, width*0.5, height*0.45);
			bounds.setStroke(Color.WHITE);
			bounds.setFill(Color.TRANSPARENT);
			bounds.setStrokeWidth(3);
			game.getSeventhLayer().getChildren().add(bounds);

		}
	}
	/**
	 * This methods will return specified bounds of this object
	 * based on coordinates and dimensions.
	 */
	public Rectangle2D getBounds() {
		return bounds2D;
	}

	public Rectangle2D getBoundsTop() {
		return new Rectangle2D(x + 20, y, width - 40, height);
	}

	public Rectangle2D getBoundsRight() {
		return new Rectangle2D(x + width - 20, y + 10, 20, height - 10);
	}

	public Rectangle2D getBoundsLeft() {
		return new Rectangle2D(x, y + 10, 20, height - 10);
	}

	public Bounds getCollisionBounds() {
		return this.view.getBoundsInParent();
	}

}
