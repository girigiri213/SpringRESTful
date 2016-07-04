package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.services.ComponentRequestRepository;
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

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 7/1/16.
 * Rest controller for {@link com.girigiri.dao.models.ComponentRequest}
 */
@RestController
@RequestMapping(value = "/api/componentRequests")
public class ComRequestController extends BaseController {


    private final ComponentRequestRepository componentRequestRepository;
    private final RepairHistoryRepository historyRepository;

    @Autowired
    public ComRequestController(ComponentRequestRepository componentRequestRepository, RepairHistoryRepository repairHistoryRepository) {
        this.componentRequestRepository = componentRequestRepository;
        this.historyRepository = repairHistoryRepository;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAllRequests() {
        Iterable<ComponentRequest> iterable = componentRequestRepository.findAll();
        for (ComponentRequest componentRequest : iterable) {
            componentRequest.set_links(linkTo(methodOn(ComRequestController.class).getRequest(componentRequest.getId())).withSelfRel());
        }
        return ResponseEntity.ok(new Resources<>(iterable));
    }


    /**
     * Return all componentRequests with particular size
     *
     * @param size the size of query
     * @return Current page of componentRequests, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "", method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getRequestsBySize(@RequestParam(value = "size") int size) {
        return getRequestsByPage(0, size, "id");
    }

    /**
     * Return all componentRequests with particular size
     *
     * @param page the page of query, note that page starts from <b>0</b>, not <b>1</b>
     * @param size the size of query
     * @param sort the sort column, id by default
     * @return Current page of componentRequests, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "", method = RequestMethod.GET, params = {"pages", "size", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getRequests(@RequestParam(value = "pages") int page, @RequestParam(value = "size") int size, @RequestParam(value = "sort") String sort) {
        return getRequestsByPage(page, size, sort);
    }


    /**
     * Return all componentRequests with particular size
     *
     * @param page the page of query, note that page starts from <b>0</b>, not <b>1</b>
     * @return Current page of componentRequests, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "", method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getRequestsByPage(@RequestParam(value = "pages") int page) {
        return getRequestsByPage(page, DEFAULT_PAGE_SIZE, "id");
    }


    /**
     * Return all componentRequests with particular size
     *
     * @param page the page of query, note that page starts from <b>0</b>, not <b>1</b>
     * @param sort the sort column, id by default
     * @return Current page of componentRequests, and <b>200 OK</b> if success
     */
    @RequestMapping(value = "", method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getRequestsByPageAndSort(@RequestParam(value = "pages") int page, @RequestParam(value = "sort") String sort) {
        return getRequestsByPage(page, DEFAULT_PAGE_SIZE, sort);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getRequest(@PathVariable Long id) {
        validateId(id);
        ComponentRequest componentRequest = componentRequestRepository.findOne(id);
        componentRequest.set_links(linkTo(methodOn(ComRequestController.class).getRequest(id)).withSelfRel());
        return ResponseEntity.ok(new Resource<>(componentRequest));
    }


    /**
     * Update a existed componentRequest using {@link RequestMethod}.PUT method,
     * PATCH method is not allowed here currently
     *
     * @param id               the repairHistory id
     * @param componentRequest the updating componentRequest, formatted in json
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody ComponentRequest componentRequest) {
        validateId(id);
        if (componentRequest.getHistory() > 0) {
            checkWithHistoryId(componentRequest.getHistory());
        }
        ComponentRequest rst = componentRequestRepository.findOne(id);
        compareAndUpdate(rst, componentRequest);
        componentRequestRepository.save(rst);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable long id) {
        validateId(id);
        componentRequestRepository.delete(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(@RequestBody ComponentRequest componentRequest) {
        if (componentRequest.getHistory() > 0) {
            checkWithHistoryId(componentRequest.getHistory());
        }
        ComponentRequest rst = componentRequestRepository.save(componentRequest);
        rst.set_links(linkTo(methodOn(ComRequestController.class).getRequest(rst.getId())).withSelfRel());
        return new ResponseEntity<>(new Resource<>(rst), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/searchRequest", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> search(@RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "low", required = false) String low,
                                    @RequestParam(value = "high", required = false) String high) {
        List<ComponentRequest> list;
        if (name == null || name.equals("")) {
            list = (List<ComponentRequest>) componentRequestRepository.findAll();
        } else {
            list = componentRequestRepository.findByName(name);
        }
        long lowerBound = Long.MIN_VALUE;
        long upperBound = Long.MAX_VALUE;
        if (low != null && !low.equals("")) lowerBound = Long.parseLong(low);
        if (high != null && !high.equals("")) upperBound = Long.parseLong(high);
        long finalUpperBound = upperBound;
        long finalLowerBound = lowerBound;
        List<ComponentRequest> rst = list.stream().filter(componentRequest -> componentRequest.getCreated() >= finalLowerBound && componentRequest.getCreated() <= finalUpperBound)
                .collect(toList());
        rst.forEach(componentRequest -> componentRequest.set_links(linkTo(methodOn(ComRequestController.class).getRequest(componentRequest.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resources<>(rst));
    }

    private void compareAndUpdate(ComponentRequest before, ComponentRequest after) {
        before.setSize(after.getSize());
        before.setHistory(after.getHistory());
        before.setState(after.getState());
        before.setName(after.getName());
        before.setPrice(after.getPrice());
        before.setSerial(after.getSerial());
    }


    private ResponseEntity<?> getRequestsByPage(int page, int size, String sort) {
        Page<ComponentRequest> pages = componentRequestRepository.findAll(new PageRequest(page, size, new Sort(sort)));
        pages.forEach(componentRequest -> componentRequest.set_links(linkTo(methodOn(ComRequestController.class).getRequest(componentRequest.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resource<>(pages));
    }


    private void checkWithHistoryId(long history) {
        if (!historyRepository.exists(history)) {
            throw new RestUtils.InvalidHistoryException(history);
        }
    }

    private void validateId(Long id) {
        if (!componentRequestRepository.exists(id)) {
            throw new RestUtils.ComponentRequestNotFoundException(id);
        }
    }


}
