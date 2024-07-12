package org.apache.flink.cdc.connectors.elasticsearch.serializer;



import java.time.ZoneId;

public class ElasticsearchRowConverter {

    public static SerializationConverter createNullableExternalConverter(ColumnType columnType, ZoneId zoneId) {
        return null;
    }

    @FunctionalInterface
    public interface SerializationConverter {
        Object serialize(int pos, RecordData data);

        Object serialize(Object o);
    }
}
