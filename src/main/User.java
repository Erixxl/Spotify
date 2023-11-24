package main;

import fileio.input.UserInput;

import java.util.ArrayList;

public class User {
	private UserInput input;
	private int playlistCount;
	private final Status playerStatus = new Status();
	private ArrayList<String> lastSearchResults;
	private String lastSearchType;
	private String currentSelection;
	private String currentSelectionType;
	private final ArrayList<String> likedSongs = new ArrayList<>();
	private final ArrayList<String> followedPlaylists = new ArrayList<>();
	private boolean recentSearch;

	public User(UserInput input) {
		this.input = input;

		lastSearchResults = null;
		lastSearchType = null;
		currentSelection = null;
		currentSelectionType = null;
		recentSearch = false;
		playlistCount = 0;
	}

	public boolean isRecentSearch() {
		return recentSearch;
	}

	public void setRecentSearch(boolean recentSearch) {
		this.recentSearch = recentSearch;
	}

	public UserInput getInput() {
		return input;
	}

	public void setInput(UserInput input) {
		this.input = input;
	}

	public Status getPlayerStatus() {
		return playerStatus;
	}

	public ArrayList<String> getLastSearchResults() {
		return lastSearchResults;
	}

	public void setLastSearchResults(ArrayList<String> lastSearchResults) {
		this.lastSearchResults = lastSearchResults;
	}

	public String getLastSearchType() {
		return lastSearchType;
	}

	public void setLastSearchType(String lastSearchType) {
		this.lastSearchType = lastSearchType;
	}

	public String getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(String currentSelection) {
		this.currentSelection = currentSelection;
	}

	public String getCurrentSelectionType() {
		return currentSelectionType;
	}

	public void setCurrentSelectionType(String currentSelectionType) {
		this.currentSelectionType = currentSelectionType;
	}

	public ArrayList<String> getLikedSongs() {
		return likedSongs;
	}

	public void addLikedSong(String songName) {
		if (!likedSongs.contains(songName)) {
			likedSongs.add(songName);
		}
	}

	public void removeLikedSong(String songName) {
		likedSongs.remove(songName);
	}

	public ArrayList<String> getFollowedPlaylists() {
		return followedPlaylists;
	}

	public void addFollowedPlaylist(String playlistName) {
		if (!followedPlaylists.contains(playlistName)) {
			followedPlaylists.add(playlistName);
		}
	}

	public void removeFollowedPlaylist(String playlistName) {
		followedPlaylists.remove(playlistName);
	}

	public void loadCommand(Database database, int timestamp) {
		// Load command should overwrite current playback
		playerStatus.updatePlayer(timestamp);
		// After update, call load method
		playerStatus.loadSource(database, timestamp, currentSelection, currentSelectionType);
		// Empty selection after load
		currentSelection = "";
		currentSelectionType = "";
	}

	public void playCommand(int timestamp) {
		playerStatus.playPlayer(timestamp);
	}

	public void pauseCommand() {
		playerStatus.pausePlayer();
	}

	public int getPlaylistCount() {
		return playlistCount;
	}

	public void incrementPlaylist() {
		playlistCount++;
	}
}
