package com.SnakeGame.Utilities;

import com.SnakeGame.FrameWork.Settings;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This image utility class is used to pre-create images with specific effects
 * added to them. The image will be loaded, the effects will be applied and then
 * the Image will be saved to memory as a snapshot of the post effects image.
 * This class may additionally allow the user to save the post effects image to
 * a desired location on the near future.
 *
 * @author Eudy Contreras
 *
 */
public class ImageUtility {
	public static WritableImage wi;
	public static DropShadow borderGlow = new DropShadow();
	public static MotionBlur motionBlur = new MotionBlur();
	public static GaussianBlur gaussianBlur = new GaussianBlur();
	public static Bloom bloom = new Bloom(0.1);
	public static Glow glow = new Glow(1.0);

	public static Image createImage(Node node) {

		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		int width = (int) node.getBoundsInLocal().getWidth();
		int height = (int) node.getBoundsInLocal().getHeight();
		wi = new WritableImage(width, height);
		node.snapshot(parameters, wi);
		return wi;

	}

	public static Image precreatedLightedImage(String path, double diffused, double specularMap, double width,
			double height) {
		Lighting lighting = new Lighting();
		Light.Point light = new Light.Point();
		Image img = new Image(loadResource(path), width, height, true, true);
		ImageView view = new ImageView(img);
		light.setX(-200);
		light.setY(450);
		light.setZ(200);
		lighting.setDiffuseConstant(diffused);
		lighting.setSpecularConstant(specularMap);
		lighting.setSurfaceScale(10.0);
		lighting.setLight(light);
		if (Settings.ADD_LIGHTING)
			view.setEffect(lighting);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image precreateShadedImages(String path, double diffused, double specularMap, double width,
			double height) {
		DropShadow shadow = new DropShadow(15, Color.BLACK);
		Image img = new Image(loadResource(path), width, height, true, true);
		ImageView view = new ImageView(img);
		shadow.setColor(Color.rgb(0, 0, 0, 0.8));
		shadow.setRadius(15);
		// shadow.setOffsetX(20);
		// shadow.setOffsetY(-15);
		if (Settings.ADD_LIGHTING)
			view.setEffect(shadow);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image precreatedLightedAndShadedImage(String path, double diffused, double specularMap, double width,
			double height) {
		DropShadow shadow = new DropShadow(15, Color.BLACK);
		Lighting lighting = new Lighting();
		Light.Point light = new Light.Point();
		Image img = new Image(loadResource(path), width, height, true, true);
		ImageView view = new ImageView(img);
		light.setX(-200);
		light.setY(450);
		light.setZ(180);
		lighting.setDiffuseConstant(diffused);
		lighting.setSpecularConstant(specularMap);
		lighting.setSurfaceScale(10.0);
		lighting.setLight(light);
		shadow.setColor(Color.rgb(0, 0, 0, 0.5));
		shadow.setRadius(15);
		shadow.setOffsetX(20);
		shadow.setOffsetY(-15);
		lighting.setContentInput(shadow);
		if (Settings.ADD_LIGHTING)
			view.setEffect(lighting);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image precreatedLightedAndShadedSnake(String path, double diffused, double specularMap, double width,
			double height) {
		DropShadow shadow = new DropShadow(15, Color.BLACK);
		Lighting lighting = new Lighting();
		Light.Point light = new Light.Point();
		Image img = new Image(loadResource(path), width, height, true, true);
		ImageView view = new ImageView(img);
		light.setX(-200);
		light.setY(450);
		light.setZ(200);
		lighting.setDiffuseConstant(diffused);
		lighting.setSpecularConstant(specularMap);
		lighting.setSurfaceScale(10.0);
		lighting.setLight(light);
		shadow.setColor(Color.rgb(0, 0, 0, 0.6));
		shadow.setRadius(20);
		shadow.setWidth(25);
		shadow.setHeight(25);
		lighting.setContentInput(shadow);
		if (Settings.ADD_LIGHTING)
			view.setEffect(lighting);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image precreateSnapshot(String path, double width, double height) {
		Image img = new Image(loadResource(path), width, height, true, true);
		ImageView view = new ImageView(img);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image preCreateShadedCircle(Color color, double diffused, double specularMap, double width,
			double height) {
		Lighting lighting = new Lighting();
		Light.Point light = new Light.Point();
		Image img;
		Circle circle = new Circle();
		circle.setFill(color);
		circle.setRadius(width);
		light.setX(340);
		light.setY(600);
		light.setZ(300);
		lighting.setDiffuseConstant(diffused);
		lighting.setSpecularConstant(specularMap);
		lighting.setSurfaceScale(8.0);

		lighting.setLight(light);
		if (Settings.ADD_LIGHTING)
			circle.setEffect(lighting);
		img = ImageUtility.createImage(circle);
		return img;
	}

	public static Image preCreateShadedGlowingCircle(Color color, double diffused, double specularMap, double width,
			double height) {
		Lighting lighting = new Lighting();
		Light.Point light = new Light.Point();
		Image img;
		Circle circle = new Circle();
		circle.setFill(color);
		circle.setRadius(width);
		light.setX(340);
		light.setY(600);
		light.setZ(300);
		lighting.setDiffuseConstant(diffused);
		lighting.setSpecularConstant(specularMap);
		lighting.setSurfaceScale(8.0);
		borderGlow.setOffsetY(0f);
		borderGlow.setOffsetX(0f);
		borderGlow.setSpread(20);
		borderGlow.setColor(color);
		borderGlow.setWidth(50);
		borderGlow.setHeight(20);
		borderGlow.setBlurType(BlurType.THREE_PASS_BOX);
		lighting.setLight(light);
		lighting.setContentInput(borderGlow);
		if (Settings.ADD_LIGHTING)
			circle.setEffect(lighting);
		img = ImageUtility.createImage(circle);
		return img;
	}

	public static Image preCreateShadedBackground(String path, double diffused, double specularMap, double width,
			double height) {
		DropShadow shadow = new DropShadow(15, Color.BLACK);
		Lighting lighting = new Lighting();
		Light.Point light = new Light.Point();
		Image img = new Image(loadResource(path), width, height, false, true);
		ImageView view = new ImageView(img);
		light.setX(1000);
		light.setY(1920);
		light.setZ(1000);
		lighting.setDiffuseConstant(diffused);
		lighting.setSpecularConstant(specularMap);
		lighting.setSurfaceScale(10.0);
		lighting.setLight(light);
		shadow.setColor(Color.rgb(0, 0, 0, 0.5));
		shadow.setRadius(15);
		shadow.setOffsetX(20);
		shadow.setOffsetY(-15);
		lighting.setContentInput(shadow);
		if (Settings.ADD_LIGHTING)
			view.setEffect(lighting);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image preCreateShadedDebris(String path, double diffused, double specularMap, double width,
			double height) {
		Lighting lighting = new Lighting();
		Light.Point light = new Light.Point();
		Image img = new Image(path, width, height, true, false, false);
		ImageView view = new ImageView(img);
		light.setX(0);
		light.setY(700);
		light.setZ(250);
		lighting.setDiffuseConstant(diffused);
		lighting.setSpecularConstant(specularMap);
		lighting.setSurfaceScale(5.0);
		lighting.setLight(light);
		if (Settings.ADD_LIGHTING)
			view.setEffect(lighting);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image preCreateGlowingImages(String path, Color color, double depth, double spread, double width,
			double height) {
		Image img = new Image(path, width, height, true, false, false);
		ImageView view = new ImageView(img);
		borderGlow.setOffsetY(0f);
		borderGlow.setOffsetX(-10f);
		borderGlow.setSpread(spread);
		borderGlow.setColor(color);
		borderGlow.setWidth(depth);
		borderGlow.setHeight(70);
		borderGlow.setBlurType(BlurType.GAUSSIAN);
		view.setEffect(borderGlow);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image preCreateGlowingCircle(Color color, double opacity, double depth, double spread, double width,
			double height) {
		Image img;
		Circle circle = new Circle();
		circle.setFill(Color.rgb(255, 200, 0, 1.0));
		circle.setRadius(20);
		borderGlow.setOffsetY(0f);
		borderGlow.setOffsetX(0f);
		borderGlow.setSpread(spread);
		borderGlow.setColor(color);
		borderGlow.setWidth(depth);
		borderGlow.setHeight(depth);
		borderGlow.setBlurType(BlurType.THREE_PASS_BOX);
		circle.setOpacity(opacity);
		circle.setEffect(borderGlow);
		img = ImageUtility.createImage(circle);
		return img;
	}

	public static Image preCreateAlternateGlowingCircle(Color color, double opacity, double depth, double spread,
			double width, double height) {
		Image img;
		Circle circle = new Circle();
		circle.setFill(Color.rgb(255, 150, 0, 1.0));
		circle.setRadius(20);
		borderGlow.setOffsetY(0f);
		borderGlow.setOffsetX(0f);
		borderGlow.setSpread(spread);
		borderGlow.setColor(color);
		borderGlow.setWidth(depth);
		borderGlow.setHeight(depth);
		borderGlow.setBlurType(BlurType.THREE_PASS_BOX);
		circle.setOpacity(opacity);
		circle.setEffect(borderGlow);
		img = ImageUtility.createImage(circle);
		return img;
	}

	public static Image preCreateImageWithBloom(String path, double threshold, double width, double height) {
		Image img = new Image(path, width, height, true, false, false);
		ImageView view = new ImageView(img);
		bloom.setThreshold(threshold);
		view.setEffect(bloom);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image preCreateImageWithBlur(String path, double radius, double width, double height) {
		Image img = new Image(path, width, height, true, false, false);
		ImageView view = new ImageView(img);
		gaussianBlur.setRadius(radius);
		view.setEffect(gaussianBlur);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image preCreateBrighterImage(String path, double glowLevel, double width, double height) {
		Image img = new Image(path, width, height, true, false, false);
		ImageView view = new ImageView(img);
		glow.setLevel(glowLevel);
		view.setEffect(glow);
		view.setFitWidth(width);
		view.setFitHeight(height);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static Image snapShotImage(String path, double width, double height) {
		Image img = new Image(path);
		ImageView view = new ImageView(img);
		img = ImageUtility.createImage(view);
		return img;
	}

	public static String loadResource(String image) {
		String url = "com/SnakeGame/Images/" + image;
		return url;
	}
}