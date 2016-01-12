package com.birdcopy.BirdCopyApp.Media;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleParser;
import com.google.android.exoplayer.util.LongArray;
import com.google.android.exoplayer.util.MimeTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by vincentsung on 1/9/16.
 */


public final class FlyingSrtParser implements SubtitleParser {

	private static final String TAG = "SubripParser";

	private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("(\\S*)\\s*-->\\s*(\\S*)");
	private static final Pattern SUBRIP_TIMESTAMP =
			Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+),(\\d+)");

	private final StringBuilder textBuilder;

	public FlyingSrtParser() {
		textBuilder = new StringBuilder();
	}

	@Override
	public boolean canParse(String mimeType) {
		return MimeTypes.APPLICATION_SUBRIP.equals(mimeType);
	}

	@Override
	public FlyingSubTitle parse(InputStream inputStream) throws IOException {
		ArrayList<Cue> cues = new ArrayList<>();
		LongArray cueTimesUs = new LongArray();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, C.UTF8_NAME));
		boolean haveEndTimecode;
		String currentLine;

		while ((currentLine = reader.readLine()) != null) {
			if (currentLine.length() == 0) {
				// Skip blank lines.
				continue;
			}

			// Parse the index line as a sanity check.
			try {
				Integer.parseInt(currentLine);
			} catch (NumberFormatException e) {
				Log.w(TAG, "Skipping invalid index: " + currentLine);
				continue;
			}

			// Read and parse the timing line.
			haveEndTimecode = false;
			currentLine = reader.readLine();
			Matcher matcher = SUBRIP_TIMING_LINE.matcher(currentLine);
			if (matcher.find()) {
				cueTimesUs.add(parseTimecode(matcher.group(1)));
				String endTimecode = matcher.group(2);
				if (!TextUtils.isEmpty(endTimecode)) {
					haveEndTimecode = true;
					cueTimesUs.add(parseTimecode(matcher.group(2)));
				}
			} else {
				Log.w(TAG, "Skipping invalid timing: " + currentLine);
				continue;
			}

			// Read and parse the text.
			textBuilder.setLength(0);
			while (!TextUtils.isEmpty(currentLine = reader.readLine())) {
				if (textBuilder.length() > 0) {
					textBuilder.append("<br>");
				}
				textBuilder.append(currentLine.trim());
			}

			Spanned text = Html.fromHtml(textBuilder.toString());
			cues.add(new Cue(text));
			if (haveEndTimecode) {
				cues.add(null);
			}
		}

		Cue[] cuesArray = new Cue[cues.size()];
		cues.toArray(cuesArray);
		long[] cueTimesUsArray = cueTimesUs.toArray();
		return new FlyingSubTitle(cuesArray, cueTimesUsArray);
	}

	private static long parseTimecode(String s) throws NumberFormatException {
		Matcher matcher = SUBRIP_TIMESTAMP.matcher(s);
		if (!matcher.matches()) {
			throw new NumberFormatException("has invalid format");
		}
		long timestampMs = Long.parseLong(matcher.group(1)) * 60 * 60 * 1000;
		timestampMs += Long.parseLong(matcher.group(2)) * 60 * 1000;
		timestampMs += Long.parseLong(matcher.group(3)) * 1000;
		timestampMs += Long.parseLong(matcher.group(4));
		return timestampMs;
	}

}
