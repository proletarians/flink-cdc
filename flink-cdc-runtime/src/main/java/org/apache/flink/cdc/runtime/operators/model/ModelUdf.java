package org.apache.flink.cdc.runtime.operators.model;

import org.apache.flink.cdc.common.types.DataType;
import org.apache.flink.cdc.common.types.DataTypes;
import org.apache.flink.cdc.common.udf.UserDefinedFunction;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelUdf implements UserDefinedFunction {

    private static final Logger LOG = LoggerFactory.getLogger(ModelUdf.class);
    private static final String DEFAULT_API_KEY = "sk-WegHEuogRpIyRSwaF5Ce6fE3E62e459dA61eFaF6CcF8C79b";
    private static final String DEFAULT_MODEL_NAME = "gpt-3.5-turbo";
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final String DEFAULT_BASE_URL = "https://api.xty.app/v1/";

    private String name;
    private String host;
    private String apiKey;
    private String modelName;
    private int timeoutSeconds;
    private ChatLanguageModel model;

    public ModelUdf() {
        // Default constructor
    }

    public void configure(String serializedParams) {
        Map<String, String> params = new Gson().fromJson(serializedParams, Map.class);
        this.name = params.get("name");
        this.host = params.getOrDefault("host", DEFAULT_BASE_URL);
        this.apiKey = params.getOrDefault("key", DEFAULT_API_KEY);
        this.modelName = params.getOrDefault("modelName", DEFAULT_MODEL_NAME);
        this.timeoutSeconds = Integer.parseInt(params.getOrDefault("timeoutSeconds", String.valueOf(DEFAULT_TIMEOUT_SECONDS)));
        LOG.info("Configured ModelUdf: {} with host: {}", name, host);
    }

    public String eval(String input) {
        return generateResponse(input);
    }

    public String eval(Integer input) {
        return generateResponse(input.toString());
    }

    public String eval(Double input) {
        return generateResponse(input.toString());
    }

    public String eval(Boolean input) {
        return generateResponse(input.toString());
    }

    // New method to support multiple parameters
    public String eval(Object... inputs) {
        String combinedInput = Stream.of(inputs)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        return generateResponse(combinedInput);
    }

    private String generateResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            LOG.warn("Received empty or null input");
            return "";
        }

        try {
            String response = model.generate(input);
            LOG.debug("Generated response for input: {}", input);
            return response;
        } catch (Exception e) {
            LOG.error("Error generating response for input: {}", input, e);
            return "Error: Unable to generate response";
        }
    }

    @Override
    public DataType getReturnType() {
        return DataTypes.STRING();
    }

    @Override
    public void open() throws Exception {
        LOG.info("Opening ModelUdf: {}", name);
        this.model = OpenAiChatModel.builder()
                .baseUrl(host)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        LOG.info("Initialized ModelUdf: {} with host: {}", name, host);
    }

    @Override
    public void close() throws Exception {
        LOG.info("Closing ModelUdf: {}", name);
        // Any cleanup code can go here
    }
}