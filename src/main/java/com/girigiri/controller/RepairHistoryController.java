package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.controller.utils.ViolationError;
import com.girigiri.dao.models.Manager;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.services.ManagerRepository;
import com.girigiri.dao.services.RepairHistoryRepository;
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
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 6/30/16.
 */
@RestController
public class RepairHistoryController {
    private final RepairHistoryRepository repairHistoryRepository;

    private final ManagerRepository managerRepository;

    @Autowired
    public RepairHistoryController(ManagerRepository managerRepository, RepairHistoryRepository repairHistoryRepository) {
        this.managerRepository = managerRepository;
        this.repairHistoryRepository = repairHistoryRepository;
    }


    /**
     * Get all customers in /api/customers
     *
     * @return the {@link ResponseEntity} of all customers, <b>200 OK</b> is also returned if success
     */
    @RequestMapping(value = "/api/histories", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getHistories() {
        Resources<RepairHistory> resources = new Resources<>(repairHistoryRepository.findAll());
        resources.add(linkTo(methodOn(RepairHistoryController.class).getHistories()).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    /**
     * Save a repairHistories using {@link RequestMethod}.POST method
     *
     * @param history the json posted, it will be converted to POJO
     * @return <b>201 Created</b> if created success
     */
    //TODO: Deprecated this for system will create history per request automatically
//    @RequestMapping(value = "/api/histories", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    ResponseEntity<?> save(@RequestBody RepairHistory history) {
//        validateHistoryByManager(history);
//        RepairHistory rst = repairHistoryRepository.save(history);
//        Resource<RepairHistory> resources = new Resource<>(rst);
//        resources.add(linkTo(methodOn(RepairHistoryController.class).getHistory(rst.getId())).withSelfRel());
//        return new ResponseEntity<>(resources, HttpStatus.CREATED);
//    }


    /**
     * Return all repairHistories in pages
     *
     * @param page the number of current page, each page's size is 5
     * @return Current page of histories, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "/api/histories", method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getHistories(@RequestParam(value = "pages") int page) {
        Page<RepairHistory> pages = repairHistoryRepository.findAll(new PageRequest(page, 5, new Sort("id")));
        Resources<RepairHistory> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * Return all repairHistories in pages
     *
     * @param size the size of query
     * @return Current page of histories, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "/api/histories", method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getHistoriesBySize(@RequestParam(value = "size") int size) {
        Page<RepairHistory> pages = repairHistoryRepository.findAll(new PageRequest(1, size, new Sort("id")));
        Resources<RepairHistory> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }



    /**
     * Get one repairHistory by id
     *
     * @param id the repairHistory id
     * @return the history json and <b>200 OK</b> if repairHistory exists, <b>404 NOT FOUND</b> if repairHistory
     * doesn't exist.
     */
    @RequestMapping(value = "/api/histories/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getHistory(@PathVariable Long id) {
        validateHistory(id);
        Resource<RepairHistory> resources = new Resource<>(repairHistoryRepository.findOne(id));
        resources.add(linkTo(methodOn(RepairHistoryController.class).getHistory(id)).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }


    /**
     * Get customers in current page and in given sort order
     *
     * @param page the current page
     * @param sort sort order
     * @return histories in json and <b>200 OK</b>
     */
    @RequestMapping(value = "/api/histories", method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getHistories(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        Page<RepairHistory> pages = repairHistoryRepository.findAll(new PageRequest(page, 20, new Sort(sort)));
        Resources<RepairHistory> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(RepairHistoryController.class).getHistories()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * Delete a repairHistory in {@link RequestMethod}.DELETE method
     *
     * @param id the repairHistory id
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/api/histories/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        validateHistory(id);
        repairHistoryRepository.delete(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    /**
     * Update a existed repairHistory using {@link RequestMethod}.PUT method,
     * PATCH method is not allowed here currently
     *
     * @param id      the repairHistory id
     * @param history the updating repairHistory, formatted in json
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/api/histories/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody RepairHistory history) {
        validateHistory(id);
        validateHistoryByManager(history);
        RepairHistory rst = repairHistoryRepository.findOne(id);
        compareAndUpdate(rst, history);
        repairHistoryRepository.save(rst);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    private void validateHistoryByManager(RepairHistory history) {
        if (!managerRepository.exists(history.getManagerId())) {
            throw new InvalidManagerException(history.getManagerId());
        } else {
            Manager manager = managerRepository.findOne(history.getManagerId());
            String[] roles = manager.getRoles();
            if (!containsCorrectRole(roles)) {
                throw new InvalidManagerException(history.getManagerId());
            }
        }
    }


    private boolean containsCorrectRole(String[] roles) {
        for (String role : roles) {
            if (role.equals(Manager.ROLE_ENGINEER)) {
                return true;
            }
        }
        return false;
    }

    private void compareAndUpdate(RepairHistory before, RepairHistory after) {
        before.setCheckHistory(after.getCheckHistory());
        before.setRepairState(after.getRepairState());
        before.setComponentRequests(after.getComponentRequests());
        before.setDelayType(after.getDelayType());
        before.setAssignTime(after.getAssignTime());
        before.setManPrice(after.getManPrice());
        before.setRepairState(after.getRepairState());
        before.setManagerId(after.getManagerId());
        before.setMaterialPrice(after.getMaterialPrice());
    }


    private void validateHistory(long id) {
        if (!repairHistoryRepository.exists(id)) {
            throw new HistoryNotFoundException(id);
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private static class InvalidManagerException extends RuntimeException {
        InvalidManagerException(long managerId) {
            super("Invalid manager id" + managerId);
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
    public static class HistoryNotFoundException extends RuntimeException {
        public HistoryNotFoundException(long historyId) {
            super("could not find history '" + historyId + "'.");
        }
    }
}
