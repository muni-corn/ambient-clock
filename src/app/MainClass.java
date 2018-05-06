
package app;

import static org.lwjgl.opengl.GL11.*;

import musicaflight.avianutils.AvianApp;
import musicaflight.avianutils.AvianUtils;

public class MainClass extends AvianApp {

	static MainScreen m;

	public void construct() {
		m = new MainScreen();
	}

	int shader;

	public void setupGL() {
		glEnable(GL_TEXTURE_2D);
		//		glEnable(GL_STENCIL_TEST);
		glShadeModel(GL_SMOOTH);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);

		glLightModelfv(GL_LIGHT_MODEL_AMBIENT, AvianUtils.asFlippedFloatBuffer(0f, 0f, 0f, 1f));

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public void logic() {
		m.logic();
	}

	public void render() {

		getCam().applyTranslations();
		getCam().applyAvianOrthoMatrix();

		m.render();

	}

	public String customTitle() {
		return !ClockSettings.useTimer ? "Ambient Clock" : DataHost.getTimeRemainingAsString();
	}

	public void checkAudio() {
	}

	public static void main(String[] args) {
		MainClass main = new MainClass();
		main.addFontBank(new Fonts());
		main.addImageBank(new Images());
		main.setAppNameAndVersion("Ambient Clock", "Specimen 5");
		AvianApp.setInfoSubject("Information");
		AvianApp.setInfo("Ambient Clock is a program written by Harrison Thorne.", "This program may NOT be distributed without written permission.", "", "Press F1 to close this screen.");
		AvianApp.addShutdownTask(new ShutdownTask() {

			@Override
			public void run() {
				DataHost.saveData();
			}
		});
		main.start();
	}

}
