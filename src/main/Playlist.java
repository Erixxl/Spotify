package main;

import java.util.ArrayList;

public class Playlist {
	private static int instanceCount = 0;
	private final String owner;
	private String playlistName;
	private final int playlistId;
	private String visibility;
	private int followerCount;
	private final ArrayList<Song> songs = new ArrayList<>();

	public Playlist(String owner, String playlistName) {
		++instanceCount;

		this.playlistId = instanceCount;
		this.visibility = "public";
		this.followerCount = 0;
		this.playlistName = playlistName;
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public String getPlaylistName() {
		return playlistName;
	}

	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}

	public int getPlaylistId() {
		return playlistId;
	}

	public String getVisibility() {
		return visibility;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void addFollower() {
		followerCount++;
	}

	public void removeFollower() {
		followerCount--;
		if (followerCount < 0) {
			followerCount = 0;
		}
	}

	public ArrayList<Song> getSongs() {
		return songs;
	}

	public void addSong(Song song) {
		if (!songs.contains(song)) {
			songs.add(song);
		}
	}

	public void removeSong(Song song) {
		songs.remove(song);
	}

	public void switchVisibility() {
		if (visibility.compareTo("public") == 0) {
			visibility = "private";
		} else {
			visibility = "public";
		}
	}
}
