package com.birdcopy.BirdCopyApp.DataManager;

public class FlyingItemData {

    private String BEWORD;
    private String BEINDEX;
    private String BEENTRY;
    private String BETAG;

	public FlyingItemData() {
	}


	public FlyingItemData(String BEWORD, String BEINDEX, String BEENTRY, String BETAG) {

        this.BEWORD = BEWORD;
        this.BEINDEX = BEINDEX;
        this.BEENTRY = BEENTRY;
        this.BETAG = BETAG;
    }

    public String getBEWORD() {
        return BEWORD;
    }

    public void setBEWORD(String BEWORD) {
        this.BEWORD = BEWORD;
    }

    public String getBEINDEX() {
        return BEINDEX;
    }

    public void setBEINDEX(String BEINDEX) {
        this.BEINDEX = BEINDEX;
    }

    public String getBEENTRY() {
        return BEENTRY;
    }

    public void setBEENTRY(String BEENTRY) {
        this.BEENTRY = BEENTRY;
    }

    public String getBETAG() {
        return BETAG;
    }

    public void setBETAG(String BETAG) {
        this.BETAG = BETAG;
    }
}