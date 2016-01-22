package com.birdcopy.BirdCopyApp.Http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vincentsung on 1/20/16.
 */
public class UpUserProtraitResult {

	@SerializedName("rc")
	@Expose
	private String rc;
	@SerializedName("rm")
	@Expose
	private String rm;
	@SerializedName("portraitUri")
	@Expose
	private String portraitUri;

	/**
	 *
	 * @return
	 * The rc
	 */
	public String getRc() {
		return rc;
	}

	/**
	 *
	 * @param rc
	 * The rc
	 */
	public void setRc(String rc) {
		this.rc = rc;
	}

	/**
	 *
	 * @return
	 * The rm
	 */
	public String getRm() {
		return rm;
	}

	/**
	 *
	 * @param rm
	 * The rm
	 */
	public void setRm(String rm) {
		this.rm = rm;
	}

	/**
	 *
	 * @return
	 * The portraitUri
	 */
	public String getPortraitUri() {
		return portraitUri;
	}

	/**
	 *
	 * @param portraitUri
	 * The portraitUri
	 */
	public void setPortraitUri(String portraitUri) {
		this.portraitUri = portraitUri;
	}
}
