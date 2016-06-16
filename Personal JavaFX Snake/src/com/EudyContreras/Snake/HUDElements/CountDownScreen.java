package com.EudyContreras.Snake.HUDElements;

import com.EudyContreras.Snake.FrameWork.GameManager;
import com.EudyContreras.Snake.FrameWork.GameSettings;
import com.EudyContreras.Snake.Identifiers.GameStateID;
import com.EudyContreras.Snake.ImageBanks.GameImageBank;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * This class aims to represent a count down shown in game which determines
 * whether or not the players can start. If the count says go the players are
 * allow to perform actions.
 *
 * @author Eudy Contreras
 *
 */
public class CountDownScreen {
	private int count;
	private int index;
	private double x;
	private double y;
	private double velX;
	private double velY;
	private double width;
	private double height;
	private double baseWidth;
	private double baseHeight;
	private double fade;
	private double scale;
	private double showCounter;
	private double panVelocityX;
	private double panVelocityY;
	private Boolean allowPan = false;
	private Boolean allowFade = false;
	private Boolean allowHide = false;
	private Boolean allowCount = true;
	private Boolean allowCheck = false;
	private Boolean allowCountdown = false;
	private FadeTransition fadeTransition;
	private ScaleTransition scaleTransition;
	private ParallelTransition synchronizer;
	private Pane layer;
	private Rectangle countView;
	private GameManager game;
	public static Boolean COUNTDOWN_OVER = false;

	/**
	 * Constructor which takes the main game class along with the width and the
	 * height of each of each element to be show. The parameter also takes a
	 * layer to which the elements will be added and displayed upon.
	 *
	 * @param game:Main game class which communicates with almost all the otherclasses
	 * @param width:Horizontal dimension of the UI element
	 * @param height:Vertical dimension of the UI element
	 * @param layer:Layer to which the UI element will be added
	 */
	public CountDownScreen(GameManager game, double width, double height, Pane layer) {
		this.game = game;
		this.width = width / GameManager.ScaleX;
		this.height = height / GameManager.ScaleY;
		this.baseWidth = this.width;
		this.baseHeight = this.height;
		this.layer = layer;
		this.initialize();
	}

	/**
	 * Method which gives a initial value to nearly all the predefined variables
	 * that are used by this count down class
	 */
	private void initialize() {
		this.index = 3;
		this.x = GameSettings.WIDTH / 2 - width / 2;
		this.y = GameSettings.HEIGHT / 2 - height / 2;
		this.fadeTransition = new FadeTransition(Duration.millis(900), countView);
		this.scaleTransition = new ScaleTransition(Duration.millis(900), countView);
		this.countView = new Rectangle(x, y, this.width, this.height);
		this.countView.setFill(GameImageBank.count_three);
		this.countView.setWidth(width);
		this.countView.setHeight(height);
		this.countView.setX(x);
		this.countView.setY(y);
		this.game.setStateID(GameStateID.COUNT_DOWN);
	}

	/**
	 * Method which starts the count down. This method can be called at the
	 * beggining of a level in order for allow for a count down before any of
	 * the players can perform any given action
	 */
	public void startCountdown() {
		this.game.setStateID(GameStateID.COUNT_DOWN);
		this.index = 3;
		this.x = (GameSettings.WIDTH / 2 - baseWidth / 2);
		this.y = (GameSettings.HEIGHT / 2 - baseHeight / 2)-GameManager.ScaleY(80);
		this.countView.setX(x);
		this.countView.setY(y);
		this.countView.setWidth(baseWidth);
		this.countView.setHeight(baseHeight);
		this.countView.setVisible(false);
		this.countView.setFill(GameImageBank.count_three);
		this.layer.getChildren().remove(countView);
		this.layer.getChildren().add(countView);
		this.game.setStateID(GameStateID.COUNT_DOWN);
		this.allowCountdown = true;
		this.fade = 0.3;
		this.scale = 0.2;
		this.count = 40;
		COUNTDOWN_OVER = false;
		this.countDown();
	}

	/**
	 * Resumes the countdown update
	 */
	public void resumeCountdow() {
		if (allowCountdown)
			return;
		allowCountdown = true;

	}

