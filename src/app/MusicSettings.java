
package app;

import java.io.File;
import java.io.IOException;
import java.util.*;

import app.FileExplorer.FileTypes;
import musicaflight.avianutils.*;
import musicaflight.avianutils.AvianApp.IsolatedTask;
import musicaflight.avianutils.AvianEase.Ease;

public class MusicSettings extends SettingsScreen {

	int selection;
	float time;
	boolean delete, hoverDel;
	float deleteX;
	static FileExplorer f = new FileExplorer(FileTypes.MUSIC) {
		@Override
		public void handleFiles(final String[] paths) {
			AvianApp.startIsolatedTask(new IsolatedTask() {
				@Override
				public void task() {
					Collections.shuffle(Arrays.asList(paths));
					close();
					addMusic(paths);
				}
			});
		}
	};

	private static Song song;
	static Song next;
	static ArrayList<String> queue = new ArrayList<String>();

	float highlight;
	float hAlpha;
	float mx, my;
	public static AvianSlider s = new AvianSlider(1f);
	static ArrayList<String> music = new ArrayList<String>();

	String deletedSong;
	int deletedIndex;
	float deletedSongX;

	public MusicSettings() {
		super("Music", Images.note);
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void scroll(int count) {
			}

			@Override
			public void release(int button, float x, float y) {
				delete = false;
				if (!isActive() || f.isOpen() || s.isDragging())
					return;
				if (hoverAdd) {
					f.open();
				}
				if (hoverDel) {
					deletedIndex = selection;
					String filename = music.get(selection).substring(music.get(selection).lastIndexOf(File.separator) + 1);
					deletedSong = filename;
					if (filename.lastIndexOf(".") != -1) {
						deletedSong = filename.substring(0, filename.lastIndexOf("."));
					}
					deletedSongX = 0;
					delete(selection);
				}
			}

			@Override
			public void press(int button, float x, float y) {
				if (!isActive() || f.isOpen() || music.isEmpty() || button != 0 || s.isDragging())
					return;
				selection = (int) ((y - 300f - getScroll()) / (ITEM_HEIGHT)) - 2;
				if (selection >= 0 && selection < music.size())
					delete = true;
			}

			@Override
			public void move(float x, float y) {
				mx = x;
				my = y;
				if (!isActive() || f.isOpen() || s.isDragging()) {
					hoverAdd = false;
					return;
				}
				if (AvianInput.isMouseButtonDown(0)) {
					int newSelection = (int) ((y - 300f - getScroll()) / (ITEM_HEIGHT)) - 2;
					if (selection != newSelection) {
						time = 0;
						if (newSelection >= 0 && newSelection < music.size())
							selection = newSelection;
					}
				}
				hoverAdd = hoversOverItem(y, 300 + getScroll(), 0);
				hoverDel = y > 300 + getScroll() && x < 25 + 8 && selection >= 0 && selection < music.size();
			}
		});
	}

	float addAlpha;
	boolean hoverAdd;
	private static AvianEase volume = new AvianEase(.3f, Ease.QUADRATIC);
	private static boolean play = true;
	private static float last;

	static void musicLogic() {
		if (play)
			volume.forward();
		else
			volume.rewind();

		if (song != null && song.getSong() != null) {
			if (last > song.getSong().getPosition()) {
				song.release();
				song = null;
			} else {
				if (song.getSong() != null) {
					song.getSong().setVolume((float) (volume.result() * s.getValue()));
				}
				if (volume.result() == 0) {
					if (song.isPlaying()) {
						song.pause();
					}
				} else {
					if (!song.isPlaying()) {
						song.play();
					}
				}
				last = song.getSong().getPosition();
			}
		} else if (song == null & next != null) {
			song = next;
			next = null;
			last = 0;
		}
		if (music.size() > 0 && next == null) {
			loadNextSong();
		}

	}

	static boolean loading;

	private static void loadNextSong() {

		if (next != null || loading)
			return;

		if (queue.isEmpty()) {
			if (music.isEmpty()) {
				return;
			}
			fillQueue();
		}

		if (queue.isEmpty())
			return;

		loading = true;

		AvianApp.startIsolatedTask(new IsolatedTask() {

			@Override
			public void task() {
				final String originalData = queue.remove(0);
				String newData = (next = new Song(originalData)).load();
				if (!originalData.equals(newData)) {
					for (int j = 0; j < music.size(); j++) {
						if (music.get(j).equals(originalData)) {
							music.set(j, newData);
							System.out.println("Original song data \"" + originalData + "\" has been changed to \"" + newData + "\"");
						}
					}
				}
				loading = false;
			}
		});
	}

	private static void fillQueue() {
		Collections.shuffle(music);
		queue.addAll(music);
		Collections.sort(music);
	}

	@Override
	public void contentLogic() {

		allowScrolling(isActive() && !f.isOpen() && !s.isDragging());

		s.restrictFocus(f.isOpen() || !isActive());
		s.logic();

		if (delete && !music.isEmpty()) {
			time += .01f;
			if (time > 1f) {
				time = 0f;
				MusicSettings.delete(selection);
				setContentHeight(300 + (music.size() + 2) * ITEM_HEIGHT);
				selection = (int) ((my - 300f - getFinalScroll()) / (ITEM_HEIGHT)) - 2;
				if (!(selection >= 0 && selection < music.size())) {
					delete = false;
				}
			}
		} else {
			time = 0;
			selection = (int) ((my - 300f - getScroll()) / (ITEM_HEIGHT)) - 2;
		}
		deletedSongX = AvianMath.glide(deletedSongX, -100, 7f);
		addAlpha = AvianMath.glide(addAlpha, hoverAdd ? 50 : 0, 10f);
		f.logic();

		if (hoverDel) {
			deleteX = AvianMath.glide(deleteX, 1, 10f);
		} else {
			deleteX = AvianMath.glide(deleteX, 0, 10f);

		}

		int items = music.size();/* 0;
									if (AvianInput.isKeyDown(AvianInput.KEY_Q)) {
									items = queue.size();
									} else {
									items = music.size();
									}*/
		setContentHeight(300 + (items + 2) * ITEM_HEIGHT);
	}

	static AvianRectangle r = new AvianRectangle();

	@Override
	public void contentRender(float yOffset, float alpha) {

		renderItemRectangle(300 + yOffset, 0, hoverAdd ? alpha : 0);
		renderItem("Add music", Images.plus, 0, 300 + yOffset, 0, alpha);

		if (selection >= 0 && selection < music.size()) {
			renderItemRectangle(300 + yOffset, selection + 2, alpha);
			int sel = selection;
			if (sel < 0)
				sel = 0;
			r.set(deleteX * AvianApp.getWidth() - AvianApp.getWidth(), 300f + ITEM_HEIGHT * (sel + 2) + yOffset, AvianApp.getWidth(), ITEM_HEIGHT);
			r.render(1, 0, 0, deleteX * 50 * alpha / 255f);
			Images.x.render(25 - 8, 300f + ITEM_HEIGHT / 2 - 8 + ITEM_HEIGHT * (sel + 2) + yOffset, (deleteX * .75f + .25f) * alpha);
		}

		renderItem(deletedSong, Images.note, -deletedSongX * 10f / 100f, 300 + yOffset + deletedSongX * 10 / 100, deletedIndex + 2, alpha * (deletedSongX + 100) / 100f);

		for (int i = 0; i < music.size(); i++) {
			float y = 300f + (i + 2) * ITEM_HEIGHT + yOffset;
			if (y < -ITEM_HEIGHT || y > AvianApp.getHeight()) {
				continue;
			}
			//			int ii = (i >= deletedIndex && deletedSongX > -99) ? i + 1 : i;
			String filename = music.get(i).substring(music.get(i).lastIndexOf(File.separator) + 1);
			String songName = filename;
			if (filename.lastIndexOf(".") != -1) {
				songName = filename.substring(0, filename.lastIndexOf("."));
			}

			if (i >= deletedIndex && deletedSongX > -99) {
				renderItem(songName, Images.note, 0, 300 + yOffset + ITEM_HEIGHT * ((deletedSongX + 100) / 100f), i + 2, alpha);
			} else {
				renderItem(songName, Images.note, 0, 300 + yOffset, i + 2, alpha);
			}
		}

		renderItem("Volume", null, 0, 200f + yOffset, 0, alpha);
		s.set(100, 200 + yOffset + ITEM_HEIGHT * 1.5f, 300);
		s.render(alpha);
		f.render();
	}

	public void onClose() {
		f.close();
		DataHost.saveData();
	}

	private static Comparator<String> c = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			o1 = o1.substring(o1.lastIndexOf(File.separator) + 1);
			if (o1.lastIndexOf(".") != -1) {
				o1 = o1.substring(0, o1.lastIndexOf("."));
			}

			o2 = o2.substring(o2.lastIndexOf(File.separator) + 1);
			if (o2.lastIndexOf(".") != -1) {
				o2 = o2.substring(0, o2.lastIndexOf("."));
			}
			return o1.compareTo(o2);
		}

	};

	public static void sortMusic() {
		Collections.sort(music, c);
	}

	@Override
	public void onOpen() {
		sortMusic();
	}

	public static ArrayList<String> getMusic() {
		return music;
	}

	public static String getNowPlaying() {
		return song != null ? song.getName().substring(0, song.getName().lastIndexOf(".")) : null;
	}

	public static boolean musicIsPlaying() {
		return song != null && song.isPlaying();
	}

	static void addMusic(String... strings) {
		for (int i = 0; i < strings.length; i++) {
			music.add(strings[i]);
		}
		Collections.sort(music);
	}

	static void delete(int index) {
		try {
			String deletionPath = new File(music.remove(index)).getCanonicalPath();

			if (song != null && deletionPath.equals(new File(song.getFilepath()).getCanonicalPath())) {
				song.release();
				song = null;
			}
			if (next != null && deletionPath.equals(new File(next.getFilepath()).getCanonicalPath())) {
				next.release();
				next = null;
			}

			if (music.size() == 0 && next != null) {
				next.release();
				next = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static AvianSound getCurrentSong() {
		return song != null ? song.getSong() : null;
	}

	public static void toggleMusic() {
		play = !play;
	}

	public static void pauseMusic() {
		play = false;
	}

	public static boolean isPlayingMusic() {
		return song != null && song.isPlaying();
	}

	public static float getVolume() {
		return volume.result();
	}

	public static boolean hasMusic() {
		return !music.isEmpty();
	}

	public static String getNext() {
		return next != null ? next.getName().substring(0, next.getName().lastIndexOf(".")) : null;
	}
}
