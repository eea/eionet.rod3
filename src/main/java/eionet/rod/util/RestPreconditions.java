package eionet.rod.util;

import eionet.rod.util.exception.ResourceNotFoundException;

/**
 * @author jrobles
 *
 */
public class RestPreconditions {

	public static <T> T checkFound(final T resource) {
		if (resource == null) {
			throw new ResourceNotFoundException();
		}
		return resource;
	}
	
	public static <T> T checkFoundMsg(final T resource, String message) {
		if (resource == null) {
			throw new ResourceNotFoundException(message);
		}
		return resource;
	}
	
	public static <T> T checkNotNull(final T resource) {
		if (resource == null) {
			throw new ResourceNotFoundException();
		}
		return resource;
	}

}
