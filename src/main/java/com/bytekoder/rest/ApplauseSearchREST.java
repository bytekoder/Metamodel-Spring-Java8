package com.bytekoder.rest;

import com.bytekoder.exception.DeviceNotSupportedException;
import com.bytekoder.exception.TesterNotFoundException;
import com.bytekoder.model.TesterResultsModel;
import com.bytekoder.query.IQueryService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value = "/search")
public class ApplauseSearchREST {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplauseSearchREST.class);

    private final IQueryService IQueryService;

    @Autowired
    public ApplauseSearchREST(IQueryService IQueryService) {
        this.IQueryService = IQueryService;
    }


    /**
     * Search testers either with or without filters.
     * When no explicit filters are supplied, it searches for ALL countries and ALL devices
     *
     * @param country a csv list of country codes
     * @param device  a csv list of device names
     * @return {@code Response}
     */
    @ApiOperation(value = "searchTesters", nickname = "searchTesters")
    @RequestMapping(method = RequestMethod.GET, path = "/testers", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "country", value = "country code", dataType = "string", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "device", value = "device name", dataType = "string", paramType = "query", defaultValue = "ALL")

    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = TesterResultsModel.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public Response searchTesters(@RequestParam(value = "country", defaultValue = "ALL") final Optional<String> country,
                                  @RequestParam(value = "device", defaultValue = "ALL") final Optional<String> device) {

        LOGGER.info("Searching for testers with Country: {}, Device: {}", country.get(), device.get());

        final Optional<List<TesterResultsModel>> results;

        try {
            results = IQueryService.searchTesters(country, device);
        } catch (DeviceNotSupportedException | TesterNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
        if (results.isPresent()) {
            return Response.status(Response.Status.OK).entity(results.get()).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
