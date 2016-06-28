package com.girigiri.controller;

import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
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
@RepositoryRestController
@RequestMapping("/api/requests")
public class RequestController {

    private RequestRepository requestRepository;


    @Autowired
    public RequestController(RequestRepository repo) {
        requestRepository = repo;
    }

//    @RequestMapping(method = RequestMethod.POST)
//    public @ResponseBody ResponseEntity<?> save(@RequestBody Request request) {
//        //TODO: Save request
//        System.err.println(request.toString());
//        Request rst = requestRepository.save(request);
//        Resource<Request> resource = new Resource<>(rst);
////        resource.add(linkTo(methodOn(HomeController.class)).withSelfRel());
//        return new ResponseEntity(resource, HttpStatus.CREATED);
//    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAll() {
        Resources<Request> requestResources = new Resources<>(requestRepository.findAll());
        requestResources.add(linkTo(methodOn(RequestController.class).getAll()).withSelfRel());
        return ResponseEntity.ok(requestResources);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> get(@PathVariable("id") Long id) {
        Resource<Request> resource = new Resource<>(requestRepository.findOne(id));
        return new ResponseEntity(resource, HttpStatus.OK);
    }
}
