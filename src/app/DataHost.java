
package app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

import musicaflight.avianutils.*;
import musicaflight.avianutils.AvianApp.IsolatedTask;

public class DataHost {

	AvianRectangle r = new AvianRectangle();

	private static long setTime = 5 * 60 * 1000;
	private static long timeRemaining = setTime;

	private static AvianTextField header = new AvianTextField("Welcome to Ambient Clock");

	private static AvianTextField content = new AvianTextField("Right click to customize music, backgrounds, text, and more.");

	static AvianDataFile adf = new AvianDataFile(getDataDirectory() + "prefs.amb");

	public DataHost(String dataFilepath) {
		this();
		if (new File(dataFilepath).exists()) {
			adf = new AvianDataFile(dataFilepath);
			AvianApp.startIsolatedTask(new IsolatedTask() {

				@Override
				public void task() {
					loadData();
				}
			});
		}
	}

	public static String getDataDirectory() {
		return System.getProperty("user.home") + File.separator + "Ambient Clock" + File.separator + "Data" + File.separator;
	}

	public DataHost() {
		last = System.currentTimeMillis();

		header.setFont(AvianApp.VegurBold());
		header.restrictFocus(true);
		header.setEmptyMessage("Header");

		content.setFont(AvianApp.Vegur());
		content.restrictFocus(true);
		content.setEmptyMessage("Body");
	}

	long last;

	void logic() {
		if (ClockSettings.useTimer) {
			long now = System.currentTimeMillis();
			long diff = now - last;
			timeRemaining -= diff;

			last = now;
			if (timeRemaining < 0) {
				timeRemaining = 0;
			}
		} else {
			last = System.currentTimeMillis();
			timeRemaining = setTime;
		}

		PaperSettings.paperLogic();
		MusicSettings.musicLogic();
	}

	static AvianTextField getMainHeader() {
		return header;
	}

	static AvianTextField getMainContent() {
		return content;
	}

	private static Comparator<String> pathComparator = new Comparator<String>() {

		@Override
		public int compare(String s0, String s1) {

			return 0;
		}

	};

	public static Comparator<String> getPathComparator() {
		return pathComparator;
	}

	static void saveData() {
		ArrayList<String> music = MusicSettings.getMusic();
		ArrayList<String> papers = PaperSettings.getPapers();
		Collections.shuffle(music);
		Collections.shuffle(papers);
		adf.clear();
		int song = 0;
		for (song = 0; song < music.size(); song++) {
			adf.setElement("song" + song, music.get(song));
		}

		int paper = 0;
		for (paper = 0; paper < papers.size(); paper++) {
			adf.setElement("paper" + paper, papers.get(paper));
		}

		adf.setElement("header", header.toString());
		adf.setElement("content", content.toString());
		adf.setElement("volume", MusicSettings.s.getValue());
		adf.setElement("interval", PaperSettings.b.getSelectionName());
		adf.setElement("millis", setTime);
		adf.setElement("timer", ClockSettings.useTimer);
		adf.setElement("analog", ClockSettings.analog);
		adf.setElement("stopMusic", ClockSettings.stopMusic);
		adf.setElement("blur", PaperSettings.doesBlur());
		adf.setElement("dim", PaperSettings.doesDim());
		adf.flushElements();
		MusicSettings.sortMusic();
		PaperSettings.sortPapers();
	}

	void loadData() {
		List<String> music = new ArrayList<String>();
		List<String> papers = new ArrayList<String>();
		String[] keys = adf.keys();
		String[] values = adf.values();
		for (int i = 0; i < adf.keys().length; i++) {
			String v = values[i];
			if (keys[i].equals("header")) {
				header.setString(v);
			} else if (keys[i].equals("content")) {
				content.setString(v);
			} else if (keys[i].equals("volume")) {
				MusicSettings.s.setValue(Double.parseDouble(v));
			} else if (keys[i].equals("interval")) {
				PaperSettings.b.setSelectionByName(v);
			} else if (keys[i].equals("timer")) {
				ClockSettings.useTimer = Boolean.parseBoolean(v);
			} else if (keys[i].equals("analog")) {
				ClockSettings.analog = Boolean.parseBoolean(v);
			} else if (keys[i].equals("stopMusic")) {
				ClockSettings.stopMusic = Boolean.parseBoolean(v);
			} else if (keys[i].equals("millis")) {
				setTime = Long.parseLong(v);
				resetTimer();
			} else if (keys[i].equals("blur")) {
				PaperSettings.setShouldBlur(Boolean.parseBoolean(v));
			} else if (keys[i].equals("dim")) {
				PaperSettings.setShouldDim(Boolean.parseBoolean(v));
			} else if (keys[i].startsWith("paper")) {
				papers.add(v);
			} else if (keys[i].startsWith("song")) {
				music.add(v);
			}
		}
		MusicSettings.addMusic(music.toArray(new String[music.size()]));
		PaperSettings.addBackgrounds(papers.toArray(new String[papers.size()]));
		music = null;
		papers = null;
		keys = null;
		values = null;
	}

	public static long getSetTime() {
		return setTime;
	}

	static DecimalFormat df = new DecimalFormat("00");

	public static long getMillisRemaining() {
		return timeRemaining;
	}

	public static String getTimeRemainingAsString() {
		long h = (timeRemaining / 1000 / 60 / 60);
		long m = (timeRemaining / 1000 / 60) % 60;
		long s = (timeRemaining / 1000) % 60;
		if (h > 0) {
			return h + ":" + df.format(m) + ":" + df.format(s);
		}
		return m + ":" + df.format(s);
	}

	public static void resetTimer() {
		timeRemaining = setTime;
	}

	public static void addToSetTime(long t) {
		setTime += t;
		if (setTime < 5 * 1000)
			setTime = 5000;
	}

}
