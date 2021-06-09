package it.unipv.ingsw.pickuppoint.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unipv.ingsw.pickuppoint.model.DeliveryStatus;
import it.unipv.ingsw.pickuppoint.model.User;
import it.unipv.ingsw.pickuppoint.model.entity.OrderDetails;
import it.unipv.ingsw.pickuppoint.service.exception.ErrorPickupCode;
import it.unipv.ingsw.pickuppoint.service.exception.ErrorTrackingCode;

@Service
public class HubService {

	@Autowired
	OrderDetailsService orderDetailsService;
	@Autowired
	EntityManager entityManager;
	@Autowired
	UserService userService;

	@Autowired
	LockerService lockerService;

	public String getCurrentDataTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public void deliver(Long id) {
		OrderDetails orderDetails = orderDetailsService.getOrderDetailsById(id);
		orderDetails.getDeliveryDetails().setDeliveryStatus(DeliveryStatus.DELIVERED);
		orderDetails.getDeliveryDetails().setDataDeliverd(getCurrentDataTime());
		lockerService.setSlotDeliver(orderDetails);
		orderDetailsService.save(orderDetails);
	}

	public void withdraw(String pickupCode) throws ErrorPickupCode {
		OrderDetails orderDetails = null;
		orderDetails = orderDetailsService.getOrderByPickupCode(pickupCode);
		
		if (orderDetails == null) 
			throw new ErrorPickupCode("Wrong pickup code, please try again");

		orderDetails.getDeliveryDetails().setDeliveryStatus(DeliveryStatus.WITHDRAWN);
		orderDetails.getDeliveryDetails().setWithdrawalDate(getCurrentDataTime());
		// rimuovere dallo slot
		orderDetailsService.save(orderDetails);
	}

	public void addOrder(int tracking) throws ErrorTrackingCode {
		OrderDetails orderDetails = null;
		orderDetails = orderDetailsService.findByTrackingCode(tracking);
		
		if (orderDetails == null) 
			throw new ErrorTrackingCode("Wrong tracking code, please try again");
		
		User customer = userService.getAuthenticatedUser();
		orderDetails.setCustomer(customer);
		orderDetailsService.assignOrder(orderDetails);
	}
}
