package com.tsi.prototype.wdogs.dogs.app;

import com.tsi.prototype.wdogs.dogs.dao.DogsRepository;
import com.tsi.prototype.wdogs.dogs.model.Dog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for DOGs micro-service
 */
@Controller
@RequestMapping(path="/dogs")
public class DogsController {
    private final static Logger LOG = LoggerFactory.getLogger(DogsController.class);

    private DogsRepository dogsRepository;

    @Autowired
    protected void setDogsRepository(DogsRepository repository) {
        this.dogsRepository = repository;
    }

    private DogsRepository getDogsRepository() {
        return this.dogsRepository;
    }

    private static final Map<String, String> PARAMETERS_MAP = new HashMap<String, String>() {{
        put("kind", "Page");
    }};

    /**
     * Method represents implementation of /easy path
     * @return hardcoded string
     */
    @GetMapping(value = "/easy", produces = "application/text")
    public @ResponseBody String getEasy() {
        return "Hello World";
    }

    /*
    @GetMapping(produces = "application/json")
    public @ResponseBody
    Iterable<Dog> getDogs(@RequestParam String name) {
        if (Objects.isNull(name)) {
            return dogsRepository.findAll();
        } else {
            return dogsRepository.findByName(name);
        }
    }
    */

    @GetMapping(produces = "application/json")
    public @ResponseBody Map<String, String> getBaseLinkProperties() {
        Map<String, String> response = new HashMap<>(PARAMETERS_MAP);
        response.put("self", ServletUriComponentsBuilder.fromCurrentRequestUri().build().toString());
        return response;
    }


    /**
     * Implementation of GET method /{name}
     * @param name name of the dog
     * @return response entity
     */
    @GetMapping(value = "/{name}", produces = "application/json")
    public @ResponseBody ResponseEntity<Dog> getPerName(@PathVariable String name) {
        List<Dog> dogs = getDogsRepository().findByDogName(name);
        if (dogs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Dog result = dogs.iterator().next();
        //setting self to current URI
        result.setSelf(ServletUriComponentsBuilder.fromCurrentRequestUri().build().toString());
        //constructing URI for owner
        result.setOwner(ServletUriComponentsBuilder.fromCurrentContextPath().path("/owners/{owner}").build(result.getOwner()).toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Creates new dog
     * @param newDog new dog to create
     * @return response entity
     */
    @PostMapping()
    public @ResponseBody ResponseEntity addDog(Dog newDog) {
        try {
            getDogsRepository().save(newDog);
        } catch (Throwable e) {
            LOG.error("Exception occured saving dog", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String id = newDog.getDogName();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").build(id);
        return ResponseEntity.status(HttpStatus.CREATED).header("Location",uri.toString()).build();
    }
}
