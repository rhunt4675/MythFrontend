package ui;

import data.Recording;
import trakt.TraktManager;
import utils.AppProperties;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExternalPlayer {
	private static final Logger LOGGER = Logger.getLogger(ExternalPlayer.class.getName());
	
	private boolean _isActive;
	private Recording _recording;
	private Process _process;
	private Thread _watchdogThread;
	
	public boolean isActive() { return _isActive; }
	
	public void play(Recording r) throws IOException {
		// Only One Video Stream at any Instance
		if (isActive())
			return;
		_recording = r;
		
		// Scrobble Video Start
		TraktManager.notifyPlay(_recording);

		// Start Other Video Players
		final String url = r.get_playback_url();
		if (AppProperties.getPlayer().isEmpty()) AppProperties.displayPlayerPropertiesWindow();
		_process = new ProcessBuilder(AppProperties.getPlayer(), url).start();
		
		// Start Process WatchDog
		_watchdogThread = new Thread(_watchdogRunnable);
		_watchdogThread.start();
		_isActive = true;
	}
	
	private void stop(float progress) {
		// Verify that the User Wants Recording Watched
		boolean markWatched = true;
		if (progress < 50f) {
			int result = JOptionPane.showConfirmDialog(null, String.format("It appears that you watched %.2f%% of " 
				+ "%s - %s. Would you like to mark it as watched?", progress, _recording.get_title(), 
				_recording.get_subtitle()), "Mark as Watched", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			markWatched = (result == JOptionPane.YES_OPTION);
		}
		
		// Mark Recording as Watched
		TraktManager.notifyStop(_recording, (markWatched ? 100f : progress));
		if (markWatched) {
			try {
				_recording.mark_watched(true);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
		_isActive = false;
	}
	
	private Runnable _watchdogRunnable = new Runnable() {
		@Override public void run() {			
			try {
				// Calculate Program Length
				long programLength = _recording.get_endts().toEpochSecond() - _recording.get_startts().toEpochSecond();
				long watchedLength = 0L;
				long lastTimestamp = System.currentTimeMillis() / 1000;
				Float progress = null;
				
				// Wait for the Process to Terminate
				while (_process.isAlive()) {
					boolean finished = _process.waitFor(500, TimeUnit.MILLISECONDS);
					if (finished) break;
					
					// Estimate (%) of Program Watched
					long newTimestamp = System.currentTimeMillis() / 1000;
					watchedLength += (newTimestamp - lastTimestamp);
					progress = (100f * watchedLength) / (programLength);
					lastTimestamp = newTimestamp;
				}
				
				// Cleanup the Player
				stop(progress != null ? progress : 100);
				
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	};
}
