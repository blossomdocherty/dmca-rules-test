# Song Rules

## Overview
For music streaming services there are various licences and rules that govern
what songs can be played when.  These rules are typically described in terms
of properties of the song, such as the artist,album, etc.  It is vital that
any music streaming service follow these rules as non-compliance can lead to
services being shut down and heavy fines.

## DMCA
The most common of these rules come from the DMCA (Digital Millennium Copyright
Act), a piece of US legislation covering among other things online music
streaming.  You need to implement the following rules from the DMCA:

* No more than 3 songs from one album per hour
* No more than 4 songs by the same artist per hour
* No more than 2 songs from one album played consecutively

"Per hour" is defined as a rolling one-hour window from `now` to one hour in the past.

So for example, if the stars are songs from the same album, this would be allowed:

      -1h                          now
       |----------------------------|
            *              *        *
          
this would be forbidden:
     
      -1h                          now
       |----------------------------|
            *       *      *        *
          
and this would be allowed:

      -1h                          now
       |----------------------------|
     *      *              *        *

## Implementation
The project so far contains a `Song` domain class and a stub `SongRulesService`.
There are also several [Spock] [spock] specifications that test the rules.

The context is that this service will form part of a larger system.  Other
components will call it with songs they want to play, and the start times that
they want to start playing them.

Your task is to write an implementation of the `SongRulesService.canPlay()`
method that enforces the DMCA rules.

The method takes two parameters: the song that is being tested for playback and
the time playback will start.  Your method should return `true` if
song can be played or `false` if it cannot given the songs that were played before.

The Spock specifications set up several different scenarios where songs have
been played in the past and then tests if a song can be played "now".  I would
recommend implementing your own tests in order to develop your code.  JUnit 4
and Hamcrest core matchers are available on the classpath.

You are not expected to complete all of the rules in the time available: this
is based on a real piece of work we did that took several days of investigation
and implementation.  Concentrate on doing the simplest thing you can at every
step, and please ask if something is unclear.  This can be a complex problem to
 get your head round so do ask if you need more information.

[spock]: http://spockframework.github.io/spock/docs/1.0/index.html "Spock Framework"

## The Java 8 Date/Time API

If you aren't familiar with the Java 8 Date/Time API, here are the main methods that
are helpful in this exercise

`java.time.Instant`
    - an immutable instant in time, without needing timezone information

`Instant.isBefore(Instant)` and `Instant.isAfter(Instant)`
    - compares two instants temporally
 
 `Instant.plus(long, TemporalUnit)` and `Instant.minus(long, TemporalUnit)`
    - returns new Instants with the appropriate amount of time added or subtracted.
    `java.time.temporal.ChronoUnit` is the most useful implementation of `TemporalUnit` # dmca-rules-test
