package no.jobbscraper.jobpostapi.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import no.jobbscraper.jobpostapi.jobdefinition.JobDefinition;

import java.io.IOException;
import java.util.*;

public class CustomJobDefinitionDeserializer extends StdDeserializer<Set<JobDefinition>> {

    public CustomJobDefinitionDeserializer() {
        this(null);
    }

    public CustomJobDefinitionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Set<JobDefinition> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode root = mapper.readTree(jsonParser);

        Set<JobDefinition> jobDefinitions = new HashSet<>();

        root.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();

            if (valueNode.isArray()) {
                for (JsonNode node : valueNode) {
                    jobDefinitions.add(new JobDefinition(key, node.textValue()));
                }
            }
        });
        return jobDefinitions;
    }
}