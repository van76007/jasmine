package www.jasmine.network;

public class ReportBuilder {
    private StringBuilder stringBuilder = new StringBuilder();

    public String getReportMessage() {
        return stringBuilder.toString();
    }

    public void appendReportMessage(String reportMessage) {
        this.stringBuilder.append(reportMessage).append("\n");
    }

    @Override
    public String toString() {
        return "ReportBuilder{" +
                "stringBuilder=" + stringBuilder.toString() +
                '}';
    }
}
