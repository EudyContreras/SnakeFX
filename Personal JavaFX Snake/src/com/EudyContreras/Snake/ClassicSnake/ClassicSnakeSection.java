package com.EudyContreras.Snake.ClassicSnake;

import com.EudyContreras.Snake.AbstractModels.AbstractSection;
import com.EudyContreras.Snake.Application.GameManager;
import com.EudyContreras.Snake.Application.GameSettings;
import com.EudyContreras.Snake.FrameWork.PlayerMovement;
import com.EudyContreras.Snake.GameObjects.LevelBounds;
import com.EudyContreras.Snake.Identifiers.GameObjectID;
import com.EudyContreras.Snake.Identifiers.GameStateID;
import com.EudyContreras.Snake.ImageBanks.GameImageBank;
import com.EudyContreras.Snake.ParticleEffects.SectionDisintegration;
import com.EudyContreras.Snake.Utilities.RandomGenUtility;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

public class ClassicSnakeSection extends AbstractSection {
	private double particleLife;
	private double particleSize;
	private double fadeValue = 1.0;
	private boolean fade = false;
	private boolean dead = false;
	private boolean blowUp = true;
	private ClassicSnake classicSnake;
	private GameManager game;
	private Paint tailFill;
	private ImagePattern normalPattern;
	private ImagePattern blurredPattern;
	private AbstractSection previousSection;
	private ClassicSnakeSectionManager sectManager;

