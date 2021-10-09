package www.jasmine.network;

public class ProcessPacketResult {
    private int count;
    private int ttl;
    private String reportMessage;

    public ProcessPacketResult(int count, int ttl) {
        this.count = count;
        this.ttl = ttl;
    }

    public void increaseCount(int amount) {
        this.count += amount;
    }

    public void increaseTtl(int amount) {
        this.ttl += amount;
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
                "count=" + count +
                ", ttl=" + ttl +
                ", reportMessage='" + (reportMessage == null ? "null" : reportMessage) + '\'' +
                '}';
    }
}
