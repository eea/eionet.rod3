package eionet.rod.controller;

import eionet.rod.model.Spatial;
import eionet.rod.service.SpatialService;
import eionet.rod.service.WebRODService;
import eionet.rod.util.RestPreconditions;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jrobles
 */
@Controller
@RequestMapping(value = "/api/spatial")
public class SpatialApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpatialApiController.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException() {
        return "404";
    }

    @Autowired
    SpatialService service;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public List<Spatial> findAll() {
        LOGGER.info("API call to /api/spatial");
        return service.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Spatial findOne(@PathVariable("id") Integer id) {
        LOGGER.info("API call to /api/spatial/{}", id);
        return RestPreconditions.checkFound(service.findOne(id));
    }

//	@RequestMapping(method = RequestMethod.POST)
//	@ResponseStatus(HttpStatus.CREATED)
//	@ResponseBody
//	public Long create(@RequestBody Spatial resource) {
//		RestPreconditions.checkNotNull(resource);
//		return service.create(resource);
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//	@ResponseStatus(HttpStatus.OK)
//	public void update(@PathVariable("id") Integer id, @RequestBody Spatial resource) {
//		RestPreconditions.checkNotNull(resource);
//		RestPreconditions.checkNotNull(service.getById(resource.getSpatialId()));
//		service.update(resource);
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//	@ResponseStatus(HttpStatus.OK)
//	public void delete(@PathVariable("id") Integer id) {
//		service.deleteById(id);
//	}

}
