package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.dao.models.Component;
import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.models.Customer;
import com.girigiri.dao.services.ComponentRepository;
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
 * Custom RestController for {@link Component}
 */
@RestController
@RequestMapping(value = "/api/components")
public class ComponentController extends BaseController {

    private final ComponentRepository componentRepository;


    @Autowired
    public ComponentController(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }


    /**
     * Get all customers in /api/components
     *
     * @return the {@link ResponseEntity} of all components, <b>200 OK</b> is also returned if success
     */
    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getComponents() {
        Iterable<Component> iterable = componentRepository.findAll();
        for (Component component : iterable) {
            component.set_links(linkTo(methodOn(ComponentController.class).getComponent(component.getId())).withSelfRel());
        }
        Resources<Component> resources = new Resources<>(iterable);
        return ResponseEntity.ok(resources);
    }

    /**
     * Save a component using {@link RequestMethod}.POST method
     *
     * @param component the json posted, it will be converted to POJO
     * @return <b>201 Created</b> if created success
     */
    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> save(@RequestBody Component component) {
        Component rst = componentRepository.save(component);
        rst.set_links(linkTo(methodOn(ComponentController.class).getComponent(rst.getId())).withSelfRel());
        Resource<Component> resources = new Resource<>(rst);
        return new ResponseEntity<>(resources, HttpStatus.CREATED);
    }


    /**
     * Return all components in pages
     *
     * @param page the number of current page, each page's size is {@link BaseController#DEFAULT_PAGE_SIZE},
     *             note that page starts in <b>0</b>, not <b>1</b>
     * @return Current page of components, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages"})
    public
    @ResponseBody
    ResponseEntity<?> getComponents(@RequestParam(value = "pages") int page) {
        return getComponentsByPage(page, DEFAULT_PAGE_SIZE, "id");
    }

    /**
     * Return all components with particular size
     *
     * @param size the size of query
     * @return Current page of components, and <b>200 OK</b> if success
     */
    @RequestMapping(method = RequestMethod.GET, params = {"size"})
    public
    @ResponseBody
    ResponseEntity<?> getComponentsBySize(@RequestParam(value = "size") int size) {
        return getComponentsByPage(0, size, "id");
    }


    /**
     * Get one component by id
     *
     * @param id the component id
     * @return the component json and <b>200 OK</b> if component exists, <b>404 NOT FOUND</b> if component
     * doesn't exist.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> getComponent(@PathVariable Long id) {
        validateComponent(id);
        Component component = componentRepository.findOne(id);
        component.set_links(linkTo(methodOn(ComponentController.class).getComponent(id)).withSelfRel());
        return ResponseEntity.ok(new Resource<>(component));
    }


    /**
     * Get components in current page and in given sort order
     *
     * @param page the current page, default size is {@link BaseController#DEFAULT_PAGE_SIZE}
     *             note that page starts from <b>0</b>, not <b>1</b>
     * @param sort sort order
     * @return components in json and <b>200 OK</b>
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getComponents(@RequestParam(value = "pages") int page
            , @RequestParam(value = "sort") String sort) {
        return getComponentsByPage(page, DEFAULT_PAGE_SIZE, sort);
    }

    /**
     * Get components in current page and in given sort order
     *
     * @param page the current page, note that page starts from <b>0</b>, not <b>1</b>
     * @param size each page's size
     * @param sort sort order
     * @return components in json and <b>200 OK</b>
     */
    @RequestMapping(method = RequestMethod.GET, params = {"pages", "size", "sort"})
    public
    @ResponseBody
    ResponseEntity<?> getComponents(@RequestParam(value = "pages") int page, @RequestParam(value = "size") int size, @RequestParam(value = "sort") String sort) {
        return getComponentsByPage(page, size, sort);
    }

    /**
     * Delete a component in {@link RequestMethod}.DELETE method
     *
     * @param id the component id
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        validateComponent(id);
        componentRepository.delete(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    /**
     * Update a existed component using {@link RequestMethod}.PUT method,
     * PATCH method is not allowed here currently
     *
     * @param id       the component id
     * @param component the updating component, formatted in json
     * @return <b>204 No Content</b> if success
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody Component component) {
        validateComponent(id);
        Component tmp = componentRepository.findOne(id);
        compareAndUpdate(tmp, component);
        componentRepository.save(tmp);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    @RequestMapping(value = "/search/findByName", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> search(@RequestParam(value = "name", required = false) String name) {
        List<Component> list = componentRepository.findByName(name);
        list.forEach(component -> component.set_links(linkTo(methodOn(ComponentController.class).getComponent(component.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resources<>(list));
    }


    private ResponseEntity<?> getComponentsByPage(int page, int size, String sort) {
        Page<Component> pages = componentRepository.findAll(new PageRequest(page, size, new Sort(sort)));
        pages.forEach(component -> component.set_links(linkTo(methodOn(ComponentController.class).getComponent(component.getId())).withSelfRel()));
        return ResponseEntity.ok(new Resource<>(pages));
    }

    private void compareAndUpdate(Component before, Component after) {
        before.setName(after.getName());
        before.setSize(after.getSize());
        before.setWarningSize(after.getWarningSize());
        before.setPrice(after.getPrice());
        before.setSerial(after.getSerial());
    }


    private void validateComponent(long id) {
        if (!componentRepository.exists(id)) {
            throw new RestUtils.ComponentNotFoundException(id);
        }
    }


}
