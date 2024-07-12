package org.apache.flink.cdc.connectors.elasticsearch.sink;

import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.cdc.common.configuration.Configuration;
import org.apache.flink.cdc.common.sink.DataSink;
import org.apache.flink.cdc.common.sink.EventSinkProvider;
import org.apache.flink.cdc.common.sink.FlinkSinkProvider;
import org.apache.flink.cdc.common.sink.MetadataApplier;
import org.apache.flink.cdc.connectors.elasticsearch.v2.Elasticsearch8AsyncSinkBuilder;
import org.apache.flink.cdc.connectors.elasticsearch.v2.Operation;
import org.apache.flink.cdc.connectors.elasticsearch.config.ElasticsearchSinkOptions;
import org.apache.flink.cdc.connectors.elasticsearch.serializer.ElasticsearchEventSerializer;
import org.apache.flink.connector.base.sink.writer.ElementConverter;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperationVariant;
import org.apache.http.HttpHost;

import java.io.Serializable;
import java.time.ZoneId;

/** A {@link DataSink} for Elasticsearch connector. */
public class ElasticsearchDataSink<InputT> implements DataSink, Serializable {

    private final ElasticsearchSinkOptions elasticsearchOptions;
    private final Configuration configuration;
    private final ZoneId zoneId;

    public ElasticsearchDataSink(
            ElasticsearchSinkOptions elasticsearchOptions,
            Configuration configuration,
            ZoneId zoneId) {
        this.elasticsearchOptions = elasticsearchOptions;
        this.configuration = configuration;
        this.zoneId = zoneId;
    }

    @Override
    public EventSinkProvider getEventSinkProvider() {
        return FlinkSinkProvider.of(
                new Elasticsearch8AsyncSinkBuilder<InputT>()
                        .setHosts(elasticsearchOptions.getHosts().toArray(new HttpHost[0]))
                        .setElementConverter(new ElasticsearchEventSerializer(ZoneId.systemDefault()))
                        .setMaxBatchSize(elasticsearchOptions.getMaxBatchSize())
                        .setMaxInFlightRequests(elasticsearchOptions.getMaxInFlightRequests())
                        .setMaxBufferedRequests(elasticsearchOptions.getMaxBufferedRequests())
                        .setMaxBatchSizeInBytes(elasticsearchOptions.getMaxBatchSizeInBytes())
                        .setMaxTimeInBufferMS(elasticsearchOptions.getMaxTimeInBufferMS())
                        .setMaxRecordSizeInBytes(elasticsearchOptions.getMaxRecordSizeInBytes())
                        .build());
    }

    @Override
    public MetadataApplier getMetadataApplier() {
        return schemaChangeEvent -> {};
    }
}
