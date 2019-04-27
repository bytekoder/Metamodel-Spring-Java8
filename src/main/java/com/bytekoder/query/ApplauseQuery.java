package com.bytekoder.query;

import com.bytekoder.exception.DeviceNotSupportedException;
import com.bytekoder.exception.TesterNotFoundException;
import com.bytekoder.model.AggregatedBugsModel;
import com.bytekoder.model.TesterModel;
import com.bytekoder.model.TesterResultsModel;
import com.google.common.collect.Sets;
import org.apache.metamodel.MetaModelHelper;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.FunctionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.bytekoder.query.QueryHelper.*;

@Component
public class ApplauseQuery implements IQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplauseQuery.class);

    private TesterModel testerModel;
    private AggregatedBugsModel aggregatedBugsModel;
    private TesterResultsModel testerResultsModel;

    private List<TesterModel> testerList;
    private List<AggregatedBugsModel> intermediateBugsList;
    private List<AggregatedBugsModel> finalBugsList;
    private List<TesterResultsModel> testerResultsList;

    private Map<String, String> deviceMap;
    private Map<String, Long> bugsMap;

    private DataSet devices;
    private DataSet testerData;

    /**
     * Search testers based on a given criteria
     *
     * @param countryFilter a csv list of countryFilter codes
     * @param deviceFilter  a csv list of deviceFilter names
     * @return {@link Optional<List<TesterResultsModel>>}
     */
    @Override
    public Optional<List<TesterResultsModel>> searchTesters(final Optional<String> countryFilter,
                                                            final Optional<String> deviceFilter) {

        DataSet bugsByDevice;
        DataSet bugsByTester;
        deviceMap = new HashMap<>();

        assert deviceFilter.isPresent();
        assert countryFilter.isPresent();

        if (!deviceFilter.get().equalsIgnoreCase(ALL_FILTER)) {
            devices = getDeviceById(deviceFilter);
        } else {
            devices = getAllDevices();
        }

        while (devices.next()) {
            Row row = devices.getRow();
            String id = (String) row.getValue(0);
            String name = (String) row.getValue(1);
            deviceMap.put(id, name);
        }

        if (deviceMap.size() == 0) {
            LOGGER.error("Device {} not found", deviceFilter.get());
            throw new DeviceNotSupportedException(deviceFilter.get());
        }

        if (countryFilter.get().equalsIgnoreCase(ALL_FILTER)) {
            testerData = getAllTesters.get().execute();
        } else {
            testerData = filterByCountry.apply(getAllTesters, countryFilter).execute();
        }

        bugsByDevice = getBugsByDevice();
        bugsByTester = getBugsByTester();

        dataMapper(testerData, bugsByTester, bugsByDevice);

        // Check for invalid country codes
        if (!countryFilter.get().equalsIgnoreCase(ALL_FILTER)) {
            Set<String> countries = Sets.newHashSet(parseFilters(countryFilter).split(","));

            Set<String> allowedCountries = new HashSet<>();
            for (TesterModel m : testerList) {
                allowedCountries.add(m.getCountry());
            }

            if (allowedCountries.stream().noneMatch(countries::contains)) {
                LOGGER.error("Tester in country: {} with device: {} not found", countryFilter.get(), deviceFilter.get());
                throw new TesterNotFoundException(deviceFilter.get(), countryFilter.get());
            }
        }

        finalBugsList = new ArrayList<>();
        testerResultsList = new ArrayList<>();

        if (!deviceFilter.get().equalsIgnoreCase(ALL_FILTER)) {
            for (AggregatedBugsModel list : intermediateBugsList) {
                for (Map.Entry<String, String> map : deviceMap.entrySet()) {
                    if (list.getDeviceId().trim().equals(map.getKey().trim())) {
                        finalBugsList.add(list);
                    }
                }
            }

            for (AggregatedBugsModel bugsList : finalBugsList) {
                for (TesterModel testerList : testerList) {
                    if (testerList.getTesterId().equals(bugsList.getTesterId())) {
                        testerResultsModel = new TesterResultsModel();
                        testerResultsModel.setName(testerList.getFirstName().concat(" ").concat(testerList.getLastName()));
                        testerResultsModel.setCountry(testerList.getCountry());
                        testerResultsModel.setBugs(bugsList.getBugsByTesterPerDevice());
                        testerResultsModel.setDevice(bugsList.getDevice());
                        testerResultsList.add(testerResultsModel);
                    }
                }
            }


        } else {

            for (Map.Entry<String, Long> map : bugsMap.entrySet()) {
                for (TesterModel list : testerList) {
                    if (list.getTesterId().equals(map.getKey())) {
                        testerResultsModel = new TesterResultsModel();
                        testerResultsModel.setName(list.getFirstName().concat(" ").concat(list.getLastName()));
                        testerResultsModel.setCountry(list.getCountry());
                        testerResultsModel.setBugs(map.getValue());
                        testerResultsList.add(testerResultsModel);
                    }
                }
            }
        }
        testerResultsList.sort((a, b) -> a.getBugs() > b.getBugs() ? -1 : a.getBugs() == b.getBugs() ? 0 : 1);
        return Optional.of(testerResultsList);
    }

    /**
     * Get device by id
     *
     * @param device a testing device
     * @return {@link DataSet}
     */
    private DataSet getDeviceById(final Optional<String> device) {

        return MetaModelHelper.getDistinct(ApplauseDataSetup.deviceDataCtx.query()
                .from(ApplauseDataSetup.device)
                .select(DEVICEID_COL, DESC_COL)
                .where(DESC_COL).in(parseFilters(device).split(","))
                .execute());
    }

    /**
     * Get all devices irrespective of any filters
     *
     * @return {@link DataSet}
     */
    private DataSet getAllDevices() {

        return MetaModelHelper.getDistinct(ApplauseDataSetup.deviceDataCtx.query()
                .from(ApplauseDataSetup.device)
                .select(DEVICEID_COL, DESC_COL)
                .execute());
    }


    /**
     * Get bugs filed through each device
     *
     * @return {@link DataSet}
     */
    private DataSet getBugsByDevice() {

        return ApplauseDataSetup.bugsDataCtx.query()
                .from(ApplauseDataSetup.bugs)
                .select(TESTERID_COL)
                .select(DEVICEID_COL)
                .select(FunctionType.COUNT, ApplauseDataSetup.bugs.getColumnByName(BUGID_COL))
                .groupBy(TESTERID_COL)
                .groupBy(DEVICEID_COL)
                .execute();
    }

    /**
     * Get bugs filed by each tester
     *
     * @return {@link DataSet}
     */
    private DataSet getBugsByTester() {

        return ApplauseDataSetup.bugsDataCtx.query()
                .from(ApplauseDataSetup.bugs)
                .select(TESTERID_COL).selectCount()
                .groupBy(TESTERID_COL)
                .orderBy(FunctionType.COUNT, ApplauseDataSetup.bugs.getColumnByName(TESTERID_COL)).desc()
                .execute();
    }

    /**
     * Utility method to map the dataset to respective model classes
     *
     * @param testerData   dataset containing data about testers
     * @param bugsByTester dataset containing bugs filed by a tester
     * @param bugsByDevice dataset containing bugs filed per device type
     */
    private void dataMapper(final DataSet testerData, final DataSet bugsByTester, final DataSet bugsByDevice) {

        testerList = new ArrayList<>();
        intermediateBugsList = new ArrayList<>();
        bugsMap = new HashMap<>();

        while (testerData.next()) {
            testerModel = new TesterModel();
            Row row = testerData.getRow();
            testerModel.setTesterId((String) row.getValue(0));
            testerModel.setFirstName(((String) row.getValue(1)));
            testerModel.setLastName((String) row.getValue(2));
            testerModel.setCountry((String) row.getValue(3));
            testerList.add(testerModel);
        }

        while (bugsByTester.next()) {
            Row row = bugsByTester.getRow();
            bugsMap.put((String) row.getValue(0), (long) row.getValue(1));
        }

        while (bugsByDevice.next()) {
            aggregatedBugsModel = new AggregatedBugsModel();
            Row row = bugsByDevice.getRow();
            aggregatedBugsModel.setTesterId((String) row.getValue(0));
            aggregatedBugsModel.setDeviceId((String) row.getValue(1));
            aggregatedBugsModel.setBugsByTesterPerDevice((long) row.getValue(2));
            aggregatedBugsModel.setDevice(deviceMap.get(row.getValue(1)));
            intermediateBugsList.add(aggregatedBugsModel);
        }
    }
}


