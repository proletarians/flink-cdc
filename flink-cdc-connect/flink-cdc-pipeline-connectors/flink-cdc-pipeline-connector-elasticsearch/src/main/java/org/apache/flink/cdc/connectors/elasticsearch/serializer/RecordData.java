package org.apache.flink.cdc.connectors.elasticsearch.serializer;


import java.util.Date;

public class RecordData {
    private final Object[] fields;

    public RecordData(Object[] fields) {
        this.fields = fields;
    }

    public int getArity() {
        return fields.length;
    }

    public String getString(int pos) {
        return (String) fields[pos];
    }

    public Integer getInt(int pos) {
        return (Integer) fields[pos];
    }

    public Date getDate(int pos) {
        return (Date) fields[pos];
    }

    public Date getTimestamp(int pos) {
        return (Date) fields[pos]; // Assuming timestamp is also represented by Date
    }

    public Boolean getBoolean(int pos) {
        return (Boolean) fields[pos];
    }

    public Double getDouble(int pos) {
        return (Double) fields[pos];
    }

    // Add other field access methods as needed
}

