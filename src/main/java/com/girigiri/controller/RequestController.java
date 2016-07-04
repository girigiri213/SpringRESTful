package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.CustomerRepository;
import com.girigiri.dao.services.RequestRepository;
import com.mysql.fabric.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 6/28/16.
 * Custom RestController for {@link Request}
 */
@RestController
@RequestMapping(value = "/api/requests")
public class RequestController extends BaseController {


    private final RequestRepository requestRepository;


    private final CustomerRepository customerRepository;


    @Autowired
    public RequestController(RequestRepository requestRepository, CustomerRepository customerRepository) {
        this.requestRepository = requestRepository;
        this.customerRepository = customerRepository;
    }


    /**
     * Get all requests in /api/requests
     *
     * @return the {@link ResponseEntity} of all requests, <b>200 OK</b> is also returned if success
     */
    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getRequests() {
        Iterable<Request> iterable = requestRepository.findAll();
        for (Request request : iterable) {
            request.set_links(linkTo(methodOn(RequestController.class).getRequest(request.getId())).withSelfRel());
        }
        Resources<Request> resources = new Resources<>(iterable);
        return ResponseEntity.ok(resources);
    }

    /**
     * Return all requests in pages
     *
     * @param page the number of current page, each page's size is {@link BaseController#DEFAULT_PAGE_SIZE},
     *             note that page starts from <b>0</b>, not <b>1</b>
     * @return Current page of requests, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getRequests(@RequestParam(value = "pages") int page) {
        return getRequestsInPage(page, DEFAULT_PAGE_SIZE, "id");
    }

    /**
     * Return all requests with particular size
     *
     * @param size the size of query
     * @return Current page of requests, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getRequestsBySize(@RequestParam(value = "size") int size) {
        return getRequestsInPage(0, size, "id");
    }


    /**
     * Get requests in current page and in given sort order
     *
     * @param page the current page, default page's size is {@link BaseController#DEFAULT_PAGE_SIZE}
     * @param sort sort order
     * @return requests in json and <b>200 OK</b>, note that device json is included
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getRequestsByPageAndSort(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        return getRequestsInPage(page, DEFAULT_PAGE_SIZE, sort);
    }


    private ResponseEntity<?> getRequestsInPage(int page, int size, String sort) {
        Page<Request> pages = requestRepository.findAll(new PageRequest(page, size, new Sort(sort)));
        pages.forEach(request -> request.set_links(linkTo(methodOn(RequestController.class).getRequest(request.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resource<>(pages));
    }


    /**
     * Get one request by id
     *
     * @param id the request id
     * @return the request json and <b>200 OK</b> if request exists, <b>404 NOT FOUND</b> if request
     * doesn't exist.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getRequest(@PathVariable Long id) {
        validateRequest(id);
        Request request = requestRepository.findOne(id);
        request.set_links(linkTo(methodOn(RequestController.class).getRequest(id)).withSelfRel());
        return new ResponseEntity<>(new Resource<>(request), HttpStatus.OK);
    }

    /**
     * Save a request using {@link RequestMethod}.POST method
     *
     * @param request the json posted, it will be converted to POJO
     * @return <b>201 Created</b> if created success
     */
    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> save(@RequestBody Request request) {
        if (request.getRepairHistory() == null) {
            request.setRepairHistory(new RepairHistory());
        }
        validateCustomerInRequest(request.getCusId());
        Request rst = requestRepository.save(request);
        Resource<Request> resources = new Resource<>(rst);
        resources.add(linkTo(methodOn(RequestController.class).getRequest(rst.getId())).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.CREATED);
    }


    /**
     * Delete a customer in {@link RequestMethod}.DELETE method
     *
     * @param id the request id
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        validateRequest(id);
        requestRepository.delete(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    /**
     * Update a existed request using {@link RequestMethod}.PUT method,
     * PATCH method is not allowed here currently
     *
     * @param id      the customer id
     * @param request the updating request, formatted in json
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody Request request) {
        validateRequest(id);
        validateCustomerInRequest(request.getCusId());
        Request rst = requestRepository.findOne(id);
        compareAndUpdate(rst, request);
        requestRepository.save(rst);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    @RequestMapping(value = "/searchRequest", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> search(@RequestParam(value = "cusId", required = false) String cusId,
                             @RequestParam(value = "low", required = false) String low,
                             @RequestParam(value = "high", required = false) String high) {
        List<Request> list;
        long lowerBound = Long.MIN_VALUE;
        long upperBound = Long.MAX_VALUE;
        if (low != null && !low.equals("")) lowerBound = Long.parseLong(low);
        if (high != null && !high.equals("")) upperBound = Long.parseLong(high);
        long finalUpperBound = upperBound;
        long finalLowerBound = lowerBound;

        if (cusId == null || cusId.equals("")) {
            list = (List<Request>) requestRepository.findAll();
        } else {
            list = requestRepository.findByCusId(Long.parseLong(cusId));
        }
        List<Request> rst = list.stream().filter(request -> (request.getCreated() <= finalUpperBound && request.getCreated() >= finalLowerBound))
                .collect(toList());
        rst.forEach(request -> request.set_links(linkTo(methodOn(RequestController.class).getRequest(request.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resources<>(rst));
    }


    private void validateRequest(long id) {
        if (!requestRepository.exists(id)) {
            throw new RestUtils.RequestNotFoundException(id);
        }
    }

    private void validateCustomerInRequest(long id) {
        if (!customerRepository.exists(id)) {
            throw new RestUtils.InvalidCustomerException(id);
        }
    }


    private void compareAndUpdate(Request before, Request after) {
        //// FIXME: 7/5/16 Appearance in device
        before.setDevice(after.getDevice());
        before.setState(after.getState());
        before.setPredictPrice(after.getPredictPrice());
        before.setPrice(after.getPrice());
        before.setPredictTime(after.getPredictTime());
        before.setRepairHistory(after.getRepairHistory());
        before.setTime(after.getTime());
    }


}
