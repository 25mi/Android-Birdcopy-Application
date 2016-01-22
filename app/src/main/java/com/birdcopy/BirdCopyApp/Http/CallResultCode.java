package com.birdcopy.BirdCopyApp.Http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vincentsung on 1/22/16.
 */
public class CallResultCode {

	@SerializedName("rc")
	@Expose
	private String rc;
	@SerializedName("rm")
	@Expose
	private String rm;
	@SerializedName("rs")
	@Expose
	private String rs;

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
	 * The rs
	 */
	public String getRs() {
		return rs;
	}

	/**
	 *
	 * @param rs
	 * The rs
	 */
	public void setRs(String rs) {
		this.rs = rs;
	}

}