	public ClassicSnakeSection(ClassicSnake snake, GameManager game, Pane layer, Node node, double x, double y, GameObjectID id,
			PlayerMovement Direction, int numericID) {
		super(game, layer, node, id);
		this.game = game;
		this.classicSnake = snake;
		this.numericID = numericID;
		this.sectManager = game.getSectManagerThree();
		if (this.numericID <= 0) {
			if (Direction == PlayerMovement.MOVE_UP) {
				this.setLastDirection(Direction);
				this.y = y + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.x = x;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.MOVE_DOWN) {
				this.setLastDirection(Direction);
				this.y = y - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.x = x;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.MOVE_LEFT) {
				this.setLastDirection(Direction);
				this.x = x + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.y = y;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.MOVE_RIGHT) {
				this.setLastDirection(Direction);
				this.x = x - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.y = y;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			} else if (Direction == PlayerMovement.STANDING_STILL) {
				this.setLastDirection(Direction);
				this.x = x - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
				this.y = y;
				this.r = snake.getR();
				this.velX = snake.getVelX();
				this.velY = snake.getVelY();
				snake.setNeighbor(this);
			}
		} else if (this.numericID > 0) {
			for (int i = sectManager.getSectionList().size() - 1; i >= 0; i--) {
				AbstractSection previousSect = sectManager.getSectionList().get(i);
				if (previousSect.getNumericID() == this.numericID - 1) {
					previousSection = previousSect;
					switch (previousSect.getLastDirection()) {
					case MOVE_UP:
						setLastDirection(PlayerMovement.MOVE_UP);
						this.y = previousSect.getY() + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.x = previousSect.getX();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case MOVE_DOWN:
						setLastDirection(PlayerMovement.MOVE_DOWN);
						this.y = previousSect.getY() - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.x = previousSect.getX();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case MOVE_LEFT:
						setLastDirection(PlayerMovement.MOVE_LEFT);
						this.x = previousSect.getX() + this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.y = previousSect.getY();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case MOVE_RIGHT:
						setLastDirection(PlayerMovement.MOVE_RIGHT);
						this.x = previousSect.getX() - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.y = previousSect.getY();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					case STANDING_STILL:
						setLastDirection(PlayerMovement.STANDING_STILL);
						this.x = previousSect.getX() - this.circle.getRadius() * GameSettings.SECTION_DISTANCE;
						this.y = previousSect.getY();
						this.r = previousSect.getR();
						this.velX = previousSect.getVelX();
						this.velY = previousSect.getVelY();
						break;
					}
				}
			}
		}
		loadPatterns();
	}
	private void loadPatterns() {
		this.normalPattern = new ImagePattern(GameImageBank.classicSnakeBody);
		this.blurredPattern = new ImagePattern(GameImageBank.classicSnakeBodyBlurred);
		this.tailFill = new ImagePattern(GameImageBank.transparentFill);

	}
	public void move() {
		this.circle.setRadius(GameSettings.CLASSIC_SNAKE_SIZE);
		checkBounds();
		disguiseLast();
		sectionAdjustment();
		if (ClassicSnake.LEVEL_COMPLETED == false && ClassicSnake.KEEP_MOVING && game.getStateID()!=GameStateID.GAME_MENU)
			super.move();
		if (lastPosition.size() > 0) {
			if (x == lastPosition.get(0).getX() && y == lastPosition.get(0).getY()) {
				removeLatestLocation();
				if (lastDirection.get(0) == PlayerMovement.MOVE_UP) {
					setLastDirection(PlayerMovement.MOVE_UP);
					removeLatestDirection();
					velX = 0;
					velY = -GameSettings.SNAKE_THREE_SPEED;
					r = 180;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_UP, this.numericID + 1);
				} else if (lastDirection.get(0) == PlayerMovement.MOVE_DOWN) {
					setLastDirection(PlayerMovement.MOVE_DOWN);
					removeLatestDirection();
					velX = 0;
					velY = GameSettings.SNAKE_THREE_SPEED;
					r = 0;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_DOWN, this.numericID + 1);
				} else if (lastDirection.get(0) == PlayerMovement.MOVE_LEFT) {
					setLastDirection(PlayerMovement.MOVE_LEFT);
					removeLatestDirection();
					velY = 0;
					velX = -GameSettings.SNAKE_THREE_SPEED;
					r = 90;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_LEFT, this.numericID + 1);
				} else if (lastDirection.get(0) == PlayerMovement.MOVE_RIGHT) {
					setLastDirection(PlayerMovement.MOVE_RIGHT);
					removeLatestDirection();
					velY = 0;
					velX = GameSettings.SNAKE_THREE_SPEED;
					r = -90;
					sectManager.addNewCoordinates(new Point2D(x, y), PlayerMovement.MOVE_RIGHT, this.numericID + 1);
				}
			}
		}
	}
	public void logicUpdate(){
		fadeDeath();
		if(ClassicSnake.DEAD)
		loseAccelaration();
	}
	public void loseAccelaration(){
		this.velX = velX*0.985;
		this.velY = velY*0.985;
	}
	public void setMotionBlur(){
		if(classicSnake.getSpeedThrust()){
			this.circle.setFill(blurredPattern);
		}
		else{
			this.circle.setFill(normalPattern);
		}
	}

	public void hideLast() {
		if (this.numericID == ClassicSnake.NUMERIC_ID - 1) {
			this.circle.setVisible(false);
		} else if (this.numericID != ClassicSnake.NUMERIC_ID - 1) {
			this.circle.setVisible(true);
		}
	}

	public void disguiseLast() {
		if (!ClassicSnake.DEAD) {
			if (this.numericID == ClassicSnake.NUMERIC_ID - 1) {
				this.circle.setFill(tailFill);
			}
			else if (this.numericID != ClassicSnake.NUMERIC_ID - 1) {
				setMotionBlur();
			}
		}
	}
	public void fadeDeath() {
		if (fade == true) {
			fadeValue -= 0.03;
			this.circle.setOpacity(fadeValue);
			if (fadeValue <= 0) {
				fadeValue = 0;
			}
		}
	}

	public void checkBounds() {
		if (!dead) {
			if (x < LevelBounds.MIN_X - radius) {
				x = (float) (LevelBounds.MAX_X + radius);
			} else if (x > LevelBounds.MAX_X + radius) {
				x = (float) (LevelBounds.MIN_X - radius);
			} else if (y < LevelBounds.MIN_Y - radius) {
				y = (float) (LevelBounds.MAX_Y + radius);
			} else if (y > LevelBounds.MAX_Y + radius) {
				y = (float) (LevelBounds.MIN_Y - radius);
			}
		}
	}

	public void sectionAdjustment() {
		if (previousSection != null && !dead) {
			if (x > LevelBounds.MIN_X + radius && x < LevelBounds.MAX_X - radius && y >  LevelBounds.MIN_Y + radius
					&& y < LevelBounds.MAX_Y - radius) {
				if (this.direction == PlayerMovement.MOVE_DOWN) {
					if (previousSection.getY() - y >= this.radius) {
						y = previousSection.getY() - this.radius;
					}
				}
				if (this.direction == PlayerMovement.MOVE_UP) {
					if (y - previousSection.getY() >= this.radius) {
						y = previousSection.getY() + this.radius;
					}
				}
				if (this.direction == PlayerMovement.MOVE_LEFT) {
					if (x - previousSection.getX() >= this.radius) {
						x = previousSection.getX() + this.radius;
					}
				}
				if (this.direction == PlayerMovement.MOVE_RIGHT) {
					if (previousSection.getX() - x >= this.radius) {
						x = previousSection.getX() - this.radius;
					}
				}
			}
		}
	}
	public int currentDistance(){
		return (int) (Math.hypot(x - previousSection.getX(), y - previousSection.getY()) - circle.getRadius() - circle.getRadius());
	}
	public void blowUp() {
		fade = true;
		if (blowUp == true) {
			for (int i = 0; i < GameSettings.MAX_DEBRIS_AMOUNT; i++) {
				if (GameSettings.ALLOW_VARIATIONS) {
					particleSize = (Math.random() * (15 - 7 + 1) + 7);
					particleLife = (Math.random() * (1.5 - 0.5 + 1) + 0.5);
				}
				game.getDebrisManager().addParticle(new SectionDisintegration(game, GameImageBank.classicSnakeBodyDebris,
						particleLife, particleSize, (double) (x + this.radius / 2), (double) (y + this.radius / 2)));
			}
			blowUp = false;
		}
	}

	public void displace(){
		this.velX = RandomGenUtility.getRandom(-2,2);
		this.velY = RandomGenUtility.getRandom(-2,2);
		if (this.numericID == ClassicSnake.NUMERIC_ID - 1) {
			this.circle.setVisible(false);
		}
	}
	public void die() {
		dead = true;
		displace();
	}
	public Rectangle2D getBounds() {

		return new Rectangle2D(x - radius / 2, y - radius / 2, radius, radius);
	}
}
