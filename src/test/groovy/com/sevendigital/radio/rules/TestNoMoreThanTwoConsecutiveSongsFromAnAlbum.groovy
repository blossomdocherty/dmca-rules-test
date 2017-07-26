package com.sevendigital.radio.rules

import spock.lang.Specification

import java.time.Instant

import static java.time.temporal.ChronoUnit.MINUTES

class TestNoMoreThanTwoConsecutiveSongsFromAnAlbum extends Specification {

	final songRulesService = new SongRulesService()

	final now = Instant.now()

	void 'a single song from an album is allowed'() {
		when:
		def allowed = songRulesService.canPlay(new Song('Led Zeppelin', 'Black Dog', 'Led Zeppelin IV'), now)

		then:
		allowed == true
	}

	void 'two consecutive songs from an album are allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Led Zeppelin', 'Black Dog', 'Led Zeppelin IV'), now.minus(3, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Led Zeppelin', 'Stairway to Heaven', 'Led Zeppelin IV'), now)

		then:
		allowed == true
	}

	void 'three consecutive songs from an album are not allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Led Zeppelin', 'Black Dog', 'Led Zeppelin IV'), now.minus(6, MINUTES))
		assert songRulesService.canPlay(new Song('Led Zeppelin', 'Stairway to Heaven', 'Led Zeppelin IV'), now.minus(3, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Led Zeppelin', 'When the Levee Breaks', 'Led Zeppelin IV'), now)

		then:
		allowed == false
	}

	void 'three non consecutive songs from an album are allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Led Zeppelin', 'Black Dog', 'Led Zeppelin IV'), now.minus(9, MINUTES))
		assert songRulesService.canPlay(new Song('Led Zeppelin', 'Stairway to Heaven', 'Led Zeppelin IV'), now.minus(6, MINUTES))
		assert songRulesService.canPlay(new Song('Michael Jackson', 'Beat It', 'Thriller'), now.minus(3, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Led Zeppelin', 'When the Levee Breaks', 'Led Zeppelin IV'), now)

		then:
		allowed == true
	}
}