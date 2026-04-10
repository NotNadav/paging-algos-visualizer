public class PageFrame {
    private int pageNumber;
    private boolean referenceBit;
    private int timestamp;

    public PageFrame(int pageNumber) {
        this.pageNumber = pageNumber;
        this.referenceBit = true;
        this.timestamp = 0;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean getReferenceBit() {
        return referenceBit;
    }

    public void setReferenceBit(boolean referenceBit) {
        this.referenceBit = referenceBit;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public PageFrame copy() {
        PageFrame copy = new PageFrame(this.pageNumber);
        copy.referenceBit = this.referenceBit;
        copy.timestamp = this.timestamp;
        return copy;
    }

    @Override
    public String toString() {
        return String.valueOf(pageNumber);
    }
}
