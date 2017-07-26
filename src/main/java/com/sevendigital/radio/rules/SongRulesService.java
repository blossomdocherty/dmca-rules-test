package com.sevendigital.radio.rules;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Class implementing DMCA rules for song playback, rules are:
 * - No more than 3 songs from one album per hour
 * - No more than 4 songs by the same artist per hour
 * - No more than 2 songs from one album played consecutively
 */
public class SongRulesService {

    // A continuous history record
    private Deque<Pair<Instant, Song>> history = new ArrayDeque<>();
    // Counts of albums and artists, enables fast access-time as opposed to traversing above deque o(n)
    private TObjectIntMap<String> albumSongCount = new TObjectIntHashMap<>();
    private TObjectIntMap<String> artistSongCount = new TObjectIntHashMap<>();

    public SongRulesService() {

    }

    /**
     * Tests whether a song can be played at the given start time according to DMCA
     * rules.
     *
     * @param song      The song to check
     * @param startTime The startTime time of the song
     * @return true if the song can be played, false if is it forbidden
     */
    public synchronized boolean canPlay(Song song, Instant startTime) {
        expelOldSongs(startTime);
        boolean canPlay = !triggersARule(song);
        if (canPlay) {
            addSongToHistory(song, startTime);
        }
        return canPlay;
    }

    public int historySize() {
        return history.size();
    }

    public void clear() {
        history.clear();
        albumSongCount.clear();
        artistSongCount.clear();
    }

    /**
     * The song passed all the DMCA rules, so add it to the history
     * @param song song being played
     * @param startTime time of song being played
     */
    private void addSongToHistory(final Song song, final Instant startTime) {
        history.push(Pair.of(startTime, song));
        incrementCount(albumSongCount, song.getAlbum());
        incrementCount(artistSongCount, song.getArtist());
    }

    /**
     * Remove songs that were played more than an hour ago as rules no longer apply
     * @param now time
     */
    private void expelOldSongs(final Instant now) {
        final Instant oneHourAgo = now.minus(1, ChronoUnit.HOURS);
        Set<Pair<Instant, Song>> toRemove = new HashSet<>();
        history
                .stream()
                .filter(s -> s.getLeft().isBefore(oneHourAgo))
                .forEach(
                        s -> {
                            Song song = s.getRight();
                            decrementCount(albumSongCount, song.getAlbum());
                            decrementCount(artistSongCount, song.getArtist());
                            toRemove.add(s);
                        }
                );
        history.removeAll(toRemove);
    }

    private void incrementCount(TObjectIntMap<String> counts, String key) {
        counts.putIfAbsent(key, 0);
        counts.put(key, counts.get(key) + 1);
    }

    private void decrementCount(TObjectIntMap<String> counts, String key) {
        if (counts.containsKey(key)) {
            int newCount = counts.get(key) - 1;
            if (newCount == 0) {
                counts.remove(key);
            } else {
                counts.put(key, newCount);
            }
        }
    }

    /**
     * Determine if this song triggers a DMCA rule against the current history of songs
     * @param song song
     * @return true if a rule is triggered, false if not
     */
    private boolean triggersARule(final Song song) {
        return isFourthSongForAlbum(song) || isFifthSongForArtist(song) || isThirdConsecutiveSongForAlbum(song);
    }

    /**
     * Determine whether song is the 4th to be played for an album in last hour
     * @param song song
     * @return true if song is 4th to be played for album, false if not
     */
    private boolean isFourthSongForAlbum(final Song song) {
        String album = song.getAlbum();
        return albumSongCount.containsKey(album) && albumSongCount.get(album) == 3;
    }

    /**
     * Determine whether song is the 5th to be played for an artist in last hour
     * @param song song
     * @return true if song is the 5th to be played for an artist, false if not
     */
    private boolean isFifthSongForArtist(final Song song) {
        String artist = song.getArtist();
        return artistSongCount.containsKey(artist) && artistSongCount.get(artist) == 4;
    }

    /**
     * Determine whether previous two songs played are from same album as current song
     * @param song song
     * @return true if song and previous two songs are of the same album
     */
    private boolean isThirdConsecutiveSongForAlbum(final Song song) {
        boolean triggered = false;
        if (history.size() > 1) {
            String album = song.getAlbum();
            Pair<Instant, Song> penultimate = history.pop();
            if (album.equals(penultimate.getRight().getAlbum())) {
                Pair<Instant, Song> antepenultimate = history.pop();
                triggered = antepenultimate.getRight().getAlbum().equals(album);
                history.push(antepenultimate);
            }
            history.push(penultimate);
        }
        return triggered;
    }

}
