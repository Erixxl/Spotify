package main;

public class PodcastStatus {
	private Podcast podcast;
	private Episode lastEpisode;
	private int timeWatched;

	public PodcastStatus() {
	}

	public PodcastStatus(Podcast podcast) {
		this.podcast = podcast;
		this.lastEpisode = podcast.getEpisodes().get(0);
		this.timeWatched = 0;
	}

	public Podcast getPodcast() {
		return podcast;
	}

	public Episode getLastEpisode() {
		return lastEpisode;
	}

	public void setLastEpisode(Episode lastEpisode) {
		this.lastEpisode = lastEpisode;
	}

	public int getTimeWatched() {
		return timeWatched;
	}

	public void setTimeWatched(int timeWatched) {
		this.timeWatched = timeWatched;
	}
}
