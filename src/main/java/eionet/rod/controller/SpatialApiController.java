package eionet.rod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import eionet.rod.model.Spatial;
import eionet.rod.service.SpatialService;
import eionet.rod.util.RestPreconditions;
import eionet.rod.util.exception.ResourceNotFoundException;

/**
 * @author jrobles
 *
 */
@Controller
@RequestMapping(value = "/api/spatial")
public class SpatialApiController {

	@ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException() {
        return "404";
    }
	
	@Autowired
	SpatialService service;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Spatial> findAll() {
		return service.findAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public  Spatial findOne(@PathVariable("id") Integer id) {
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
