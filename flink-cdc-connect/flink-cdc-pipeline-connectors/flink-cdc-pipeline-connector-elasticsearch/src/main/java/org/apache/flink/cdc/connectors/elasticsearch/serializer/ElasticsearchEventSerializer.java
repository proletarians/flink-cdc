package org.apache.flink.cdc.connectors.elasticsearch.serializer;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperationVariant;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.cdc.common.data.RecordData;
import org.apache.flink.cdc.common.event.*;
import org.apache.flink.cdc.common.schema.Column;
import org.apache.flink.cdc.common.schema.Schema;
import org.apache.flink.cdc.common.utils.Preconditions;
import org.apache.flink.cdc.common.utils.SchemaUtils;
import org.apache.flink.connector.base.sink.writer.ElementConverter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchEventSerializer implements ElementConverter<Event, BulkOperationVariant> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<TableId, Schema> schemaMaps = new HashMap<>();

    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    private final ZoneId pipelineZoneId;

    public ElasticsearchEventSerializer(ZoneId zoneId) {
        this.pipelineZoneId = zoneId;
    }

    @Override
    public BulkOperationVariant apply(Event event, SinkWriter.Context context) {
        try {
            if (event instanceof DataChangeEvent) {
                return applyDataChangeEvent((DataChangeEvent) event);
            } else if (event instanceof SchemaChangeEvent) {
                SchemaChangeEvent schemaChangeEvent = (SchemaChangeEvent) event;
                TableId tableId = schemaChangeEvent.tableId();
                if (event instanceof CreateTableEvent) {
                    schemaMaps.put(tableId, ((CreateTableEvent) event).getSchema());
                } else {
                    if (!schemaMaps.containsKey(tableId)) {
                        throw new RuntimeException("Schema of " + tableId + " does not exist.");
                    }
                    schemaMaps.put(
                            tableId,
                            SchemaUtils.applySchemaChangeEvent(
                                    schemaMaps.get(tableId), schemaChangeEvent));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
        return null;
    }

    private BulkOperationVariant applyDataChangeEvent(DataChangeEvent event) throws JsonProcessingException {
        TableId tableId = event.tableId();
        Schema schema = schemaMaps.get(tableId);
        Preconditions.checkNotNull(schema, event.tableId() + " does not exist.");
        Map<String, Object> valueMap;
        OperationType op = event.op();

        // 生成一个唯一的事件 ID
        String id = generateUniqueId(tableId, op);

        switch (op) {
            case INSERT:
            case UPDATE:
            case REPLACE:
                valueMap = serializeRecord(event.after(), schema);
                break;
            case DELETE:
                valueMap = serializeRecord(event.before(), schema);
                valueMap.put("delete", true);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation " + op);
        }

        return new IndexOperation.Builder<>()
                .index(tableId.getTableName())
                .id(id)
                .document(objectMapper.writeValueAsString(valueMap))
                .build();
    }

    private String generateUniqueId(TableId tableId, OperationType op) {
        return tableId.toString() + "-" + op.name();
    }

    public Map<String, Object> serializeRecord(RecordData recordData, Schema schema) {
        List<Column> columns = schema.getColumns();
        Map<String, Object> record = new HashMap<>();
        Preconditions.checkState(
                columns.size() == recordData.getArity(),
                "Column size does not match the data size.");

        for (int i = 0; i < recordData.getArity(); i++) {
            Column column = columns.get(i);
            String columnTypeName = column.getType();
            ElasticsearchRowConverter.SerializationConverter converter =
                    ElasticsearchRowConverter.createNullableExternalConverter(ColumnType.valueOf(columnTypeName), pipelineZoneId);
            Object field = converter.serialize(recordData.get(i));
            record.put(column.getName(), field);
        }
        return record;
    }

    @Override
    public void open(Sink.InitContext context) {
        ElementConverter.super.open(context);
    }
}
