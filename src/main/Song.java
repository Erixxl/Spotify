package main;

import fileio.input.SongInput;

public class Song {
	private SongInput input;
	private Integer likeCount;

	public Song() {
	}

	public Song(SongInput song) {
		this.likeCount = 0;
		this.input = new SongInput();

		this.input.setName(song.getName());
		this.input.setDuration(song.getDuration());
		this.input.setAlbum(song.getAlbum());
		this.input.setTags(song.getTags());
		this.input.setLyrics(song.getLyrics());
		this.input.setGenre(song.getGenre());
		this.input.setReleaseYear(song.getReleaseYear());
		this.input.setArtist(song.getArtist());
	}

	public SongInput getInput() {
		return input;
	}

	public void setInput(SongInput input) {
		this.input = input;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void addLike() {
		likeCount++;
	}

	public void removeLike() {
		likeCount--;
		if (likeCount < 0) {
			likeCount = 0;
		}
	}
}
