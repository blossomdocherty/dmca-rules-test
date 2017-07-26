package com.sevendigital.radio.rules

import spock.lang.Specification

import java.time.Instant

import static java.time.temporal.ChronoUnit.MINUTES

class TestNoMoreThanThreeSongsFromAnAlbumInAnHour extends Specification {

	final songRulesService = new SongRulesService()

	final now = Instant.now()

	void 'the first song from an album is allowed'() {
		when:
		def allowed = songRulesService.canPlay(new Song('Rick Astley', 'Never Gonna Give You Up', 'Whenever You Need Somebody'), now)

		then:
		allowed == true
	}

	void 'three non-consecutive songs from an album in an hour are allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Michael Jackson', "Wanna Be Startin' Somethin'", 'Thriller'), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'The Girl Is Mine', 'Thriller'), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('Madonna', 'La Isla Bonita', 'True Blue'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Michael Jackson', 'Thriller', 'Thriller'), now)

		then:
		allowed == true
	}

	void 'four non-consecutive songs from an album in an hour are not allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Michael Jackson', "Wanna Be Startin' Somethin'", 'Thriller'), now.minus(40, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'The Girl Is Mine', 'Thriller'), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('Madonna', 'La Isla Bonita', 'True Blue'), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'Thriller', 'Thriller'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Michael Jackson', 'Beat It', 'Thriller'), now)

		then:
		allowed == false
	}

	void 'four non-consecutive songs from an album in more than an hour are allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Michael Jackson', "Wanna Be Startin' Somethin'", 'Thriller'), now.minus(90, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'The Girl Is Mine', 'Thriller'), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('Madonna', 'La Isla Bonita', 'True Blue'), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'Thriller', 'Thriller'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Michael Jackson', 'Beat It', 'Thriller'), now)

		then:
		allowed == true
	}

	void 'four non-consecutive songs from an album in any one hour window are not allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Michael Jackson', "Wanna Be Startin' Somethin'", 'Thriller'), now.minus(90, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'The Girl Is Mine', 'Thriller'), now.minus(50, MINUTES))
		assert songRulesService.canPlay(new Song('Madonna', 'La Isla Bonita', 'True Blue'), now.minus(40, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'Thriller', 'Thriller'), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'Beat It', 'Thriller'), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'Yesterday', 'Help!'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Michael Jackson', 'Billy Jean', 'Thriller'), now)

		then:
		allowed == false
	}
}