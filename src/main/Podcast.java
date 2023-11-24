package main;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;

import java.util.ArrayList;

public class Podcast {
	private String name;
	private String owner;
	private ArrayList<Episode> episodes;

	public Podcast() {
	}

	public Podcast(PodcastInput podcast) {
		this.name = podcast.getName();
		this.owner = podcast.getOwner();
		this.episodes = new ArrayList<>();

		for (EpisodeInput episodeInput : podcast.getEpisodes()) {
			this.episodes.add(new Episode(episodeInput));
		}
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

	public ArrayList<Episode> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(ArrayList<Episode> episodes) {
		this.episodes = episodes;
	}
}
