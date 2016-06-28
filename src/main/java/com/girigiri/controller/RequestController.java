package com.girigiri.controller;

import com.girigiri.dao.models.Request;
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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by JianGuo on 6/21/16.
 * RestController for {@link Request}
 */
@RestController
public class RequestController {

    private RequestRepository requestRepository;


    @Autowired
    public RequestController(RequestRepository repo) {
        requestRepository = repo;
    }

    @RequestMapping(value = "/api/requests", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getRequests() {
        Resources<Request> resources = new Resources<>(requestRepository.findAll());
        resources.add(linkTo(methodOn(RequestController.class).getRequests()).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    @RequestMapping(value = "/api/requests", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> save(@RequestBody Request request) {
        Resource<Request> resources = new Resource<>(requestRepository.save(request));
        resources.add(linkTo(methodOn(RequestController.class).getRequests()).withSelfRel());
        return new ResponseEntity<>(resources, HttpStatus.CREATED);
    }



    @RequestMapping(value = "/api/requests", method = RequestMethod.GET, params = {"pages"})
    public @ResponseBody ResponseEntity<?> getRequests(@RequestParam(value = "pages") int page) {
        Page<Request> pages = requestRepository.findAll(new PageRequest(page, 2, new Sort("id")));
        Resources<Request> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(RequestController.class).getRequests()).withSelfRel());
        return ResponseEntity.ok(resources);
    }



    @RequestMapping(value = "/api/requests", method = RequestMethod.GET, params = {"pages", "sort"})
    public @ResponseBody ResponseEntity<?> getRequests(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        Page<Request>  pages = requestRepository.findAll(new PageRequest(page, 20, new Sort(sort)));
        Resources<Request> resources = new Resources<>(pages);
        resources.add(linkTo(methodOn(RequestController.class).getRequests()).withSelfRel());
        return ResponseEntity.ok(resources);
    }
}
