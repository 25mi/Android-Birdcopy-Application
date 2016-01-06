package com.birdcopy.BirdCopyApp.Media.SrtSubtitle;

import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.Subtitle;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by vincentsung on 1/4/16.
 */
public class FlyingSubTitle implements Subtitle {

    /**
     * Gets the index of the first event that occurs after a given time (exclusive).
     *
     * @param timeUs The time in microseconds.
     * @return The index of the next event, or -1 if there are no events after the specified time.
     */
    public int getNextEventTimeIndex(long timeUs){

        int index = NSNotFound;

        int tempIndex = 0;
        while (tempIndex<getEventTimeCount())
        {
            Caption caption = parsedSub.captions.get(tempIndex);

            if(caption.start.mseconds>timeUs){

                index=tempIndex;

                break;
            }
            else
            {
                tempIndex++;
            }
        }

        return  index;
    }

    /**
     * Gets the number of event times, where events are defined as points in time at which the cues
     * returned by {@link #getCues(long)} changes.
     *
     * @return The number of event times.
     */
    public int getEventTimeCount() {

        int count=0;

        if(parsedSub!=null){

            count = parsedSub.captions.size();
        }

        return  count;
    }

    /**
     * Gets the event time at a specified index.
     *
     * @param index The index of the event time to obtain.
     * @return The event time in microseconds.
     */
    public long getEventTime(int index) {

        long eventTime=NSNotFound;

        if(parsedSub!=null){

            eventTime = parsedSub.captions.get(index).start.mseconds;
        }
        return  eventTime;
    }

    /**
     * Convenience method for obtaining the last event time.
     *
     * @return The time of the last event in microseconds, or -1 if {@code getEventTimeCount() == 0}.
     */
    public long getLastEventTime(){

        long eventTime=NSNotFound;

        if(parsedSub!=null) {

            eventTime = parsedSub.captions.get(getEventTimeCount()-1).start.mseconds;
        }

        return  eventTime;
    }

    /**
     * Retrieve the subtitle cues that should be displayed at a given time.
     *
     * @param timeUs The time in microseconds.
     * @return A list of cues that should be displayed, possibly empty.
     */
    public List<Cue> getCues(long timeUs){


        return  null;
    }

    //扩展内容
    TimedTextObject parsedSub=null;

    public void parse(InputStream inputStream) throws IOException{

        {
            try {

                TimedTextFileFormat ttff = new FormatSRT();
                parsedSub = ttff.parseFile("subtitle", inputStream);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (FatalParsingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    static public int NSNotFound=-1;

    public int idxOfSubItemWithSubTime(long timeUs) {

        int index = NSNotFound;

        int tempIndex = 0;
        while (tempIndex<getEventTimeCount())
        {
            Caption caption = parsedSub.captions.get(tempIndex);

            if(timeUs<caption.start.mseconds)
            {
                tempIndex++;
            }
            else
            {
                if(timeUs <= caption.end.mseconds){

                    index=tempIndex;

                    break;
                }
                else
                {
                    break;
                }
            }
        }

        return  index;
    }

    public Caption getSubItemForIndex(int index) {

        if(parsedSub!=null){

            return parsedSub.captions.get(index);
        }
        else {
            return null;
        }

    }

    //获得字幕开始时间
    public long getStartSubtitleTime()
    {
        if(parsedSub!=null) {

            return  parsedSub.captions.get(0).start.mseconds;
        }
        else
        {
            return  NSNotFound;
        }
    }

    //获得字幕结束时间
    public long  getEndSubtitleTime()
    {
        if(parsedSub!=null) {

            return  parsedSub.captions.get(getEventTimeCount()-1).end.mseconds;
        }
        else
        {
            return  NSNotFound;
        }
    }

}
