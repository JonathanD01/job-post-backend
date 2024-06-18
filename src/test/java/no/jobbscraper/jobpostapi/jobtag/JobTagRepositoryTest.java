package no.jobbscraper.jobpostapi.jobtag;

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
@ActiveProfiles("test")
@DirtiesContext
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
@Sql(value = "/test-data.sql", executionPhase = BEFORE_TEST_CLASS)
@QuickPerfTest
class JobTagRepositoryTest {

    @Autowired
    private JobTagRepository underTest;

    @Test
    @DisplayName("It should find the corresponding tag by lowercase")
    @ExpectSelect
    void findByTagLowerCase() {
        // Given
        String tagName = "java";

        // When
        Optional<JobTag> jobTagOptional = underTest.findByTag(tagName);

        // Then
        assertTrue(jobTagOptional.isPresent());
        assertEquals("Java", jobTagOptional.get().getTag());
    }

    @Test
    @DisplayName("It should find the corresponding tag by uppercase")
    @ExpectSelect
    void findByTagUpperCase() {
        // Given
        String tagName = "PYTHON";

        // When
        Optional<JobTag> jobTagOptional = underTest.findByTag(tagName);

        // Then
        assertTrue(jobTagOptional.isPresent());
        assertEquals("Python", jobTagOptional.get().getTag());
    }

    @Test
    @DisplayName("It should find the corresponding tag by both upper- and lowercase")
    @ExpectSelect
    void findByTagBothLowerAndUpperCase() {
        // Given
        String tagName = "rEaCt";

        // When
        Optional<JobTag> jobTagOptional = underTest.findByTag(tagName);

        // Then
        assertTrue(jobTagOptional.isPresent());
        assertEquals("React", jobTagOptional.get().getTag());
    }
}