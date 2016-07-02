package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.controller.utils.ViolationError;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.CustomerRepository;
import com.girigiri.dao.services.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 6/28/16.
 * Custom RestController for {@link Request}
 */
@RestController
@RequestMapping(value = "/api/requests")
@PreAuthorize("hasRole('CUSTOMER_SERVICE')")
public class RequestController {


    private final RequestRepository requestRepository;


    private final CustomerRepository customerRepository;


    @Autowired
    public RequestController(RequestRepository requestRepository, CustomerRepository customerRepository) {
        this.requestRepository = requestRepository;
        this.customerRepository = customerRepository;
    }



    /**
     * Get all requests in /api/requests
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
     * Save a request using {@link RequestMethod}.POST method
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
     * Return all requests in pages
     * @param page the number of current page, each page's size is 5
     * @return Current page of requests, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getRequests(@RequestParam(value = "pages") int page) {
        Page<Request> pages = requestRepository.findAll(new PageRequest(page, 5, new Sort("id")));
        Resources<Request> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(RequestController.class).getRequests()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }


    /**
     * Get one request by id
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
     * Return all requests with particular size
     *
     * @param size the size of query
     * @return Current page of requests, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getRequestsBySize(@RequestParam(value = "size") int size) {
        Page<Request> pages = requestRepository.findAll(new PageRequest(1, size, new Sort("id")));
        Resources<Request> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(RequestController.class).getRequestsBySize(size)).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }


    /**
     * Get requests in current page and in given sort order
     * @param page the current page
     * @param sort sort order
     * @return requests in json and <b>200 OK</b>, note that device json is included
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getRequests(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        Page<Request> pages = requestRepository.findAll(new PageRequest(page, 20, new Sort(sort)));
        Resources<Request> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(RequestController.class).getRequests()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * Delete a customer in {@link RequestMethod}.DELETE method
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
     * @param id the customer id
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


    void validateRequest(long id) {
        if (!requestRepository.exists(id)) {
            throw new RequestNotFoundException(id);
        }
    }

    void validateCustomerInRequest(long id) {
        if (!customerRepository.exists(id)) {
            throw new InvalidCustomerException(id);
        }
    }




    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private static class InvalidCustomerException extends RuntimeException {
        InvalidCustomerException(long customerId) {
            super("Can not find customerId " + customerId);
        }
    }

    private void compareAndUpdate(Request before, Request after) {
        before.setDevice(after.getDevice());
        before.setState(after.getState());
        before.setPredictPrice(after.getPredictPrice());
        before.setPrice(after.getPrice());
        before.setRepairHistory(after.getRepairHistory());
        before.setTime(after.getTime());
    }





    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> processFieldErrors(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> set = exception.getConstraintViolations();
        Resource<ViolationError> resource = new Resource<>(RestUtils.generateError(set));
        return new ResponseEntity<>(resource, HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class RequestNotFoundException extends RuntimeException {
        RequestNotFoundException(long customerId) {
            super("could not find request '" + customerId + "'.");
        }
    }


}
