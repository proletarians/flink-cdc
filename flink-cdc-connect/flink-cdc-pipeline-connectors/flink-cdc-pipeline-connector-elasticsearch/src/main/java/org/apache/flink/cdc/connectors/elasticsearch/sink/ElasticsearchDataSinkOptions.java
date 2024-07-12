package org.apache.flink.cdc.connectors.elasticsearch.sink;

import java.util.Objects;

public class ElasticsearchDataSinkOptions{

    private static final long serialVersionUID = 1L;

    private final String clusterAddress;
    private final String indexName;
    private final String username;
    private final String password;
    private final boolean useSSL;
    private final int batchSize;
    private final int flushIntervalMillis;

    private ElasticsearchDataSinkOptions(Builder builder, String password) {
        this.clusterAddress = Objects.requireNonNull(builder.clusterAddress, "clusterAddress");
        this.indexName = Objects.requireNonNull(builder.indexName, "indexName");
        this.username = builder.username;
        this.password = password;
        this.useSSL = builder.useSSL;
        this.batchSize = builder.batchSize;
        this.flushIntervalMillis = builder.flushIntervalMillis;
    }

    public String getClusterAddress() {
        return clusterAddress;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean useSSL() {
        return useSSL;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getFlushIntervalMillis() {
        return flushIntervalMillis;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String clusterAddress;
        private String indexName;
        private String username;
        private String password;
        private boolean useSSL = false;
        private int batchSize = 1000;
        private int flushIntervalMillis = 10000;

        private Builder() {
        }

        public Builder clusterAddress(String clusterAddress) {
            this.clusterAddress = clusterAddress;
            return this;
        }

        public Builder indexName(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder useSSL(boolean useSSL) {
            this.useSSL = useSSL;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder flushIntervalMillis(int flushIntervalMillis) {
            this.flushIntervalMillis = flushIntervalMillis;
            return this;
        }

    }
}
