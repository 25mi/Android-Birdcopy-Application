package com.birdcopy.BirdCopyApp.Http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vincentsung on 1/20/16.
 */
public class UserInfoResult {

	@SerializedName("rc")
	@Expose
	private String rc;
	@SerializedName("rm")
	@Expose
	private String rm;
	@SerializedName("userId")
	@Expose
	private String userId;
	@SerializedName("token")
	@Expose
	private String token;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("br_intro")
	@Expose
	private String brIntro;
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
	 * The userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 *
	 * @param userId
	 * The userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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

	/**
	 *
	 * @return
	 * The name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param name
	 * The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return
	 * The brIntro
	 */
	public String getBrIntro() {
		return brIntro;
	}

	/**
	 *
	 * @param brIntro
	 * The br_intro
	 */
	public void setBrIntro(String brIntro) {
		this.brIntro = brIntro;
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
