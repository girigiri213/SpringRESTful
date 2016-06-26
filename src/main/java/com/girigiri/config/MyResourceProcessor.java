package com.girigiri.config;

import com.girigiri.controller.ConfirmController;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by JianGuo on 6/26/16.
 * Mix {@link org.springframework.data.repository.Repository} links with custom {@link org.springframework.web.bind.annotation.RestController}
 * Mix the {@link org.springframework.web.bind.annotation.RestController} links into {@link org.springframework.data.repository.Repository}
 */
@Component
public class MyResourceProcessor implements ResourceProcessor<RepositoryLinksResource> {

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(ConfirmController.class).withRel("confirm"));
        return resource;
    }
}
