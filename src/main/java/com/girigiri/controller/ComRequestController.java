package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.controller.utils.ViolationError;
import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.services.ComponentRequestRepository;
import com.girigiri.dao.services.RepairHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by JianGuo on 7/1/16.
 * Rest controller for {@link com.girigiri.dao.models.ComponentRequest}
 */
@RestController
@RequestMapping(value = "/api/com_requests")
public class ComRequestController {


    private final ComponentRequestRepository componentRequestRepository;
    private final RepairHistoryRepository historyRepository;

    @Autowired
    public ComRequestController(ComponentRequestRepository componentRequestRepository, RepairHistoryRepository repairHistoryRepository) {
        this.componentRequestRepository = componentRequestRepository;
        this.historyRepository = repairHistoryRepository;
    }

    @RequestMapping(value = "" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAllRequests() {
        Iterable<ComponentRequest> iterable = componentRequestRepository.findAll();
        for (ComponentRequest componentRequest : iterable) {
            componentRequest.set_link(linkTo(methodOn(ComRequestController.class).getRequest(componentRequest.getId())).withSelfRel());
        }

        return ResponseEntity.ok(new Resources<>(iterable));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getRequest(@PathVariable Long id) {
        validateId(id);
        ComponentRequest componentRequest = componentRequestRepository.findOne(id);
        componentRequest.set_link(linkTo(methodOn(ComRequestController.class).getRequest(id)).withSelfRel());
        return ResponseEntity.ok(new Resource<>(componentRequest));
    }


    /**
     * Update a existed componentRequest using {@link RequestMethod}.PUT method,
     * PATCH method is not allowed here currently
     *
     * @param id      the repairHistory id
     * @param componentRequest the updating componentRequest, formatted in json
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody ComponentRequest componentRequest) {
        validateId(id);
        checkWithHistoryId(componentRequest.getHistory());
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
        checkWithHistoryId(componentRequest.getHistory());
        componentRequestRepository.save(componentRequest);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    private void compareAndUpdate(ComponentRequest before, ComponentRequest after) {
        before.setSize(after.getSize());
        before.setHistory(after.getHistory());
        before.setState(after.getState());
        before.setName(after.getName());
        before.setPrice(after.getPrice());
        before.setSerial(after.getSerial());
    }


    private void checkWithHistoryId(long history) {
        if (!historyRepository.exists(history)) {
            throw new InvalidHistoryException(history);
        }
    }

    private void validateId(Long id) {
        if (!componentRequestRepository.exists(id)) {
            throw new ComponentRequestNotFoundException(id);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private class ComponentRequestNotFoundException extends RuntimeException {
        ComponentRequestNotFoundException(Long id) {
            super("can not found component request " + id);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private class InvalidHistoryException extends RuntimeException {
        InvalidHistoryException(long history) {
            super("invalid with history id " + history);
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
}
