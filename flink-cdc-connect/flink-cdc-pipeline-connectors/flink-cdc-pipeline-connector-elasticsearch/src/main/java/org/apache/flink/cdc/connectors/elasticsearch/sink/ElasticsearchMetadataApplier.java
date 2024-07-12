package org.apache.flink.cdc.connectors.elasticsearch.sink;

import org.apache.flink.cdc.common.event.AddColumnEvent;
import org.apache.flink.cdc.common.event.AlterColumnTypeEvent;
import org.apache.flink.cdc.common.event.CreateTableEvent;
import org.apache.flink.cdc.common.event.DropColumnEvent;
import org.apache.flink.cdc.common.event.RenameColumnEvent;
import org.apache.flink.cdc.common.event.SchemaChangeEvent;
import org.apache.flink.cdc.common.sink.MetadataApplier;
import org.apache.flink.cdc.common.configuration.Configuration;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

public class ElasticsearchMetadataApplier implements MetadataApplier {

    private final ElasticsearchDataSinkOptions options;
    private final Configuration configuration;
    private final RestClient client;

    public ElasticsearchMetadataApplier(ElasticsearchDataSinkOptions options, Configuration configuration) {
        this.options = options;
        this.configuration = configuration;
        this.client = createRestClient();
    }

    private RestClient createRestClient() {
        return RestClient.builder(HttpHost.create(options.getClusterAddress())).build();
    }

    @Override
    public void applyMetadata() {
        try {
            createIndexIfNotExists(options.getIndexName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to apply metadata to Elasticsearch", e);
        }
    }

    private void createIndexIfNotExists(String indexName) throws IOException {
        Request request = new Request("HEAD", "/" + indexName);
        Response response = client.performRequest(request);

        if (response.getStatusLine().getStatusCode() == 404) {
            // Index does not exist, create it
            String createIndexJson = "{ \"settings\": { \"number_of_shards\": 1, \"number_of_replicas\": 1 } }";
            Request createIndexRequest = new Request("PUT", "/" + indexName);
            createIndexRequest.setJsonEntity(createIndexJson);
            client.performRequest(createIndexRequest);
        }
    }

    public void close() throws IOException {
        client.close();
    }

    @Override
    public void applySchemaChange(SchemaChangeEvent schemaChangeEvent) {
        try {
            if (schemaChangeEvent instanceof CreateTableEvent) {
                applyCreateTable((CreateTableEvent) schemaChangeEvent);
            } else if (schemaChangeEvent instanceof AddColumnEvent) {
                applyAddColumn((AddColumnEvent) schemaChangeEvent);
            } else if (schemaChangeEvent instanceof DropColumnEvent) {
                applyDropColumn((DropColumnEvent) schemaChangeEvent);
            } else if (schemaChangeEvent instanceof RenameColumnEvent) {
                applyRenameColumn((RenameColumnEvent) schemaChangeEvent);
            } else if (schemaChangeEvent instanceof AlterColumnTypeEvent) {
                applyAlterColumn((AlterColumnTypeEvent) schemaChangeEvent);
            } else {
                throw new UnsupportedOperationException(
                        "ElasticsearchMetadataApplier doesn't support schema change event " + schemaChangeEvent);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to apply schema change to Elasticsearch", e);
        }
    }

    private void applyCreateTable(CreateTableEvent event) throws IOException {
        String indexName = event.tableId().getTableName();
        createIndexIfNotExists(indexName);
    }

    private void applyAddColumn(AddColumnEvent event) throws IOException {
        for (AddColumnEvent.ColumnWithPosition columnWithPosition : event.getAddedColumns()) {
            String addFieldJson = "{ \"properties\": { \"" + columnWithPosition.getAddColumn().getName() + "\": { \"type\": \"" + mapColumnType(columnWithPosition.getAddColumn().getType()) + "\" } } }";
            Request addFieldRequest = new Request("PUT", "/" + event.tableId().getTableName() + "/_mapping");
            addFieldRequest.setJsonEntity(addFieldJson);
            client.performRequest(addFieldRequest);
        }
    }

    private void applyDropColumn(DropColumnEvent event) throws IOException {
        // Elasticsearch 不直接支持删除字段，可以使用索引重建策略来删除字段
        throw new UnsupportedOperationException("Elasticsearch does not support dropping columns directly");
    }

    private void applyRenameColumn(RenameColumnEvent event) throws IOException {
        // Elasticsearch 不直接支持重命名字段，可以使用索引重建策略来重命名字段
        throw new UnsupportedOperationException("Elasticsearch does not support renaming columns directly");
    }

    private void applyAlterColumn(AlterColumnTypeEvent event) throws IOException {
        // Elasticsearch 不直接支持更改字段类型，可以使用索引重建策略来更改字段类型
        throw new UnsupportedOperationException("Elasticsearch does not support altering column types directly");
    }

    private String mapColumnType(String columnType) {
        switch (columnType.toLowerCase()) {
            case "string":
                return "text";
            case "int":
            case "integer":
                return "integer";
            case "boolean":
                return "boolean";
            case "double":
                return "double";
            case "date":
                return "date";
            default:
                throw new IllegalArgumentException("Unsupported column type: " + columnType);
        }
    }
}