	/**
	 * Stops the countdown update
	 */
	public void stopCountdown() {
		if (!allowCountdown)
			return;
		allowCountdown = false;
	}
	private void startTransition(){
		if(index<0){
			scale = 1;
			fade = 0;
			fadeTransition.setDuration(Duration.millis(700));
		}
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(fade);
		fadeTransition.setCycleCount(1);

		scaleTransition.setFromX(1);
		scaleTransition.setFromY(1);
		scaleTransition.setToX(scale);
		scaleTransition.setToY(scale);
		scaleTransition.setCycleCount(1);

		synchronizer = new ParallelTransition(countView, fadeTransition, scaleTransition);
		synchronizer.play();
		synchronizer.setOnFinished(event -> {
			countDown();
		});
	}
	/**
	 * Method which updates all the functionality of this class: the movement,
	 * the fading and the panning are updated through this method.
	 */
	public void update() {
		if (allowCountdown) {
			count--;
			if (count <= 0) {
				count = 0;
				startTransition();
				allowCountdown = false;
				countView.setVisible(true);
			}
		}
	}

	/**
	 * Method which makes the UI element fade until the UI element is completely
	 * transparent
	 */
	@SuppressWarnings("unused")
	private void fade() {
		if (allowFade) {
			showCounter--;
			if (showCounter <= 0) {
				showCounter = 0.0;
				scale -= fade;
			}
			countView.setOpacity(scale);
			if (scale <= 0) {
				allowFade = false;
			}
		}

	}

	/**
	 * Method which zooms out the object: decreases the size of the object as
	 * long as the object is still visible.
	 */
	@SuppressWarnings("unused")
	private void panOut() {
		if (allowPan ) {
			if(showCounter <= 0){
			width -= panVelocityX;
			height -= panVelocityY;
			}
			countView.setWidth(width);
			countView.setHeight(height);
			if (height <= 0 || scale <= 0) {
				allowPan = false;
			}
		}
	}

	/**
	 * Method wich attempts to move the object from the center of the screen all
	 * the way to the left top corner of the screen until the object is no
	 * longer visible
	 */
	@SuppressWarnings("unused")
	private void hide() {
		if (allowHide) {
			countView.setX(x);
			countView.setY(y);
			if(showCounter <= 0){
			x += velX;
			y += velY;
			}
			if (x < 0 - width || y < 0 - height) {
				allowHide = false;
			}
		}

	}

	/**
	 * Method which checks the life of the current count being shown. If the
	 * lifetime of the current UI element is zero a count down happens where for
	 * example a three is replace by a two nad so forth until the last index is
	 * reach which triggers a specified event.
	 */
	@SuppressWarnings("unused")
	private void checkLife() {
		if (allowCheck) {
			if (scale <= 0) {
				countDown();
			}
		}
	}

	/**
	 * Method which visualizes the count down into images according to the
	 * current index of the count down.
	 */
	private void countDown() {
		allowCheck = false;
		if (allowCount) {
			if (index == -1) {
				go();
			}
			if (index == 0) {
				countView.setFill(GameImageBank.count_go);
				reset(1.0, true);
			}
			if (index == 1) {
				countView.setFill(GameImageBank.count_one);
				reset(1.0, false);
			}
			if (index == 2) {
				countView.setFill(GameImageBank.count_two);
				reset(1.0, false);
			}
			if (index == 3) {
				countView.setFill(GameImageBank.count_three);
				reset(1.0, false);
			}
		}
	}

	/**
	 * Method which resets the dimensions, opacity and position of the UI
	 * element in order to allow for another number or index to be shown. This
	 * reset method must be called at the end of every lifetime.
	 *
	 * @param life: lifetime assigned to the current index
	 */
	private void reset(double life, Boolean lastCount) {
		index -= 1;
		allowCountdown = true;
		x = (double) (GameSettings.WIDTH / 2 - baseWidth / 2);
		y = (double) (GameSettings.HEIGHT / 2 - baseHeight / 2)-GameManager.ScaleY(40);
		if (lastCount) {
			height = baseHeight - GameManager.ScaleY(80);
			width = baseWidth + GameManager.ScaleX(250);
			x = (double) (GameSettings.WIDTH / 2 - width / 2);
			y = (double) (GameSettings.HEIGHT / 2 - height / 2)-GameManager.ScaleY(40);
			countView.setWidth(width);
			countView.setHeight(height);
			countView.setX(x);
			countView.setY(y);
			game.getScoreKeeper().swipeUp();
			game.getGameHud().hideHUDCover();
			game.setStateID(GameStateID.GAMEPLAY);
			game.getScoreKeeper().startTimer();
			COUNTDOWN_OVER = true;
		}
	}

	/**
	 * Method which when called allows the players to perform their actions.
	 * This method gets called at the end of the count down
	 */
	private void go() {
		layer.getChildren().remove(countView);
		allowCountdown = false;
	}

}
