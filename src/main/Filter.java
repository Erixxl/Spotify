package main;

import fileio.input.SongInput;

import java.util.ArrayList;

/**
 * Class used for parsing filters before executing
 * a search query. Jackson should initialize with null
 * all unused values.
 * @author Badescu Andrei-Cristian (andrei.badescu1512)
 */
public class Filter {
	private String name;
	private String owner;
	private String album;
	private ArrayList<String> tags;
	private String lyrics;
	private String genre;
	private String releaseYear;
	private String artist;

	/**
	 * For coding style
	 */
	public Filter() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = new ArrayList<>(tags);
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(String releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * Method for comparing two lists of tags
	 * @param tagList is the list this.tags will be compared against
	 * @return true if the tags provided by the command are found in
	 *      tagList, false otherwise
	 */
	public boolean matchTags(ArrayList<String> tagList) {
		if (this.getTags() == null) {
			return false;
		}

		for (String tag : this.getTags()) {
			if (!tagList.contains(tag)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Method for checking if a given release year (of a song) is in the
	 * range provided by the releaseYear field. Returns false on equality
	 * @param checkYear is the value that should be compared with releaseYear
	 * @return true if the condition of releaseYear is met, false otherwise
	 */
	public boolean checkYearPeriod(Integer checkYear) {
		if (this.releaseYear == null) {
			return false;
		}

		Integer releaseYear = Integer.parseInt(this.releaseYear.substring(1));
		Integer compareSign = this.releaseYear.startsWith("<") ? 1 : -1;

		return (checkYear * compareSign < releaseYear * compareSign);
	}
}
