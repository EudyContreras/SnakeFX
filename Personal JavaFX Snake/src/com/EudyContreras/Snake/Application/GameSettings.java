package com.EudyContreras.Snake.Application;

import com.EudyContreras.Snake.FrameWork.GameLoader;

/**
 * This class contains all global settings within the game which can be easily
 * modified. The following settings can be used to increase the speed of the
 * snake relative to the size of the snake. These are the valid values that can
 * be used Good settings for speed and size : 4/24 4/28 5/25 5/30 6/24 6/30 7/28
 * 8/32 9/27 10/30 11/33 12/24 12/36 13/26 14/28 15/30
 *
 * @author Eudy Contreras
 *
 */
public class GameSettings {

	public static String PLAYER_ONE_NAME = "";
	public static String PLAYER_TWO_NAME = "";

	public static double SIZE_SCALE = 1.0;
	public static double FRAME_SCALE = 1.4f;

	public static int PATH_FINDING_CELL_SIZE = 40;

	public static int MIN_X = 0;
	public static int MIN_Y = 81;

	public static int WIDTH = (int) (1920/GameLoader.ResolutionScaleX);
	public static int HEIGHT = (int) (1080/GameLoader.ResolutionScaleY);

	public static int SCREEN_WIDTH =  WIDTH + 40;
	public static int SCREEN_HEIGHT = HEIGHT + 80;

	public static final String IMAGE_SOURCE_DIRECTORY = "com/EudyContreras/Snake/ImageFiles/";

	public static int APPLE_COUNT = 40;
	public static int BUFF_COUNT = 0;
	public static int SECTIONS_TO_ADD = 1;
	public static int SNAKE_SPEED = 1; // must be a number which the size of the result remains whole
	public static int SECTION_SIZE = 30;

	public static int SNAKE_ONE_SPEED = 1;
	public static int SNAKE_TWO_SPEED = 1;
	public static int SNAKE_THREE_SPEED = 1;

	public static double SLITHER_SPEED = 8;
	public static int SLITHER_SIZE = 30;

	public static double PLAYER_ONE_SIZE = 22; // Must be even or divisible by two 24 25 26 27 28 29 30 May still bugout while teleporting
	public static double PLAYER_TWO_SIZE = 22; // Must be even or divisible by two
	public static double CLASSIC_SNAKE_SIZE = 30;
	public static double SECTION_DISTANCE = 1; // Must be a number divisible by the speed

	public static int TURN_DELAY = 2;
	public static int BITE_DELAY = 6;
	public static int IMMUNITY_TIME = 20;
	public static int COLLISION_DELAY = 0;

	public static double HEALTH_REGENERATION_SPEED = 0.2;
	public static double ENERGY_COMSUMPTION_SPEED = 3;
	public static double ENERGY_REGENRATION_SPEED = 1.0;
	public static double ENERGY_REGENRATION_DELAY = 40;
	public static double DAMAGE_AMOUNT = 25;

	public static double PLAYER_ONE_SPEED = 8.0;
	public static double PLAYER_TWO_SPEED = 8.0;
	public static final double CLASSIC_SNAKE_SPEED = 9.0;  
	public static final double PLAYER_SIZE = 24;

	public static double PLAYER_HEALTH = 100.0;

	public static double GlOBAL_ILLUMINATION = 1.75;
	public static double GLOBAL_SPECULARITY = 2.870;

	public static int BLUR_RANDOMNESS = 500;
	public static int MAX_AMOUNT_OF_BACKGROUND_OBJECT = 100;
	public static int PARTICLE_LIMIT = 250;
	public static int MAX_DEBRIS_AMOUNT = 45;
	public static int SAND_SPAWN_DELAY = 1;
	public static int SAND_AMOUNT = 5;
	public static int DIRT_AMOUNT = 4;

	public static double SAND_SIZE = 6;
	public static double WIND_FORCE = 2.7;
	public static double WIND_SPEED = 2.0;

	public static boolean WINDOW_ALWAYS_VISIBLE = true;
	public static boolean SHOW_SPLASHSCREEN = true;
	public static boolean OBJECT_TRACKER = false;

	public static boolean ALLOW_AI_CONTROL = true;
	public static boolean ALLOW_AI_TELEPORT = true;
	public static boolean ALLOW_SNAKE_GROWTH = true;
	public static boolean ALLOW_SELF_COLLISION = true;
	public static boolean ALLOW_TOUCH_CONTROL = false;
	public static boolean ALLOW_AUTOMATIC_EATING = true;
	public static boolean ALLOW_DAMAGE_IMMUNITY = true;
	public static boolean ALLOW_ROCK_COLLISION = true;
	public static boolean ALLOW_FAST_TURNS = false;

	public static boolean ALLOW_DIRT = true;
	public static boolean SAND_STORM = true;
	public static boolean RAIN_STORM = false;

	public static boolean ALLOW_ASTAR_GRAPH = true;
	public static boolean SHOW_ASTAR_GRAPH = false;
	public static boolean DEBUG_MODE = false;
	public static boolean RENDER_GAME = false;
	public static boolean PARENT_CACHE = false;

	public static boolean LOAD_SPIKE_FENCE = false;

	public static boolean ALLOW_MOUSE_INPUT = false;
	public static boolean ALLOW_VARIATIONS = true;
	public static boolean ADD_LIGHTING = true;
	public static boolean ADD_GLOW = true;


}
