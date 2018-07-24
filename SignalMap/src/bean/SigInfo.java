package bean;

public class SigInfo {
    private int sigStrength ;
    private int sigLevel ;
    private String sigType ;
    private CellID cid ;

    public int getSigStrength() {
        return sigStrength;
    }

    public void setSigStrength(int sigStrength) {
        this.sigStrength = sigStrength;
    }

    public int getSigLevel() {
        return sigLevel;
    }

    public void setSigLevel(int sigLevel) {
        this.sigLevel = sigLevel;
    }

    public String getSigType() {
        return sigType;
    }

    public void setSigType(String sigType) {
        this.sigType = sigType;
    }

    public CellID getCid() {
        return cid;
    }

    public void setCid(CellID cid) {
        this.cid = cid;
    }
}

class CellID{
    private int mMcc ;
    private int mMnc ;
}

