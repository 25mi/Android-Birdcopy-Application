package com.birdcopy.BirdCopyApp.Http;

/**
 * Created by vincentsung on 1/19/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RongCloudTokenResult {

	@SerializedName("rc")
	@Expose
	private String rc;
	@SerializedName("rm")
	@Expose
	private String rm;
	@SerializedName("token")
	@Expose
	private String token;

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
	 * The token
	 */
	public String getToken() {
		return token;
	}

	/**
	 *
	 * @param token
	 * The token
	 */
	public void setToken(String token) {
		this.token = token;
	}

}

