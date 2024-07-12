package org.apache.flink.cdc.connectors.elasticsearch.serializer;

public enum ColumnType {
    STRING,
    INT,
    DATE,
    TIMESTAMP,
    BOOLEAN,
    DOUBLE,
    // 其他数据类型
}
