package org.apache.flink.cdc.connectors.elasticsearch.config;

import java.io.Serializable;
import java.util.List;

import org.apache.flink.cdc.connectors.elasticsearch.v2.NetworkConfig;
import org.apache.http.HttpHost;

public class ElasticsearchSinkOptions implements Serializable {

    private final int maxBatchSize;
    private final int maxInFlightRequests;
    private final int maxBufferedRequests;
    private final long maxBatchSizeInBytes;
    private final long maxTimeInBufferMS;
    private final long maxRecordSizeInBytes;
    private final NetworkConfig networkConfig;

    public ElasticsearchSinkOptions(
            int maxBatchSize,
            int maxInFlightRequests,
            int maxBufferedRequests,
            long maxBatchSizeInBytes,
            long maxTimeInBufferMS,
            long maxRecordSizeInBytes,
            NetworkConfig networkConfig) {
        this.maxBatchSize = maxBatchSize;
        this.maxInFlightRequests = maxInFlightRequests;
        this.maxBufferedRequests = maxBufferedRequests;
        this.maxBatchSizeInBytes = maxBatchSizeInBytes;
        this.maxTimeInBufferMS = maxTimeInBufferMS;
        this.maxRecordSizeInBytes = maxRecordSizeInBytes;
        this.networkConfig = networkConfig;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public int getMaxInFlightRequests() {
        return maxInFlightRequests;
    }

    public int getMaxBufferedRequests() {
        return maxBufferedRequests;
    }

    public long getMaxBatchSizeInBytes() {
        return maxBatchSizeInBytes;
    }

    public long getMaxTimeInBufferMS() {
        return maxTimeInBufferMS;
    }

    public long getMaxRecordSizeInBytes() {
        return maxRecordSizeInBytes;
    }

    public NetworkConfig getNetworkConfig() {
        return networkConfig;
    }

    public List<HttpHost> getHosts() {
        return networkConfig.getHosts();
    }
}
