package com.birdcopy.BirdCopyApp.Media.SrtSubtitle;

import com.google.android.exoplayer.text.Subtitle;
import com.google.android.exoplayer.text.SubtitleParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vincentsung on 1/4/16.
 */
public class FlyingSubParser implements SubtitleParser {

    /**
     * Checks whether the parser supports a given subtitle mime type.
     *
     * @param mimeType A subtitle mime type.
     * @return Whether the mime type is supported.
     */
    public boolean canParse(String mimeType){

        return true;
    }

    /**
     * Parses a {@link Subtitle} from the provided {@link InputStream}.
     *
     * @param inputStream The stream from which to parse the subtitle.
     * @return A parsed representation of the subtitle.
     * @throws IOException If a problem occurred reading from the stream.
     */
    public FlyingSubTitle parse(InputStream inputStream) throws IOException{

       FlyingSubTitle subTitle = new FlyingSubTitle();

        subTitle.parse(inputStream);
        
        return subTitle;
    }
}
