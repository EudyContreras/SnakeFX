package com.EudyContreras.Snake.InputHandlers;

import com.EudyContreras.Snake.FrameWork.GameManager;
import com.EudyContreras.Snake.FrameWork.GameSettings;
import com.EudyContreras.Snake.FrameWork.PlayerMovement;

import javafx.event.EventHandler;
import javafx.scene.input.SwipeEvent;

/**
 * Class responsible for processing touch gestures
 * @author Eudy Contreras
 *
 */
public class InputManagerGestures {
	public InputManagerGestures(){
	}
	/**
	 * Method that will create an action according to the nature of the
	 * gesture.
	 * @param game
	 */
	public void processGestures(GameManager game) {
		if(GameSettings.ALLOW_TOUCH_CONTROL){
		game.getScene().setOnSwipeUp(new EventHandler<SwipeEvent>() {

			public void handle(SwipeEvent event) {
				game.getGameLoader().getPlayerTwo().setDirection(PlayerMovement.MOVE_UP);
				event.consume();
			}
		});
		game.getScene().setOnSwipeDown(new EventHandler<SwipeEvent>() {

			public void handle(SwipeEvent event) {
				game.getGameLoader().getPlayerTwo().setDirection(PlayerMovement.MOVE_DOWN);
				event.consume();
			}
		});
		game.getScene().setOnSwipeLeft(new EventHandler<SwipeEvent>() {
			public void handle(SwipeEvent event) {
				game.getGameLoader().getPlayerTwo().setDirection(PlayerMovement.MOVE_LEFT);
				event.consume();
			}
		});
		game.getScene().setOnSwipeRight(new EventHandler<SwipeEvent>() {
			public void handle(SwipeEvent event) {
				game.getGameLoader().getPlayerTwo().setDirection(PlayerMovement.MOVE_RIGHT);
				event.consume();
			}
		});
		}
	}
}