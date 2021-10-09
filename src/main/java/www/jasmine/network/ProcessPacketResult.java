package www.jasmine.network;

public class ProcessPacketResult {
    private int sequence;
    private int ttl;
    private String reportMessage;

    public ProcessPacketResult(int sequence, int ttl) {
        this.sequence = sequence;
        this.ttl = ttl;
    }

    public void increaseSequence(int amount) {
        this.sequence += amount;
    }

    public void increaseTtl(int amount) {
        this.ttl += amount;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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
                "count=" + sequence +
                ", ttl=" + ttl +
                ", reportMessage='" + (reportMessage == null ? "null" : reportMessage) + '\'' +
                '}';
    }
}
