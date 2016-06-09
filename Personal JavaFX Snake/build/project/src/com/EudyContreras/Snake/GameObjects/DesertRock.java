
package com.EudyContreras.Snake.GameObjects;

import com.EudyContreras.Snake.AbstractModels.AbstractTile;
import com.EudyContreras.Snake.EnumIDs.GameLevelObjectID;
import com.EudyContreras.Snake.FrameWork.GameManager;
import com.EudyContreras.Snake.FrameWork.GameSettings;
import com.EudyContreras.Snake.ImageBanks.GameLevelImage;
import com.EudyContreras.Snake.Utilities.RandomGenerator;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Every static object or esthetic object in the game such as walls, boxes etc
 * is considered a tile. This class is the main tile class and can be used for
 * creating any level object.
 *
 * @author Eudy Contreras
 *
 */
public class DesertRock extends AbstractTile {
	private Rectangle2D collisionBounds;
	private GameManager game;
	float speed;

	public DesertRock(GameManager game, float x, float y, float speed, float velY, Image image, GameLevelObjectID id) {
		super(x, y, image, id);
		this.game = game;
		this.velX = 0;
		this.speed = speed;
		this.velY = velY;
		this.view.setTranslateX(x);
		this.view.setTranslateY(y);
		if(RandomGenerator.getRNG(1, 3) == 3){
			this.view.setImage(GameLevelImage.desert_rock_alt);
			this.width = image.getWidth()+5;
			this.height = image.getHeight()+5;
			this.view.setFitWidth(width);
			this.view.setFitHeight(height);
			this.y = y-5;
			this.x = x-5;
		}
		draw();
		adjustBounds();
	}

	public DesertRock(GameManager game, float x, float y, float velX, float velY, Image image) {
		super(x, y, image);
		this.game = game;
		this.velX = velX;
		this.velY = velY;
		this.view.setTranslateX(x);
		this.view.setTranslateY(y);
		draw();
		adjustBounds();
	}
	/**
	 * Method which initializes bounds for a specific object
	 */
	public void adjustBounds() {
		collisionBounds = new Rectangle2D(x+GameManager.ScaleX(12), y + GameManager.ScaleY(45), width - GameManager.ScaleX(60), height - GameManager.ScaleY(55));
	}

	/**
	 * Moves this object
	 */
	public void move() {
		x = x + velX;
	}
	/**
	 * Draws a bounding box
	 */
	public void draw() {
		drawBoundingBox();
	}
	/**
	 * Draws the bounding box of this object for debugging purposes
	 */
	public void drawBoundingBox() {

		if (GameSettings.DEBUG_MODE) {
			Rectangle bounds = new Rectangle(x+GameManager.ScaleX(12), y + GameManager.ScaleY(45), width - GameManager.ScaleX(60), height - GameManager.ScaleY(55));
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
		return collisionBounds;
	}

	public Bounds getCollisionBounds() {
		return this.view.getBoundsInParent();
	}

}