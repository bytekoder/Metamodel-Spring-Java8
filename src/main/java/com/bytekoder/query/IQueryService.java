package com.bytekoder.query;


import com.bytekoder.model.TesterResultsModel;

import java.util.List;
import java.util.Optional;

public interface IQueryService {

    /**
     * Search a tester/s by device/s and country/s
     * @param countryFilter a csv list of country codes
     * @param deviceFilter a csv list of device names
     * @return {@link Optional<List<TesterResultsModel>>}
     */
    Optional<List<TesterResultsModel>> searchTesters(final Optional<String> countryFilter, final Optional<String> deviceFilter);
}
