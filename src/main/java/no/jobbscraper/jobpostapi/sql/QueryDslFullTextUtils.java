package no.jobbscraper.jobpostapi.sql;

import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;

public class QueryDslFullTextUtils {

    public static BooleanTemplate fullTextMatch(StringExpression field, String query) {
        return Expressions.booleanTemplate("ftsMatch({0}, {1})", field, query);
    }
}
