package com.sevendigital.radio.rules;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by alexander.docherty on 19/07/2017.
 */
public class SongRulesServiceTest {

    private static final Instant now = Instant.now();

    @Test
    public void testHistoryCache() {
        SongRulesService songRulesService = new SongRulesService();
        songRulesService.canPlay(new Song("Donna Summer", "Bad Girls", "Bad Girls"), now.minus(20, ChronoUnit.MINUTES));
        songRulesService.canPlay(new Song("Johnny Cash", "Ring of Fire", "I Walk the Line"), now.minus(15, ChronoUnit.MINUTES));
        assertThat(songRulesService.historySize()).isEqualTo(2);
        songRulesService.clear();
        assertThat(songRulesService.historySize()).isEqualTo(0);
    }

    @Test
    public void testNoOldSongsInCache() {
        SongRulesService songRulesService = new SongRulesService();
        songRulesService.canPlay(new Song("Donna Summer", "Bad Girls", "Bad Girls"), now);
        assertThat(songRulesService.historySize()).isEqualTo(1);
        songRulesService.canPlay(new Song("Donna Summer", "Bad Girls", "Bad Girls"), now.plus(2, ChronoUnit.HOURS));
        assertThat(songRulesService.historySize()).isEqualTo(1);
    }

    /**
     * Test a possible bug scenario where many threads are trying to play the same song at once with the same service
     * No more than 2 consecutive plays as the service shouldn't be susceptible to different threads being able to break the DMCA rules
     * Technically,
     */
    @Test
    public void testThreadSafe() {
        SongRulesService songRulesService = new SongRulesService();
        List<Pair<Song, Instant>> songs =
                Lists.newArrayList(
                        Pair.of(new Song("Donna Summer", "Bad Girls", "Bad Girls"), now.minus(30, ChronoUnit.MINUTES)),
                        Pair.of(new Song("Donna Summer", "Bad Girls", "Bad Girls"), now.minus(30, ChronoUnit.MINUTES)),
                        Pair.of(new Song("Donna Summer", "Bad Girls", "Bad Girls"), now.minus(30, ChronoUnit.MINUTES)),
                        Pair.of(new Song("Donna Summer", "Bad Girls", "Bad Girls"), now.minus(30, ChronoUnit.MINUTES))
                );
        long playedSongs = songs.parallelStream().map(s -> songRulesService.canPlay(s.getLeft(), s.getRight())).filter(s -> s.equals(true)).count();
        assertThat(playedSongs).isEqualTo(2);
    }
}
