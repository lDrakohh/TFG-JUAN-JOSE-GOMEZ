package org.springframework.samples.CumbresMalvinas.auth.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
	
	 private String message;

	  public MessageResponse(String message) {
	    this.message = message;
	  }

}
