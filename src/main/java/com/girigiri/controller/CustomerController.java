package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.controller.utils.ViolationError;
import com.girigiri.dao.models.Customer;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 6/28/16.
 * Custom RestController for {@link com.girigiri.dao.models.Customer}
 */
@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, RequestRepository requestRepository) {
        this.customerRepository = customerRepository;
        this.requestRepository = requestRepository;
    }


    /**
     * Get all customers in /api/customers
     * @return the {@link ResponseEntity} of all customers, <b>200 OK</b> is also returned if success
     */
    @RequestMapping(value = "/api/customers", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getCustomers() {
        Iterable<Customer> iterable = customerRepository.findAll();
        Iterator<Customer> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            Customer customer = iterator.next();
            customer.set_links(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel());
        }
        Resources<Customer> resources = new Resources<>(iterable);
        return ResponseEntity.ok(resources);
    }

    /**
     * Save a customer using {@link RequestMethod}.POST method
     * @param customer the json posted, it will be converted to POJO
     * @return <b>201 Created</b> if created success
     */
    @RequestMapping(value = "/api/customers", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> save(@RequestBody Customer customer) {
        Resource<Customer> resources = new Resource<>(customerRepository.save(customer));
        resources.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.CREATED);
    }


    /**
     * Return all customers in pages
     * @param page the number of current page, each page's size is 5
     * @return Current page of customers, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "/api/customers", method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getCustomers(@RequestParam(value = "pages") int page) {
        Page<Customer> pages = customerRepository.findAll(new PageRequest(page, 5, new Sort("id")));
        Resources<Customer> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * Return all customers with particular size
     *
     * @param size the size of query
     * @return Current page of customers, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "/api/customers", method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getCustomersBySize(@RequestParam(value = "size") int size) {
        Page<Customer> pages = customerRepository.findAll(new PageRequest(1, size, new Sort("id")));
        Resources<Customer> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(CustomerController.class).getCustomersBySize(size)).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }


    /**
     * Get one customer by id
     * @param id the customer id
     * @return the customer json and <b>200 OK</b> if customer exists, <b>404 NOT FOUND</b> if customer
     * doesn't exist.
     */
    @RequestMapping(value = "/api/customers/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getCustomer(@PathVariable Long id) {
        validateCustomer(id);
        Customer customer = customerRepository.findOne(id);
        customer.set_links(linkTo(methodOn(CustomerController.class).getCustomer(id)).withSelfRel());
        return ResponseEntity.ok(new Resource<>(customer));
    }


    /**
     * Get customers in current page and in given sort order
     * @param page the current page
     * @param sort sort order
     * @return customers in json and <b>200 OK</b>
     */
    @RequestMapping(value = "/api/customers", method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getCustomers(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        Page<Customer> pages = customerRepository.findAll(new PageRequest(page, 20, new Sort(sort)));
        Resources<Customer> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * Delete a customer in {@link RequestMethod}.DELETE method
     * @param id the customer id
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/api/customers/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        validateCustomer(id);
        requestRepository.removeByCusId(id);
        customerRepository.delete(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    /**
     * Update a existed customer using {@link RequestMethod}.PUT method,
     * PATCH method is not allowed here currently
     * @param id the customer id
     * @param customer the updating customer, formatted in json
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/api/customers/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody Customer customer) {
        validateCustomer(id);
        Customer rst = customerRepository.findOne(id);
        compareAndUpdate(rst, customer);
        customerRepository.save(rst);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    private void compareAndUpdate(Customer before, Customer after) {
        before.setCompanyName(after.getCompanyName());
        before.setEmail(after.getEmail());
        before.setPhone(after.getPhone());
        before.setZip(after.getZip());
        before.setType(after.getType());
        before.setMobile(after.getMobile());
        before.setAddress(after.getAddress());
        before.setUserId(after.getUserId());
        before.setContactName(after.getContactName());
    }


    private void validateCustomer(long id) {
        if (!customerRepository.exists(id)) {
            throw new CustomerNotFoundException(id);
        }
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
    public static class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(long customerId) {
            super("could not find customer '" + customerId + "'.");
        }
    }


}
