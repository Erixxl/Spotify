package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;

import java.util.ArrayList;

public class Command {
	private String command;
	private String username;
	private User user;
	private String type;
	private Filter filters;
	private Integer timestamp;
	private ArrayList<String> stringResults;
	private ArrayList<Playlist> playlistResults;
	private Integer itemNumber;
	private String playlistName;
	private Integer playlistId;
	private Integer seed;
	private String message;

	public Command() {
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Filter getFilters() {
		return filters;
	}

	public void setFilters(Filter filters) {
		this.filters = filters;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public ArrayList<String> getStringResults() {
		return stringResults;
	}

	public void setStringResults(ArrayList<String> stringResults) {
		this.stringResults = new ArrayList<>(stringResults);
	}

	public ArrayList<Playlist> getPlaylistResults() {
		return playlistResults;
	}

	public void setPlaylistResults(ArrayList<Playlist> playlistResults) {
		this.playlistResults = new ArrayList<>(playlistResults);
	}

	public Integer getSeed() {
		return seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public Integer getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(Integer itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getPlaylistName() {
		return playlistName;
	}

	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}

	public Integer getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(Integer playlistId) {
		this.playlistId = playlistId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void executeCommand(Database database) {
		// Link username to corresponding User entity for easier handling
		if (username != null) {
			user = findRelatedUser(database);
		}
		// executeCommand() calls appropriate method based on command type
		switch (command) {
			case "search":
				searchCommand(database);
				break;
			case "select":
				selectCommand();
				break;
			case "load":
				loadCommand(database);
				break;
			case "playPause":
				playPauseCommand();
				break;
			case "repeat":
				repeatCommand();
				break;
			case "shuffle":
				shuffleCommand();
				break;
			case "forward":
				forwardCommand();
				break;
			case "backward":
				backwardCommand();
				break;
			case "like":
				likeCommand();
				break;
			case "next":
				nextCommand();
				break;
			case "prev":
				prevCommand();
				break;
			case "addRemoveInPlaylist":
				addRemoveInPlaylistCommand(database);
				break;
			case "status":
				statusCommand();
				break;
			case "createPlaylist":
				createPlaylistCommand(database);
				break;
			case "switchVisibility":
				switchVisibilityCommand(database);
				break;
			case "follow":
				followCommand(database);
				break;
			case "getTop5Songs":
				getTop5SongsCommand(database);
				break;
			case "getTop5Playlists":
				getTop5PlaylistsCommand(database);
				break;
			case "showPlaylists":
			case "showPreferredSongs":
			default:
				// Do nothing -- command just prints an output
				break;
		}
	}

	private User findRelatedUser(Database database) {
		if (database == null || this.username == null || this.username.isEmpty()) {
			System.out.println("Fatal error while looking for user");
			return null;
		}

		return database.findMatchUser(username);
	}

	public ObjectNode commandOutput(ObjectMapper objectMapper, Database database) {
		ObjectNode outputObject = objectMapper.createObjectNode();
		ArrayNode arrayField = objectMapper.createArrayNode();

		outputObject.put("command", command);
		outputObject.put("user", username);
		outputObject.put("timestamp", timestamp);

		switch (command) {
			case "search":
				outputObject.put("message", message);

				for (String result : stringResults) {
					arrayField.add(result);
				}
				outputObject.set("results", arrayField);
				break;
			case "select":
			case "load":
			case "playPause":
			case "createPlaylist":
			case "switchVisibility":
			case "addRemoveInPlaylist":
			case "like":
			case "repeat":
			case "shuffle":
			case "forward":
			case "backward":
			case "next":
			case "prev":
			case "follow":
				outputObject.put("message", message);
				break;
			case "status":
				ObjectNode statusObject = objectMapper.createObjectNode();

				statusObject.put("name", user.getPlayerStatus().getLoadedName());
				statusObject.put("remainedTime", user.getPlayerStatus().getRemainingTime());
				statusObject.put("repeat", user.getPlayerStatus().getRepeat());
				statusObject.put("shuffle", user.getPlayerStatus().getShuffle());
				statusObject.put("paused", user.getPlayerStatus().getPaused());

				outputObject.set("stats", statusObject);
				break;
			case "showPlaylists":
				ArrayNode playlistArray = objectMapper.createArrayNode();

				for (Playlist playlist : database.getPlaylists()) {
					if (playlist.getOwner().compareTo(username) == 0) {
						ObjectNode playlistNode = objectMapper.createObjectNode();
						ArrayNode songArray = objectMapper.createArrayNode();

						for (Song song : playlist.getSongs()) {
							songArray.add(song.getInput().getName());
						}

						playlistNode.put("name", playlist.getPlaylistName());
						playlistNode.set("songs", songArray);
						playlistNode.put("visibility", playlist.getVisibility());
						playlistNode.put("followers", playlist.getFollowerCount());

						playlistArray.add(playlistNode);
					}
				}

				outputObject.set("result", playlistArray);
				break;
			case "showPreferredSongs":
				ArrayNode songArray = objectMapper.createArrayNode();

				for (String songName : user.getLikedSongs()) {
					songArray.add(songName);
				}

				outputObject.set("result", songArray);
				break;
			case "getTop5Songs":
				ArrayNode bestSongs = objectMapper.createArrayNode();

				for (Song song : database.getTop5Songs()) {
					bestSongs.add(song.getInput().getName());
				}

				outputObject.remove("user");
				outputObject.set("result", bestSongs);
				break;
			case "getTop5Playlists":
				ArrayNode bestPlaylists = objectMapper.createArrayNode();

				for (Playlist playlist : database.getTop5Playlists()) {
					bestPlaylists.add(playlist.getPlaylistName());
				}

				outputObject.remove("user");
				outputObject.set("result", bestPlaylists);
				break;
			default:
				break;
		}

		return outputObject;
	}

	private void searchCommand(Database database) {
		// Unload player
		user.getPlayerStatus().updatePlayer(timestamp);
		user.getPlayerStatus().reset();
		// Different method calls based on the type of the search
		switch (type) {
			case "song":
				searchSongCommand(database.getSongs());
				break;
			case "podcast":
				searchPodcastCommand(database.getPodcasts());
				break;
			case "playlist":
				searchPlaylistCommand(database.getPlaylists());
				break;
			default:
				return;
		}
		// Saving the results of the search
		if (user != null) {
			user.setLastSearchType(type);
			user.setLastSearchResults(stringResults);
			user.setRecentSearch(true);
		}
	}

	private void searchSongCommand(ArrayList<Song> songs) {
		ArrayList<String> searchResult = new ArrayList<>();
		int resultCount = 0;

		for (Song song : songs) {
			if (filterMatch(song.getInput())) {
				searchResult.add(song.getInput().getName());
				resultCount++;
			}
		}

		if (resultCount > 5) {
			resultCount = 5;
			while (searchResult.size() > 5) {
				searchResult.remove(searchResult.size() - 1);
			}
		}

		message = "Search returned " + resultCount + " results";
		stringResults = searchResult;
	}

	private void searchPodcastCommand(ArrayList<Podcast> podcasts) {
		ArrayList<String> searchResult = new ArrayList<>();
		int resultCount = 0;

		for (Podcast podcast : podcasts) {
			if (filterMatch(podcast)) {
				searchResult.add(podcast.getName());
				resultCount++;
			}
		}

		if (resultCount > 5) {
			resultCount = 5;
			while (searchResult.size() > 5) {
				searchResult.remove(searchResult.size() - 1);
			}
		}

		message = "Search returned " + resultCount + " results";
		stringResults = searchResult;
	}

	private void searchPlaylistCommand(ArrayList<Playlist> playlists) {
		ArrayList<String> searchResult = new ArrayList<>();
		int resultCount = 0;

		for (Playlist playlist : playlists) {
			if (filterMatch(playlist)) {
				searchResult.add(playlist.getPlaylistName());
				resultCount++;
			}
		}

		if (resultCount > 5) {
			resultCount = 5;
			while (searchResult.size() > 5) {
				searchResult.remove(searchResult.size() - 1);
			}
		}

		message = "Search returned " + resultCount + " results";
		stringResults = searchResult;
	}

	private boolean filterMatch(SongInput song) {
		return ((filters.getName() == null || song.getName().
			startsWith(filters.getName())) &&
			(filters.getAlbum() == null || song.getAlbum()
				.compareToIgnoreCase(filters.getAlbum()) == 0) &&
			(filters.getTags() == null || filters.matchTags(song.getTags())) &&
			(filters.getLyrics() == null || song.getLyrics().toLowerCase()
				.contains(filters.getLyrics().toLowerCase())) &&
			(filters.getGenre() == null || song.getGenre()
				.compareToIgnoreCase(filters.getGenre()) == 0) &&
			(filters.getReleaseYear() == null || filters
				.checkYearPeriod(song.getReleaseYear())) &&
			(filters.getArtist() == null || song.getArtist()
				.compareToIgnoreCase(filters.getArtist()) == 0));
	}

	private boolean filterMatch(Podcast podcast) {
		return ((filters.getName() == null || podcast.getName().startsWith(filters.getName())) &&
			(filters.getOwner() == null || podcast.getOwner().compareTo(filters.getOwner()) == 0));
	}

	private boolean filterMatch(Playlist playlist) {
		return ((filters.getName() == null || playlist.getPlaylistName().startsWith(filters.getName())) &&
			(filters.getOwner() == null || playlist.getOwner().compareTo(filters.getOwner()) == 0) &&
			(playlist.getVisibility().compareTo("public") == 0 ||
				(playlist.getVisibility().compareTo("private") == 0 && username.compareTo(playlist.getOwner()) == 0)));
	}

	private void selectCommand() {
		if (user != null) {
			// Clear previous selection
			if (user.getCurrentSelection() != null && !user.isRecentSearch()) {
				user.setCurrentSelection(null);
				user.setCurrentSelectionType(null);
				message = "Please conduct a search before making a selection.";
				return;
			}

			if (user.getLastSearchResults() == null) {
				message = "Please conduct a search before making a selection.";
			} else if (user.getLastSearchResults().size() < itemNumber) {
				message = "The selected ID is too high.";
			} else {
				message = "Successfully selected " + user.getLastSearchResults().get(itemNumber - 1) + ".";
				user.setCurrentSelection(user.getLastSearchResults().get(itemNumber - 1));
				user.setCurrentSelectionType(user.getLastSearchType());
			}

			user.setRecentSearch(false);
		}
	}

	private void loadCommand(Database database) {
		if (user.getCurrentSelection() == null || user.getCurrentSelection().isEmpty()) {
			message = "Please select a source before attempting to load.";
			return;
		}
		if (user.getCurrentSelectionType().compareTo("playlist") == 0) {
			Playlist selection = database.findMatchPlaylist(user.getCurrentSelection());
			if (selection.getSongs().isEmpty()) {
				message = "You can't load an empty audio collection!";
				return;
			}
		}
		message = "Playback loaded successfully.";
		user.loadCommand(database, timestamp);
	}

	private void playPauseCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (user.getPlayerStatus().getLoaded()) {
			if (user.getPlayerStatus().getPaused()) {
				// Was paused, switch to play
				user.playCommand(timestamp);
				message = "Playback resumed successfully.";
			} else {
				// Was playing, switch to paused
				user.pauseCommand();
				message = "Playback paused successfully.";
			}
		} else {
			message = "Please load a source before attempting to pause or resume playback.";
		}
	}

	private void statusCommand() {
		// Just update and return stats in output method
		user.getPlayerStatus().updatePlayer(timestamp);
	}

	private void createPlaylistCommand(Database database) {
		if (playlistName == null || playlistName.isEmpty()) {
			System.out.println("No playlist from input");
			return;
		}

		Playlist newPlaylist = database.findMatchPlaylist(playlistName);

		if (newPlaylist == null) {
			database.addPlaylist(new Playlist(username, playlistName));
			user.incrementPlaylist();
			message = "Playlist created successfully.";
		} else {
			message = "A playlist with the same name already exists.";
		}
	}

	private void switchVisibilityCommand(Database database) {
		if (playlistId == null) {
			System.out.println("Error - no ID provided");
			return;
		}

		Playlist playlist = database.getPlaylistMadeByUser(user, playlistId);

		if (playlist == null) {
			message = "The specified playlist ID is too high.";
			return;
		}

		playlist.switchVisibility();
		message = "Visibility status updated successfully to " + playlist.getVisibility() + ".";
	}

	private void addRemoveInPlaylistCommand(Database database) {
		// Update before attempting to load
		user.getPlayerStatus().updatePlayer(timestamp);
		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before adding to or removing from the playlist.";
			return;
		}

		// Attempt to load
		if (user.getPlayerStatus().getLoadedType().compareTo("song") != 0) {
			message = "The loaded source is not a song.";
			return;
		}

		Playlist playlist = database.getPlaylistMadeByUser(user, playlistId);

		if (playlist == null) {
			message = "The specified playlist does not exist.";
			return;
		}

		Song selection = user.getPlayerStatus().getLoadedSong();

		if (!playlist.getSongs().contains(selection)) {
			playlist.addSong(selection);
			message = "Successfully added to playlist.";
		} else {
			playlist.removeSong(selection);
			message = "Successfully removed from playlist.";
		}
	}

	private void likeCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before liking or unliking.";
			return;
		}

		if (user.getCurrentSelectionType().compareTo("podcast") == 0) {
			message = "Loaded source is not a song.";
			return;
		}

		Song loadedSong = user.getPlayerStatus().getLoadedSong();
		if (user.getLikedSongs().contains(loadedSong.getInput().getName())) {
			user.removeLikedSong(loadedSong.getInput().getName());
			loadedSong.removeLike();
			message = "Unlike registered successfully.";
		} else {
			user.addLikedSong(loadedSong.getInput().getName());
			loadedSong.addLike();
			message = "Like registered successfully.";
		}
	}

