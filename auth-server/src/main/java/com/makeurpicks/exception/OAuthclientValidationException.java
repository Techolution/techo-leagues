package com.makeurpicks.exception;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST, value = HttpStatus.BAD_REQUEST)
public class OAuthclientValidationException extends Exception {

	public enum OAuthClientExceptions {
		CLIENT_ID_NULL, AUTH_GRANT_TYPE_NULL_OR_EMPTY, SCOPE_NULL_OR_EMPTY, REDIRECT_URI_NULL_OR_EMPTY
	}

	private Log log = LogFactory.getLog(OAuthclientValidationException.class);

	private Iterable<OAuthClientExceptions> exceptions;

	public OAuthclientValidationException(OAuthClientExceptions... oauthCLientExceptions) {
		log.debug(oauthCLientExceptions);
		exceptions = new ArrayList<OAuthClientExceptions>(Arrays.asList(oauthCLientExceptions));
	}

	public boolean hasException() {
		if (exceptions.iterator().hasNext())
			return false;
		return true;
	}

	@Override
	public String getMessage() {
		return exceptions.toString();
	}
}
