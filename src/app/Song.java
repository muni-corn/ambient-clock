
package app;

import java.io.File;

import musicaflight.avianutils.AvianSound;

public class Song implements Comparable<Song> {

	AvianSound record;
	boolean ready = false, loading = false;;
	private boolean destroyed = false;
	String name;
	String data;

	public Song(String data) {
		this.data = data;
		name = new File(data).getName();
	}

	public AvianSound getSong() {
		return record;
	}

	public String getName() {
		if (destroyed)
			return null;
		return name;
	}

	public int compareTo(Song s) {
		if (destroyed)
			return 1;
		if (s.getName() == null || getName() == null)
			return 0;
		return getName().compareTo(s.getName());
	}

	public String load() {
		loading = true;
		ready = false;

		if (!getName().substring(getName().lastIndexOf(".") + 1).equalsIgnoreCase("wav")) {
			AudioConverter.setUpConversion(data, DataHost.getDataDirectory() + "music/" + getName().substring(0, getName().lastIndexOf(".")) + ".wav");
			data = AudioConverter.convert();
		}

		record = new AvianSound(data);

		ready = true;
		loading = false;
		return data;
	}

	public Paper release() {
		if (destroyed)
			return null;
		if (record != null) {
			record = record.destroy();
		}
		ready = false;
		return null;
	}

	public void delete() {
		if (destroyed)
			return;

		release();
		//		TODO new File(bPath).delete();
		ready = false;
		destroyed = true;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public String getFilepath() {
		if (destroyed)
			return null;
		return data;
	}

	public boolean isLoaded() {
		if (destroyed)
			return false;
		return ready && !loading;
	}

	public boolean isPlaying() {
		return record != null && record.isPlaying();
	}

	public boolean isStopped() {
		return record != null && record.isStopped();
	}

	public void play() {
		if (record == null)
			return;
		record.play();
	}

	public void pause() {
		if (record == null)
			return;
		record.pause();
	}

}
