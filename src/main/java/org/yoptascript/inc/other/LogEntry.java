package org.yoptascript.inc.other;

public class LogEntry {
    private String date;
    private String level;
    private String uri;
    private String method;
    private String req_headers;
    private String resp_headers;
    private String status;

    public LogEntry(String date, String level, String uri, String method, String req_headers, String resp_headers, String status) {
        this.date = date;
        this.level = level;
        this.uri = uri;
        this.method = method;
        this.req_headers = req_headers;
        this.resp_headers = resp_headers;
        this.status = status;
    }
}

