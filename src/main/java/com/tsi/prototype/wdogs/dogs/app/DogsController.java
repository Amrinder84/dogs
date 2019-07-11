package com.tsi.prototype.wdogs.dogs.app;

import com.tsi.prototype.wdogs.dogs.dao.DogsRepository;
import com.tsi.prototype.wdogs.dogs.model.Dog;
import com.tsi.prototype.wdogs.dogs.model.DogList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * REST Controller for DOGs micro-service
 */
@Controller
@RequestMapping(path="/dogs")
public class DogsController {
    private final static Logger LOG = LoggerFactory.getLogger(DogsController.class);
    static final String QPARAM_PAGE = "page";
    static final String QPARAM_SIZE = "size";

    private DogsRepository dogsRepository;

    private int pageSize = 10;

    @Autowired
    protected void setDogsRepository(DogsRepository repository) {
        this.dogsRepository = repository;
    }

    private DogsRepository getDogsRepository() {
        return this.dogsRepository;
    }

    @Value("#{systemProperties['PAGE_SIZE'] ?: '10'}")
    public void setPageSize(int size) {
        this.pageSize = size;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    private static final Map<String, String> PARAMETERS_MAP = new HashMap<String, String>() {{
        put("kind", "Page");
    }};

    /**
     * Implementation of the get method in resource /dogs
     * @param page optional parameter, non-null based page
     * @param size optional parameter positive integer page size
     * @return
     */
    @GetMapping(produces = "application/json")
    public @ResponseBody ResponseEntity<DogList> getDogsList(@RequestParam(name = QPARAM_PAGE, required = false) Integer page, @RequestParam(name = QPARAM_SIZE, required = false) Integer size) {
        if ((Objects.isNull(page) && Objects.nonNull(size)) || (Objects.isNull(size) && Objects.nonNull(page))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        long count = getDogsRepository().count();
        LOG.debug("Total count of dogs " + count);
        if (Objects.isNull(page) && count > getPageSize()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location",
                            ServletUriComponentsBuilder.fromCurrentRequestUri()
                                    .queryParam(QPARAM_PAGE, 0)
                                    .queryParam(QPARAM_SIZE, getPageSize()).build().toString()).build();
        }
        Iterable<Dog> dogsList;
        if (Objects.isNull(page)) {
            dogsList = getDogsRepository().findAll();
        } else {
            if (page == 0 || size < 1 || page < 0 || size < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            PageRequest request = PageRequest.of(page - 1, size);
            dogsList = getDogsRepository().findAll(request);
        }
        DogList result = new DogList();
        dogsList.forEach(dog -> {
            dog.setSelf(ServletUriComponentsBuilder.fromCurrentRequestUri().replaceQuery("").path("/{name}").build(dog.getDogName()).toString());
            result.getContents().add(dog);
        });
        result.setSelf(ServletUriComponentsBuilder.fromCurrentRequestUri().build().toString());
        result.setPageOf(ServletUriComponentsBuilder.fromCurrentRequestUri().replaceQuery("").build().toString());
        if (Objects.nonNull(page)) {
            result.setFirst(ServletUriComponentsBuilder.fromCurrentRequestUri().replaceQuery("")
                    .queryParam(QPARAM_PAGE, 1).queryParam(QPARAM_SIZE, size).build().toString());
            if (page != 1) result.setPrevious(ServletUriComponentsBuilder.fromCurrentRequestUri().replaceQuery("")
                    .queryParam(QPARAM_PAGE, page - 1).queryParam(QPARAM_SIZE, size).build().toString());

            if (page * size < count) {
                result.setNext(ServletUriComponentsBuilder.fromCurrentRequestUri().replaceQuery("")
                        .queryParam(QPARAM_PAGE, page + 1).queryParam(QPARAM_SIZE, size).build().toString());
            }

            int totalPage = (int) Math.ceil((double) count/ size);
            result.setLast(ServletUriComponentsBuilder.fromCurrentRequestUri().replaceQuery("")
                    .queryParam(QPARAM_PAGE, totalPage).queryParam(QPARAM_SIZE, size).build().toString());
        }

        return ResponseEntity.ok(result);
    }


    /**
     * Implementation of GET method /dogs/{name}
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
