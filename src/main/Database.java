package main;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.Collections;

public class Database {
	private ArrayList<User> users;
	private ArrayList<Song> songs;
	private ArrayList<Playlist> playlists;
	private ArrayList<Podcast> podcasts;
	private final ArrayList<Playlist> top5Playlists = new ArrayList<>();
	private final ArrayList<Song> top5Songs = new ArrayList<>();

	public Database() {
	}

	public Database(LibraryInput library) {
		this.users = new ArrayList<>();
		this.songs = new ArrayList<>();
		this.playlists = new ArrayList<>();
		this.podcasts = new ArrayList<>();

		for (UserInput userInput : library.getUsers()) {
			users.add(new User(userInput));
		}
		for (SongInput songInput : library.getSongs()) {
			songs.add(new Song(songInput));
		}
		for (PodcastInput podcastInput : library.getPodcasts()) {
			podcasts.add(new Podcast(podcastInput));
		}
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public ArrayList<Song> getSongs() {
		return songs;
	}

	public ArrayList<Playlist> getPlaylists() {
		return playlists;
	}

	public void addPlaylist(Playlist newPlaylist) {
		if (newPlaylist == null) {
			return;
		}
		if (!playlists.contains(newPlaylist)) {
			playlists.add(newPlaylist);
		}
	}

	public ArrayList<Podcast> getPodcasts() {
		return podcasts;
	}

	public User findMatchUser(String name) {
		if (name == null) {
			return null;
		}
		for (User user : users) {
			if (user.getInput().getUsername().compareTo(name) == 0) {
				return user;
			}
		}
		return null;
	}

	public Song findMatchSong(String name) {
		if (name == null) {
			return null;
		}
		for (Song song : songs) {
			if (song.getInput().getName().compareTo(name) == 0) {
				return song;
			}
		}
		return null;
	}

	public Playlist findMatchPlaylist(String name) {
		if (name == null) {
			return null;
		}
		for (Playlist playlist : playlists) {
			if (playlist.getPlaylistName().compareTo(name) == 0) {
				return playlist;
			}
		}
		return null;
	}

	public Podcast findMatchPodcast(String name) {
		if (name == null) {
			return null;
		}
		for (Podcast podcast : podcasts) {
			if (podcast.getName().compareTo(name) == 0) {
				return podcast;
			}
		}
		return null;
	}

	public Episode findMatchEpisode(String name, Podcast podcast) {
		if (name == null || podcast == null) {
			return null;
		}
		for (Episode episode : podcast.getEpisodes()) {
			if (episode.getInput().getName().compareTo(name) == 0) {
				return episode;
			}
		}
		return null;
	}

	public Playlist findIDPlaylist(int id) {
		for (Playlist playlist : playlists) {
			if (playlist.getPlaylistId() == id) {
				return playlist;
			}
		}

		return null;
	}

	public ArrayList<Playlist> getTop5Playlists() {
		return top5Playlists;
	}

	public ArrayList<Song> getTop5Songs() {
		return top5Songs;
	}

	public void updateTop5Playlists() {
		top5Playlists.removeAll(top5Playlists);

		for (Playlist playlist: playlists) {
			top5Playlists.add(playlist);

			for (int index = top5Playlists.size() - 1; index > 0; index--) {
				if (top5Playlists.get(index).getFollowerCount() >
					top5Playlists.get(index - 1).getFollowerCount()) {
					Collections.swap(top5Playlists, index, index - 1);
				} else {
					break;
				}
			}

			if (top5Playlists.size() == 6) {
				top5Playlists.remove(5);
			}
		}
	}

	public void updateTop5Songs() {
		top5Songs.removeAll(top5Songs);

		for (Song song : songs) {
			top5Songs.add(song);

			for (int index = top5Songs.size() - 1; index > 0; index--) {
				if (top5Songs.get(index).getLikeCount() >
					top5Songs.get(index - 1).getLikeCount()) {
					Collections.swap(top5Songs, index, index - 1);
				} else {
					break;
				}
			}

			if (top5Songs.size() == 6) {
				top5Songs.remove(5);
			}
		}
	}

	public Playlist getPlaylistMadeByUser(User user, int index) {
		int count = 0;
		for (Playlist playlist : playlists) {
			if (playlist.getOwner().compareTo(user.getInput().getUsername()) == 0) {
				count++;
				if (count == index) {
					return playlist;
				}
			}
		}

		return null;
	}
}
