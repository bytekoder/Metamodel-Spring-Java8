package com.bytekoder.query;

import org.apache.metamodel.query.builder.SatisfiedSelectBuilder;
import org.apache.metamodel.query.builder.SatisfiedWhereBuilder;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;


class QueryHelper {

    public static final String TESTERID_COL = "testerId";
    public static final String DEVICEID_COL = "deviceId";
    public static final String BUGID_COL = "bugId";
    private static final String FIRSTNAME_COL = "firstName";
    private static final String LASTNAME_COL = "lastName";
    private static final String COUNTRY_COL = "country";
    public static final String DESC_COL = "description";


    public static final String ALL_FILTER = "ALL";


    static final Supplier<SatisfiedSelectBuilder> getAllTesters = () ->

            ApplauseDataSetup.testerDataCtx.query().from(ApplauseDataSetup.testers)
                    .select(TESTERID_COL, FIRSTNAME_COL, LASTNAME_COL, COUNTRY_COL);

    static final BiFunction<Supplier<SatisfiedSelectBuilder>, Optional<String>, SatisfiedWhereBuilder>
            filterByCountry = (satisfiedSelectBuilder, country) ->

            ((SatisfiedWhereBuilder) satisfiedSelectBuilder.get().where(COUNTRY_COL)
                    .in(parseFilters(country).split(",")));


    /**
     * Parses csv filters so that space between commas can be treated as a fair input
     *
     * @param filter a matcher provided by the user
     * @return {@code String}
     */
    public static String parseFilters(final Optional<String> filter) {

        return filter.get().replaceAll(",\\s+", ",");
    }
}
