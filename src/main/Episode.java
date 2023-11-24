package main;

import fileio.input.EpisodeInput;

public class Episode {
	private EpisodeInput input;

	public Episode() {
	}

	public Episode(EpisodeInput episode) {
		this.input = new EpisodeInput();

		this.input.setName(episode.getName());
		this.input.setDescription(episode.getDescription());
		this.input.setDuration(episode.getDuration());
	}

	public EpisodeInput getInput() {
		return input;
	}

	public void setInput(EpisodeInput input) {
		this.input = input;
	}
}

