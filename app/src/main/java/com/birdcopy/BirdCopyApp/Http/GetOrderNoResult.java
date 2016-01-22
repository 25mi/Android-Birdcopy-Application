package com.birdcopy.BirdCopyApp.Http;

/**
 * Created by vincentsung on 1/22/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetOrderNoResult {

	@SerializedName("rc")
	@Expose
	private String rc;
	@SerializedName("rm")
	@Expose
	private String rm;
	@SerializedName("order_no")
	@Expose
	private String orderNo;

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
	 * The orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 *
	 * @param orderNo
	 * The order_no
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}
