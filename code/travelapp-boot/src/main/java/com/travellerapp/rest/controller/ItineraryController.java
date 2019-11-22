package com.travellerapp.rest.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.travellerapp.business.ItineraryServiceImpl;
import com.travellerapp.domain.Itinerary;
import com.travellerapp.email.EmailService;
import com.travellerapp.email.Mail;

@RestController
@RequestMapping(path = "/itinerary")
public class ItineraryController 
{
	@Autowired
    private EmailService emailService;
	
	@Autowired
	private ItineraryServiceImpl itineraryService;
	 
	@GetMapping(path="/getAllItineraries", produces = "application/json")
    public List<Itinerary> getAllItineraries() 
    {
       return itineraryService.listAllItineraries();
    }

	@GetMapping(path = "/getEmail", produces = "application/json")
	public ResponseEntity<String> sendEmail() throws MessagingException, IOException {
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		Mail mail = new Mail();
		mail.setFrom("planpackrepeat@gmail.com");
		mail.setTo("saikirankondapalli03@gmail.com");
		mail.setSubject("Sending Email with Thymeleaf HTML Template Example");

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("name", "Memorynotfound.com");
		model.put("location", "Belgium");
		model.put("signature", "http://memorynotfound.com");
		mail.setModel(model);

		emailService.sendSimpleMessage(mail);
		return response;
	}
	   
       
       
    @GetMapping(path="/getItineraryByEmail/{email}", produces = "application/json")
    public ResponseEntity<Itinerary> getActiveItineraryByEmail(@PathVariable String email) 
    {
    	Itinerary itr= itineraryService.getActiveItineraryByEmail(email);
    	if(itr !=null) {
    		return ResponseEntity.status(HttpStatus.OK).body(itr);
    	}else {
    		return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
	}
    
    @RequestMapping(value = "/createItinerary", method = RequestMethod.POST)
    public ResponseEntity<Itinerary> createItineraryWithDestinations(@Valid @RequestBody Itinerary itr) 
	{
    	return ResponseEntity.ok().body(itineraryService.createItinerary(itr));
	}
    
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public Itinerary updateItinerary(@PathVariable("id") ObjectId id,@Valid @RequestBody Itinerary itr) 
	{
    	return itineraryService.updateItinerary(id,itr);
	}
    
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteItinerary(@PathVariable ObjectId id) {
		ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
		itineraryService.deleteItinerary(id);
		return response;
	}
    
    @RequestMapping(value = "/itineraries", method = RequestMethod.POST)
    public List<Itinerary> createItineraries(@Valid @RequestBody List<Itinerary> itineraries) 
	{
		return itineraryService.createItineraries(itineraries);
	}
    
    @RequestMapping(value = "/deleteAllItineraries", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllItineraries() 
	{
    	ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
    	itineraryService.listAllItineraries().stream().forEach(x->{
    		itineraryService.deleteItinerary(new ObjectId(x.getId()));
			});;
		return response;
	}
    
    @RequestMapping(value = "/deleteDestination/{email}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteDestination(@PathVariable String email,@Valid @RequestBody List<String> objectIds) 
	{
    	ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
    	itineraryService.deleteDestinationFromItinerary(email, objectIds);
		return response;
	}
    
    
 }
