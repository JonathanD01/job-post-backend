package no.jobbscraper.jobpostapi.sql;


import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;


public class PostgresFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        BasicType<Boolean> resolveType = functionContributions
            .getTypeConfiguration()
            .getBasicTypeRegistry()
            .resolve(StandardBasicTypes.BOOLEAN);

        functionContributions
            .getFunctionRegistry()
            .registerPattern("ftsMatch","?1 @@ websearch_to_tsquery(?2)",resolveType);
    }
}
