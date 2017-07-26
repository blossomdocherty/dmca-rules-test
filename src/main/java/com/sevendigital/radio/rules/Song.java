package com.sevendigital.radio.rules;

public class Song {

	private final String artist;

	private final String title;

	private final String album;

	public Song(String artist, String title, String album) {

		this.artist = artist;

		this.title = title;

		this.album = album;
	}

	public String getArtist() {

		return artist;
	}

	public String getTitle() {

		return title;
	}

	public String getAlbum() {

		return album;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Song song = (Song) o;

		return artist.equals(song.artist) && title.equals(song.title) && album.equals(song.album);

	}

	@Override
	public int hashCode() {
		int result = artist.hashCode();
		result = 31 * result + title.hashCode();
		result = 31 * result + album.hashCode();
		return result;
	}
}
