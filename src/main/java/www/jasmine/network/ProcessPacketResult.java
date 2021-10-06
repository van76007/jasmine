package www.jasmine.network;

public class ProcessPacketResult {
    private boolean isLastResult;
    private int count;
    private int ttl;
    private String reportMessage;

    public ProcessPacketResult(boolean isLastResult, int count, int ttl) {
        this.isLastResult = isLastResult;
        this.count = count;
        this.ttl = ttl;
    }

    public void increaseCount(int amount) {
        this.count += amount;
    }

    public void increaseTtl(int amount) {
        this.ttl += amount;
    }

    public boolean isLastResult() {
        return isLastResult;
    }

    public void setLastResult(boolean lastResult) {
        isLastResult = lastResult;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }


    @Override
    public String toString() {
        return "ProcessPacketResult{" +
                "isLastResult=" + isLastResult +
                ", count=" + count +
                ", ttl=" + ttl +
                ", reportMessage='" + (reportMessage == null ? "null" : reportMessage) + '\'' +
                '}';
    }
}
