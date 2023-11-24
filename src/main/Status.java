package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Status {
	private final Random randomGenerator = new Random();
	private Boolean isLoaded;
	private String loadedName;
	private String loadedType;
	private Song loadedSong;
	private Playlist loadedPlaylist;
	private Podcast loadedPodcast;
	private Episode loadedEpisode;
	private Boolean paused;
	private Boolean shuffle;
	private String repeat;
	private int lastTimestamp;
	private int startTimestamp;
	private int duration;
	private int remainingTime;
	private final ArrayList<PodcastStatus> podcastMemory = new ArrayList<>();
	private final ArrayList<Integer> shuffleOrder = new ArrayList<>();


	public Status() {
		this.isLoaded = false;
		this.loadedSong = null;
		this.loadedPlaylist = null;
		this.loadedPodcast = null;
		this.loadedEpisode = null;
		this.paused = true;
		this.shuffle = false;
		this.repeat = "No Repeat";
		this.lastTimestamp = 0;
		this.startTimestamp = 0;
		this.duration = 0;
		this.remainingTime = 0;
	}

	public Boolean getLoaded() {
		return isLoaded;
	}

	public Boolean getPaused() {
		return paused;
	}

	public Boolean getShuffle() {
		return shuffle;
	}

	public String getRepeat() {
		return repeat;
	}

	public String getLoadedType() {
		return loadedType;
	}

	public String getLoadedName() {
		if (isLoaded) {
			return loadedName;
		}

		return "";
	}

	public int getRemainingTime() {
		if (isLoaded) {
			return remainingTime;
		}

		return 0;
	}

	public Playlist getLoadedPlaylist() {
		return loadedPlaylist;
	}

	public Song getLoadedSong() {
		return loadedSong;
	}

	public Podcast getLoadedPodcast() {
		return loadedPodcast;
	}

	public void reset() {
		isLoaded = false;
		loadedName = null;
		loadedType = null;
		loadedSong = null;
		loadedPlaylist = null;
		loadedEpisode = null;
		loadedPodcast = null;
		paused = true;
		shuffle = false;
		repeat = "No Repeat";
		startTimestamp = 0;
		duration = 0;
		remainingTime = 0;
	}

	private void updatePlayerSong(int timestamp) {
		// Playback has not ended yet
		if (duration < loadedSong.getInput().getDuration()) {
			remainingTime = loadedSong.getInput().getDuration() - duration;
			startTimestamp = timestamp - duration;
			return;
		}
		// Original playback ended, check and update for repetitions
		duration -= loadedSong.getInput().getDuration();
		switch (repeat) {
			case "No Repeat":
				reset();
				break;
			case "Repeat Once":
				remainingTime = loadedSong.getInput().getDuration();
				repeat = "No Repeat";
				updatePlayerSong(timestamp);
				break;
			case "Repeat Infinite":
				remainingTime = loadedSong.getInput().getDuration();
				updatePlayerSong(timestamp);
				break;
			default:
				break;
		}
	}

	public void updatePlayerPlaylist(int timestamp) {
		// Playback has not ended yet
		if (duration < loadedSong.getInput().getDuration()) {
			remainingTime = loadedSong.getInput().getDuration() - duration;
			startTimestamp = timestamp - duration;
			loadedName = loadedSong.getInput().getName();
			return;
		}

		int index;
		duration -= loadedSong.getInput().getDuration();
		switch (repeat) {
			case "No Repeat":
				// Find next song
				index = loadedPlaylist.getSongs().indexOf(loadedSong);
				// Check for order, attempt to load next song (if exists)
				if (shuffle) {
					// Shuffle order
					int shuffleIndex = shuffleOrder.indexOf(index);
					if (shuffleIndex + 1 == loadedPlaylist.getSongs().size()) {
						reset();
					} else {
						loadedSong = loadedPlaylist.getSongs().get(shuffleOrder.get(shuffleIndex + 1));
						loadedName = loadedSong.getInput().getName();
						remainingTime = loadedSong.getInput().getDuration();
						updatePlayerPlaylist(timestamp);
					}
				} else {
					// Normal order
					if (index + 1 == loadedPlaylist.getSongs().size()) {
						reset();
					} else {
						loadedSong = loadedPlaylist.getSongs().get(index + 1);
						loadedName = loadedSong.getInput().getName();
						remainingTime = loadedSong.getInput().getDuration();
						updatePlayerPlaylist(timestamp);
					}
				}
				break;
			case "Repeat Current Song":
				remainingTime = loadedSong.getInput().getDuration();
				updatePlayerPlaylist(timestamp);
				break;
			case "Repeat All":
				// Find next song
				index = loadedPlaylist.getSongs().indexOf(loadedSong);
				// Check for order, attempt to load next song
				if (shuffle) {
					// Shuffle order
					int shuffleIndex = shuffleOrder.indexOf(index);
					if (shuffleIndex + 1 == loadedPlaylist.getSongs().size()) {
						loadedSong = loadedPlaylist.getSongs().get(shuffleOrder.get(0));
					} else {
						loadedSong = loadedPlaylist.getSongs().get(shuffleOrder.get(shuffleIndex + 1));
					}
				} else {
					// Normal order
					if (index + 1 == loadedPlaylist.getSongs().size()) {
						loadedSong = loadedPlaylist.getSongs().get(0);
					} else {
						loadedSong = loadedPlaylist.getSongs().get(index + 1);
					}
				}
				loadedName = loadedSong.getInput().getName();
				remainingTime = loadedSong.getInput().getDuration();
				updatePlayerPlaylist(timestamp);
				break;
			default:
				break;
		}
	}

	public void updatePlayerPodcast(int timestamp) {
		PodcastStatus currentPodcast = null;
		for (PodcastStatus podcastStatus : podcastMemory) {
			if (podcastStatus.getPodcast().getName().compareTo(loadedPodcast.getName()) == 0) {
				currentPodcast = podcastStatus;
			}
		}
		if (currentPodcast == null) {
			return;
		}
		// Playback has not ended yet
		if (duration < loadedEpisode.getInput().getDuration()) {
			remainingTime = loadedEpisode.getInput().getDuration() - duration;
			startTimestamp = timestamp - duration;
			currentPodcast.setTimeWatched(duration);
			loadedName = loadedEpisode.getInput().getName();
			return;
		}

		// Playback ended, find next
		duration -= loadedEpisode.getInput().getDuration();
		switch (repeat) {
			case "No Repeat":
				int index = currentPodcast.getPodcast().getEpisodes().indexOf(loadedEpisode);

				if (index + 1 == currentPodcast.getPodcast().getEpisodes().size()) {
					currentPodcast.setLastEpisode(currentPodcast.getPodcast().getEpisodes().get(0));
					currentPodcast.setTimeWatched(0);
					reset();
				} else {
					currentPodcast.setLastEpisode(currentPodcast.getPodcast().getEpisodes().get(index + 1));
					currentPodcast.setTimeWatched(0);
					remainingTime = currentPodcast.getLastEpisode().getInput().getDuration();
					loadedEpisode = currentPodcast.getLastEpisode();
					loadedName = loadedEpisode.getInput().getName();
					updatePlayerPodcast(timestamp);
				}
				break;
			case "Repeat Once":
				currentPodcast.setTimeWatched(0);
				remainingTime = currentPodcast.getLastEpisode().getInput().getDuration();
				repeat = "No Repeat";
				updatePlayerPodcast(timestamp);
				break;
			case "Repeat Infinite":
				currentPodcast.setTimeWatched(0);
				remainingTime = currentPodcast.getLastEpisode().getInput().getDuration();
				updatePlayerPodcast(timestamp);
				break;
			default:
				break;
		}
	}

	public void updatePlayer(int timestamp) {
		// Check if player was actually playing
		if (!paused && isLoaded) {
			// Before doing anything, update duration
			duration = duration + (timestamp - lastTimestamp);
			// Handle different cases
			if (loadedPodcast == null) {
				if (loadedPlaylist == null) {
					// Loaded entity is of type Song
					updatePlayerSong(timestamp);
				} else {
					// Loaded entity is of type Playlist
					updatePlayerPlaylist(timestamp);
				}
			} else {
				// Loaded entity is of type Podcast
				updatePlayerPodcast(timestamp);
			}
		}
		// After processing, player should be chronologically here
		lastTimestamp = timestamp; // Code should run after time update
	}

	public void updateShuffle() {
		shuffle = false;
	}

	public void updateShuffle(int seed) {
		shuffle = true;
		randomGenerator.setSeed(seed);
		shuffleOrder.clear();

		for (int i = 0; i < loadedPlaylist.getSongs().size(); i++) {
			shuffleOrder.add(i);
		}

		Collections.shuffle(shuffleOrder, randomGenerator);
	}

	public void updateRepeat() {
		switch (repeat) {
			case "No Repeat":
				if (loadedType.compareTo("playlist") == 0) {
					repeat = "Repeat All";
				} else {
					repeat = "Repeat Once";
				}
				break;
			case "Repeat All":
				repeat = "Repeat Current Song";
				break;
			case "Repeat Once":
				repeat = "Repeat Infinite";
				break;
			case "Repeat Current Song":
			case "Repeat Infinite":
				repeat = "No Repeat";
				break;
			default:
				break;
		}
	}

	public void loadSource(Database database, int timestamp, String name, String type) {
		// Assume playback was updated; refresh, then load selection
		reset();
		// Start playback after loading
		loadedType = type;
		isLoaded = true;
		paused = false;
		duration = 0;
		startTimestamp = timestamp;
		// Handle different types
		switch (type) {
			case "song":
				loadedSong = database.findMatchSong(name);
				remainingTime = loadedSong.getInput().getDuration();
				loadedName = loadedSong.getInput().getName();
				break;
			case "playlist":
				loadedPlaylist = database.findMatchPlaylist(name);
				loadedSong = loadedPlaylist.getSongs().get(0);
				loadedName = loadedSong.getInput().getName();
				remainingTime = loadedSong.getInput().getDuration();
				break;
			case "podcast":
				loadedPodcast = database.findMatchPodcast(name);
				PodcastStatus currentStatus = null;

				for (PodcastStatus podcastStatus : podcastMemory) {
					if (podcastStatus.getPodcast().getName().compareTo(name) == 0) {
						currentStatus = podcastStatus;
					}
				}

				if (currentStatus == null) {
					currentStatus = new PodcastStatus(loadedPodcast);
					loadedEpisode = currentStatus.getLastEpisode();
					remainingTime = loadedEpisode.getInput().getDuration();
					podcastMemory.add(currentStatus);
				} else {
					loadedEpisode = currentStatus.getLastEpisode();
					remainingTime = loadedEpisode.getInput().getDuration() -
						currentStatus.getTimeWatched();
					duration = currentStatus.getTimeWatched();
				}
				loadedName = loadedEpisode.getInput().getName();
				break;
			default:
				break;
		}
	}

	public void playPlayer(int timestamp) {
		// Assume playback was updated before method call
		paused = false;
		startTimestamp = timestamp;
	}

	public void pausePlayer() {
		// Assume playback was updated before method call
		paused = true;
		startTimestamp = 0;
	}

	public void forwardPlayer(int timestamp) {
		// Assume playback was updated and loaded type is podcast
		Episode currentEpisode = loadedEpisode;
		duration += 90;

		// Update call when timestamp == lastTimestamp
		updatePlayer(timestamp);

		if (!isLoaded) {
			return;
		}

		if (currentEpisode.getInput().getName().compareTo(loadedName) != 0) {
			// Forwarding past the end of the episode should load the next
			duration = 0;
			remainingTime = loadedEpisode.getInput().getDuration();
			for (PodcastStatus podcastStatus : podcastMemory) {
				if (podcastStatus.getPodcast().getName().compareTo(loadedPodcast.getName()) == 0) {
					podcastStatus.setTimeWatched(0);
					return;
				}
			}
		}
	}

	public void backwardPlayer(int timestamp) {
		// Assume playback was updated and loaded type is podcast
		Episode currentEpisode = loadedEpisode;
		duration -= 90;
		if (duration < 0) {
			duration = 0;
		}

		// Update call when timestamp == lastTimestamp
		updatePlayer(timestamp);
	}

	public void nextPlayer(int timestamp) {
		// Assume playback was updated
		duration += remainingTime;

		updatePlayer(timestamp);
	}

	public void prevPlayer(int timestamp) {
		// Assume playback was updated
		int index;
		if (duration == 0 && loadedPlaylist != null &&
			loadedPlaylist.getSongs().indexOf(loadedSong) > 0) {
			// Command loads previous song
			index = loadedPlaylist.getSongs().indexOf(loadedSong);
			loadedSong = loadedPlaylist.getSongs().get(index - 1);
		} else if (duration == 0 && loadedPodcast != null &&
			loadedPodcast.getEpisodes().indexOf(loadedEpisode) > 0) {
			// Command loads previous episode
			index = loadedPodcast.getEpisodes().indexOf(loadedEpisode);
			loadedEpisode = loadedPodcast.getEpisodes().get(index - 1);
		} else {
			// Command restarts current selection
			duration = 0;
		}

		updatePlayer(timestamp);
	}
}
