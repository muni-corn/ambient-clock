
package app;

import java.io.*;
import java.net.URISyntaxException;

import musicaflight.avianutils.AvianApp;
import musicaflight.avianutils.AvianFileDownloader;
import musicaflight.avianutils.AvianApp.IsolatedTask;

public class UpdateChecker {

	static AvianFileDownloader info = new AvianFileDownloader("https://dl.dropboxusercontent.com/u/100480748/Ambient%20Clock/AmbientClockUpdateInfo.txt");
	static AvianFileDownloader installJar;
	static AvianFileDownloader updateJar;
	static boolean updateAvailable;
	static boolean checking;
	static boolean downloading;
	static boolean error;
	static String version;
	static String infoString;
	static String updateLink;
	static String installLink;

	public static void checkForUpdate() {
		if (downloading)
			return;
		AvianApp.startIsolatedTask(new IsolatedTask() {

			@Override
			public void task() {
				if (checking || downloading || readyToInstall())
					return;
				checking = true;
				error = false;
				e = null;
				updateAvailable = false;
				try {
					info.retrieveAll();
					String[] lines = info.getFileLines();
					version = "";
					infoString = "";
					updateLink = "";
					installLink = "";
					for (int i = 0; i < lines.length; i++) {
						String line = lines[i];
						int j = i + 1;
						if (line.startsWith("<")) {
							while (j < lines.length && !lines[j].startsWith("<")) {
								if (line.startsWith("<version>")) {
									version = (version + lines[j] + "\n");
								} else if (line.startsWith("<info>")) {
									infoString = (infoString + lines[j] + "\n");
								} else if (line.startsWith("<updatelink>")) {
									updateLink = (updateLink + lines[j] + "\n");
								} else if (line.startsWith("<installlink>")) {
									installLink = (installLink + lines[j] + "\n");
								}
								j++;
							}
						}
					}
					version = version.trim();
					infoString = infoString.trim();
					updateLink = updateLink.trim();
					installLink = installLink.trim();
					updateAvailable = !version.equals(AvianApp.getVersion());
					checking = false;
				} catch (IOException ex) {
					UpdateChecker.e = ex;
					ex.printStackTrace();
					error = true;
				}
				//				if (updateAvailable)
				//					requestUpdate();
			}

		});
	}

	static Exception e;

	public static String getProblem() {
		if (error) {
			return e.toString();
		}
		return "";
	}

	public static String getResults() {
		if (readyToInstall())
			return AvianApp.getAppName() + " " + version + " is ready to be installed.";
		if (downloading)
			return "Downloading " + AvianApp.getAppName() + " " + version + ".";
		if (error)
			return "An error occured...";
		if (checking)
			return "Checking for updates...";
		if (updateAvailable)
			return AvianApp.getAppName() + " " + version + " is available.";
		return "Ambient Clock is up to date.";
	}

	static String getJarPath() {
		try {
			return new File(MainScreen.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getCanonicalPath();
		} catch (URISyntaxException | IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void requestDownload() {
		//		checkForUpdate();
		if (readyToInstall() || !updateAvailable || checking || error || downloading)
			return;
		AvianApp.startIsolatedTask(new IsolatedTask() {

			@Override
			public void task() {
				downloading = true;
				updateJar = new AvianFileDownloader(DataHost.getDataDirectory() + "tmp/AmbientClockUpdate.jar", updateLink);
				installJar = new AvianFileDownloader(DataHost.getDataDirectory() + "tmp/install.jar", installLink);
				try {
					updateJar.retrieveAll();
					installJar.retrieveAll();
				} catch (IOException ex) {
					ex.printStackTrace();
					UpdateChecker.e = ex;
					error = true;
				}

				try {
					BufferedWriter w = new BufferedWriter(new FileWriter(new File(DataHost.getDataDirectory() + "tmp/jarfile.updateconfig")));
					w.write(getJarPath());
					w.newLine();
					w.write(new File(DataHost.getDataDirectory() + "tmp/AmbientClockUpdate.jar").getCanonicalPath());
					w.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				downloading = false;
			}

		});

	}

	public static void requestInstall() {
		if (!readyToInstall() || !updateAvailable || checking || error || downloading)
			return;
		try {
			Runtime.getRuntime().exec("java -jar \"" + new File(DataHost.getDataDirectory() + "tmp/install.jar").getCanonicalPath() + "\"");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.exit(0);

	}

	public static String getInformation() {
		return infoString;
	}

	public static double getProgress() {
		if (!downloading || updateJar.getOnlineFileSize() <= 0)
			return 0;
		return (double) (updateJar.getBytesDownloaded()) / (double) (updateJar.getOnlineFileSize());
	}

	public static boolean isDownloading() {
		return downloading;
	}

	public static boolean updateIsAvailable() {
		return updateAvailable;
	}

	public static boolean readyToInstall() {
		if (updateJar == null) {
			return false;
		}
		return updateJar.isFinished() && installJar.isFinished();
	}

	public static String getInfo() {
		if (infoString == null)
			return "";
		return infoString;
	}

}
