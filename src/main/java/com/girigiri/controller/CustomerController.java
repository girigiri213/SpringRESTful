package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.dao.models.Customer;
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

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 6/28/16.
 * Custom RestController for {@link com.girigiri.dao.models.Customer}
 */
@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController extends BaseController {

    private final CustomerRepository customerRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, RequestRepository requestRepository) {
        this.customerRepository = customerRepository;
        this.requestRepository = requestRepository;
    }


    /**
     * Get all customers in /api/customers
     *
     * @return the {@link ResponseEntity} of all customers, <b>200 OK</b> is also returned if success
     */
    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getCustomers() {
        Iterable<Customer> iterable = customerRepository.findAll();
        for (Customer customer : iterable) {
            customer.set_links(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel());
        }
        Resources<Customer> resources = new Resources<>(iterable);
        return ResponseEntity.ok(resources);
    }

    /**
     * Save a customer using {@link RequestMethod}.POST method
     *
     * @param customer the json posted, it will be converted to POJO
     * @return <b>201 Created</b> if created success
     */
    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> save(@RequestBody Customer customer) {
        Customer rst = customerRepository.save(customer);
        rst.set_links(linkTo(methodOn(CustomerController.class).getCustomer(rst.getId())).withSelfRel());
        Resource<Customer> resources = new Resource<>(rst);
        return new ResponseEntity<>(resources, HttpStatus.CREATED);
    }


    /**
     * Return all customers in pages
     *
     * @param page the number of current page, each page's size is {@link BaseController#DEFAULT_PAGE_SIZE},
     *             note that page starts in <b>0</b>, not <b>1</b>
     * @return Current page of customers, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getCustomers(@RequestParam(value = "pages") int page) {
        return getCustomersByPage(page, DEFAULT_PAGE_SIZE, "id");
    }

    /**
     * Return all customers with particular size
     *
     * @param size the size of query
     * @return Current page of customers, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getCustomersBySize(@RequestParam(value = "size") int size) {
        return getCustomersByPage(0, size, "id");
    }


    /**
     * Get one customer by id
     *
     * @param id the customer id
     * @return the customer json and <b>200 OK</b> if customer exists, <b>404 NOT FOUND</b> if customer
     * doesn't exist.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
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
     *
     * @param page the current page, default size is {@link BaseController#DEFAULT_PAGE_SIZE}
     *             note that page starts from <b>0</b>, not <b>1</b>
     * @param sort sort order
     * @return customers in json and <b>200 OK</b>
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getCustomers(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        return getCustomersByPage(page, DEFAULT_PAGE_SIZE, sort);
    }

    /**
     * Get customers in current page and in given sort order
     *
     * @param page the current page, note that page starts from <b>0</b>, not <b>1</b>
     * @param size each page's size
     * @param sort sort order
     * @return customers in json and <b>200 OK</b>
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages", "size", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getCustomers(@RequestParam(value = "pages") int page, @RequestParam(value = "size") int size, @RequestParam(value = "sort") String sort) {
        return getCustomersByPage(page, size, sort);
    }

    /**
     * Delete a customer in {@link RequestMethod}.DELETE method
     *
     * @param id the customer id
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
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
     *
     * @param id       the customer id
     * @param customer the updating customer, formatted in json
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody Customer customer) {
        validateCustomer(id);
        Customer rst = customerRepository.findOne(id);
        compareAndUpdate(rst, customer);
        customerRepository.save(rst);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    @RequestMapping(value = "/searchCustomer", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> search(@RequestParam(value = "userId", required = false) String userId,
                             @RequestParam(value = "mobile", required = false) String mobile,
                             @RequestParam(value = "contactName", required = false) String contactName,
                             @RequestParam(value = "low", required = false) String low,
                             @RequestParam(value = "high", required = false) String high) {
        if (userId == null || userId.equals("")) userId = "%";
        if (mobile == null || mobile.equals("")) mobile = "%";
        if (contactName == null || contactName.equals("")) contactName = "%";
        long lowerBound = Long.MIN_VALUE;
        long upperBound = Long.MAX_VALUE;
        if ((low != null) && !low.equals("")) lowerBound = Long.parseLong(low);
        if ((high != null) && !high.equals("")) upperBound = Long.parseLong(high);
        List<Customer> list = customerRepository.search(userId, mobile, contactName);
        long finalUpperBound = upperBound;
        long finalLowerBound = lowerBound;
        List<Customer> rst = list.stream().filter(customer -> (customer.getCreated() <= finalUpperBound && customer.getCreated() >= finalLowerBound))
                .collect(toList());
        rst.forEach(customer -> customer.set_links(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resources<>(rst));
    }


    private ResponseEntity<?> getCustomersByPage(int page, int size, String sort) {
        Page<Customer> pages = customerRepository.findAll(new PageRequest(page, size, new Sort(sort)));
        pages.forEach(customer -> customer.set_links(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resource<>(pages));
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
            throw new RestUtils.CustomerNotFoundException(id);
        }
    }


}