	private void repeatCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (user.getPlayerStatus().getLoaded()) {
			user.getPlayerStatus().updateRepeat();
			message = "Repeat mode changed to " +
				user.getPlayerStatus().getRepeat().toLowerCase() + ".";
		} else {
			message = "Please load a source before setting the repeat status.";
		}
	}

	private void shuffleCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before using the shuffle function.";
			return;
		}

		if (user.getPlayerStatus().getLoadedPlaylist() == null) {
			message = "The loaded source is not a playlist.";
			return;
		}

		if (seed != null) {
			user.getPlayerStatus().updateShuffle(seed);
			message = "Shuffle function activated successfully.";
		} else {
			user.getPlayerStatus().updateShuffle();
			message = "Shuffle function deactivated successfully.";
		}
	}

	private void forwardCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before attempting to forward.";
			return;
		}

		if (user.getPlayerStatus().getLoadedPodcast() == null) {
			message = "The loaded source is not a podcast.";
			return;
		}

		message = "Skipped forward successfully.";
		user.getPlayerStatus().forwardPlayer(timestamp);
	}

	private void backwardCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before rewinding.";
			return;
		}

		if (user.getPlayerStatus().getLoadedPodcast() == null) {
			message = "The loaded source is not a podcast.";
			return;
		}

		message = "Rewound successfully.";
		user.getPlayerStatus().backwardPlayer(timestamp);
	}

	private void nextCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before skipping to the next track.";
			return;
		}

		user.getPlayerStatus().nextPlayer(timestamp);

		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before skipping to the next track.";
		} else {
			message = "Skipped to next track successfully. The current track is "
				+ user.getPlayerStatus().getLoadedName() + ".";
		}
	}

	private void prevCommand() {
		user.getPlayerStatus().updatePlayer(timestamp);

		if (!user.getPlayerStatus().getLoaded()) {
			message = "Please load a source before returning to the previous track.";
			return;
		}

		user.getPlayerStatus().prevPlayer(timestamp);
		message = "Returned to previous track successfully. The current track is "
			+ user.getPlayerStatus().getLoadedName() + ".";
	}

	private void followCommand(Database database) {
		if (user.getCurrentSelection() == null) {
			message = "Please select a source before following or unfollowing.";
			return;
		}

		if (user.getCurrentSelectionType().compareTo("playlist") != 0) {
			message = "The selected source is not a playlist.";
			return;
		}

		if (database.findMatchPlaylist(user.getCurrentSelection())
			.getOwner().compareTo(username) == 0) {
			message = "You cannot follow or unfollow your own playlist.";
			return;
		}

		if (user.getFollowedPlaylists().contains(user.getCurrentSelection())) {
			user.removeFollowedPlaylist(user.getCurrentSelection());
			database.findMatchPlaylist(user.getCurrentSelection()).removeFollower();
			message = "Playlist unfollowed successfully.";
		} else {
			user.addFollowedPlaylist(user.getCurrentSelection());
			database.findMatchPlaylist(user.getCurrentSelection()).addFollower();
			message = "Playlist followed successfully.";
		}
	}

	public void getTop5SongsCommand(Database database) {
		database.updateTop5Songs();
	}

	public void getTop5PlaylistsCommand(Database database) {
		database.updateTop5Playlists();
	}
}
