package com.birdcopy.BirdCopyApp.Media;

import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.Subtitle;
import com.google.android.exoplayer.util.Assertions;
import com.google.android.exoplayer.util.Util;


import java.util.Collections;
import java.util.List;

/**
 * Created by vincentsung on 1/4/16.
 */
public class FlyingSubTitle implements Subtitle {

    private final Cue[] cues;
    private final long[] cueTimesUs;

    /**
     * @param cues The cues in the subtitle. Null entries may be used to represent empty cues.
     * @param cueTimesUs The cue times, in microseconds.
     */
    public FlyingSubTitle(Cue[] cues, long[] cueTimesUs) {
        this.cues = cues;
        this.cueTimesUs = cueTimesUs;
    }

    /**
     * Gets the index of the first event that occurs after a given time (exclusive).
     *
     * @param timeUs The time in microseconds.
     * @return The index of the next event, or -1 if there are no events after the specified time.
     */
    @Override
    public int getNextEventTimeIndex(long timeUs) {
        int index = Util.binarySearchCeil(cueTimesUs, timeUs, false, false);
        return index < cueTimesUs.length ? index : -1;
    }

    /**
     * Gets the number of event times, where events are defined as points in time at which the cues
     * returned by {@link #getCues(long)} changes.
     *
     * @return The number of event times.
     */
    @Override
    public int getEventTimeCount() {

        return cueTimesUs.length;
    }

    /**
     * Gets the event time at a specified index.
     *
     * @param index The index of the event time to obtain.
     * @return The event time in microseconds.
     */
    @Override
    public long getEventTime(int index) {
        Assertions.checkArgument(index >= 0);
        Assertions.checkArgument(index < cueTimesUs.length);
        return cueTimesUs[index];
    }

    /**
     * Convenience method for obtaining the last event time.
     *
     * @return The time of the last event in microseconds, or -1 if {@code getEventTimeCount() == 0}.
     */
    @Override
    public long getLastEventTime() {
        if (getEventTimeCount() == 0) {
            return -1;
        }
        return cueTimesUs[cueTimesUs.length - 1];
    }

    /**
     * Retrieve the subtitle cues that should be displayed at a given time.
     *
     * @param timeUs The time in microseconds.
     * @return A list of cues that should be displayed, possibly empty.
     */
    @Override
    public List<Cue> getCues(long timeUs) {
        int index = Util.binarySearchFloor(cueTimesUs, timeUs, true, false);
        if (index == -1 || cues[index] == null) {
            // timeUs is earlier than the start of the first cue, or we have an empty cue.
            return Collections.<Cue>emptyList();
        } else {
            return Collections.singletonList(cues[index]);
        }
    }

    public Cue getCue(long timeUs) {
        int index = Util.binarySearchFloor(cueTimesUs, timeUs, true, false);
        if (index == -1 || cues[index] == null) {
            // timeUs is earlier than the start of the first cue, or we have an empty cue.
            return null;
        } else {
            return cues[index];
        }
    }
}
