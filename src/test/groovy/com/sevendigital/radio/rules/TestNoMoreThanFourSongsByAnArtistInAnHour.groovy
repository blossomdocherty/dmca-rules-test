package com.sevendigital.radio.rules

import spock.lang.Specification

import java.time.Instant

import static java.time.temporal.ChronoUnit.MINUTES

class TestNoMoreThanFourSongsByAnArtistInAnHour extends Specification {

	final songRulesService = new SongRulesService()

	final now = Instant.now()

	void 'the first song by an artist is allowed'() {
		when:
		def allowed = songRulesService.canPlay(new Song('Las Ketchup', 'The Ketchup Song', 'Hijas del Tomate'), now)

		then:
		allowed == true
	}

	void 'four songs by an artist in an hour are allowed'() {
		given:
		assert songRulesService.canPlay(new Song('Madonna', 'Borderline', 'Madonna'), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('Madonna', 'Material Girl', 'Like a Virgin'), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('Madonna', "Papa Don't Preach", 'True Blue'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('Madonna', 'Express Yourself', 'Like a Prayer'), now)

		then:
		allowed == true
	}

	void 'five songs by an artist in an hour are not allowed'() {
		given:
		assert songRulesService.canPlay(new Song('The Beatles', 'Love Me Do', 'Please Please Me'), now.minus(40, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'All My Loving', 'With The Beatles'), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', "Can't Buy Me Love", "A Hard Day's Night"), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'Yesterday', 'Help!'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('The Beatles', 'Drive My Car', 'Rubber Soul'), now)

		then:
		allowed == false
	}

	void 'five songs by an artist in more an hour are allowed'() {
		given:
		assert songRulesService.canPlay(new Song('The Beatles', 'Love Me Do', 'Please Please Me'), now.minus(70, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'All My Loving', 'With The Beatles'), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', "Can't Buy Me Love", "A Hard Day's Night"), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'Yesterday', 'Help!'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('The Beatles', 'Drive My Car', 'Rubber Soul'), now)

		then:
		allowed == true
	}

	void 'five songs by an artist in any one hour window are not allowed'() {
		given:
		assert songRulesService.canPlay(new Song('The Beatles', 'Love Me Do', 'Please Please Me'), now.minus(90, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'All My Loving', 'With The Beatles'), now.minus(40, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', "Can't Buy Me Love", "A Hard Day's Night"), now.minus(30, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'Yesterday', 'Help!'), now.minus(20, MINUTES))
		assert songRulesService.canPlay(new Song('The Beatles', 'Drive My Car', 'Rubber Soul'), now.minus(10, MINUTES))

		when:
		def allowed = songRulesService.canPlay(new Song('The Beatles', 'Eleanor Rigby', 'Revolver'), now)

		then:
		allowed == false
	}
}