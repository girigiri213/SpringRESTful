package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.dao.models.Customer;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 6/30/16.
 * Rest Controller for {@link RepairHistoryRepository}
 * Because system will create a history when a request comes, so save a history directly is not allowed currently.
 */
@RestController
@RequestMapping(value = "/api/repairHistories")
public class RepairHistoryController extends BaseController {
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
    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getHistories() {
        Iterable<RepairHistory> iterable = repairHistoryRepository.findAll();
        for (RepairHistory repairHistory : iterable) {
            repairHistory.set_links(linkTo(methodOn(RepairHistoryController.class).getHistory(repairHistory.getId())).withSelfRel());
        }
        return ResponseEntity.ok(new Resources<>(iterable));
    }


    /**
     * Return all repairHistories in pages
     *
     * @param page the number of current page, each page's size is 5
     * @return Current page of histories, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getHistories(@RequestParam(value = "pages") int page) {
        return getHistoriesInPage(page, DEFAULT_PAGE_SIZE, "id");
    }

    /**
     * Return all repairHistories in pages
     *
     * @param size the size of query
     * @return Current page of histories, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getHistoriesBySize(@RequestParam(value = "size") int size) {
        return getHistoriesInPage(0, size, "id");
    }

    /**
     * Get histories in current page and in given sort order
     *
     * @param page the current page, the default page's is {@link BaseController#DEFAULT_PAGE_SIZE}
     * @param sort sort order
     * @return histories in json and <b>200 OK</b>
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getHistoriesByPageAndSort(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        return getHistoriesInPage(page, DEFAULT_PAGE_SIZE, sort);
    }


    private ResponseEntity<?> getHistoriesInPage(int page, int size, String sort) {
        Page<RepairHistory> pages = repairHistoryRepository.findAll(new PageRequest(page, size, new Sort(sort)));
        pages.forEach(history -> history.set_links(linkTo(methodOn(RequestController.class).getRequest(history.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resource<>(pages));
    }


    /**
     * Get one repairHistory by id
     *
     * @param id the repairHistory id
     * @return the history json and <b>200 OK</b> if repairHistory exists, <b>404 NOT FOUND</b> if repairHistory
     * doesn't exist.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getHistory(@PathVariable Long id) {
        validateHistory(id);
        RepairHistory repairHistory = repairHistoryRepository.findOne(id);
        repairHistory.set_links(linkTo(methodOn(RepairHistoryController.class).getHistory(id)).withSelfRel());
        return ResponseEntity.ok(new Resource<>(repairHistory));
    }

    /**
     * Delete a repairHistory in {@link RequestMethod}.DELETE method
     *
     * @param id the repairHistory id
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
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
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
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

    @RequestMapping(value = "/searchHistories", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> search(@RequestParam(value = "engineer", required = false) String name,
                             @RequestParam(value = "low", required = false) String low,
                             @RequestParam(value = "high", required = false) String high) {
        long lowerBound = Long.MIN_VALUE;
        long upperBound = Long.MAX_VALUE;
        if (low != null && !low.equals("")) lowerBound = Long.parseLong(low);
        if (high != null && !high.equals("")) upperBound = Long.parseLong(high);
        long finalUpperBound = upperBound;
        long finalLowerBound = lowerBound;
        List<RepairHistory> list = new ArrayList<>();
        if (name == null || name.equals("")) {
            list = (List<RepairHistory>) repairHistoryRepository.findAll();
        } else {
            Manager manager = managerRepository.findOneByName(name);
            if (manager == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                if (containsCorrectRole(manager.getRoles())) {
                    list = repairHistoryRepository.findByManagerId(manager.getId());
                } else {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
            }
        }
        List<RepairHistory> rst = list.stream().filter(history -> (history.getCreated() <= finalUpperBound && history.getCreated() >= finalLowerBound))
                .collect(toList());
        rst.forEach(history -> history.set_links(linkTo(methodOn(RepairHistoryController.class).getHistory(history.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resources<>(rst));
    }


    private void validateHistoryByManager(RepairHistory history) {
        if (!managerRepository.exists(history.getManagerId())) {
            throw new RestUtils.InvalidManagerException(history.getManagerId());
        } else {
            Manager manager = managerRepository.findOne(history.getManagerId());
            String[] roles = manager.getRoles();
            if (!containsCorrectRole(roles)) {
                throw new RestUtils.InvalidManagerException(history.getManagerId());
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
        before.setRepairTime(after.getRepairTime());
        before.setDelayType(after.getDelayType());
        before.setAssignTime(after.getAssignTime());
        before.setManPrice(after.getManPrice());
        before.setRepairState(after.getRepairState());
        before.setManagerId(after.getManagerId());
        before.setMaterialPrice(after.getMaterialPrice());
    }


    private void validateHistory(long id) {
        if (!repairHistoryRepository.exists(id)) {
            throw new RestUtils.HistoryNotFoundException(id);
        }
    }


}
