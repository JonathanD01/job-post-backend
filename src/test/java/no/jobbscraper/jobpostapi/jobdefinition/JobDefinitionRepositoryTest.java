package no.jobbscraper.jobpostapi.jobdefinition;

import no.jobbscraper.jobpostapi.PostgreSQLContainerInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.spring.sql.QuickPerfSqlConfig;
import org.quickperf.sql.annotation.ExpectSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

@Import(QuickPerfSqlConfig.class)
@DataJpaTest
@DirtiesContext
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
@Sql(value = "/test-data.sql", executionPhase = BEFORE_TEST_CLASS)
@QuickPerfTest
class JobDefinitionRepositoryTest {

    @Autowired
    private JobDefinitionRepository underTest;

    @Test
    @DisplayName("It should find the corresponding definition by lowercase")
    @ExpectSelect
    void findByKeyAndValueLowerCase() {
        // Given
        String key = "location";
        String value = "copenhagen";

        // When
        Optional<JobDefinition> optionalJobDefinition = underTest.findByKeyAndValue(key, value);

        // Then
        assertTrue(optionalJobDefinition.isPresent());
        assertEquals("Location", optionalJobDefinition.get().getKey());
        assertEquals("Copenhagen", optionalJobDefinition.get().getValue());
    }

    @Test
    @DisplayName("It should find the corresponding definition by uppercase")
    @ExpectSelect
    void findByKeyAndValueUpperCase() {
        // Given
        String key = "CATEGORY";
        String value = "MOBILE DEVELOPMENT";

        // When
        Optional<JobDefinition> optionalJobDefinition = underTest.findByKeyAndValue(key, value);

        // Then
        assertTrue(optionalJobDefinition.isPresent());
        assertEquals("Category", optionalJobDefinition.get().getKey());
        assertEquals("Mobile Development", optionalJobDefinition.get().getValue());
    }

    @Test
    @DisplayName("It should find the corresponding definition by both lower- and uppercase")
    @ExpectSelect
    void findByKeyAndValueLowerAndUpperCase() {
        // Given
        String key = "cAtEgOrY";
        String value = "UI/ux dESIGN";

        // When
        Optional<JobDefinition> optionalJobDefinition = underTest.findByKeyAndValue(key, value);

        // Then
        assertTrue(optionalJobDefinition.isPresent());
        assertEquals("Category", optionalJobDefinition.get().getKey());
        assertEquals("UI/UX Design", optionalJobDefinition.get().getValue());
    }
}