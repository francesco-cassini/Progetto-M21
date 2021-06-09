package it.unipv.ingsw.pickuppoint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import it.unipv.ingsw.pickuppoint.service.HubService;
import it.unipv.ingsw.pickuppoint.service.OrderDetailsService;
import it.unipv.ingsw.pickuppoint.service.UserService;
import it.unipv.ingsw.pickuppoint.service.exception.ErrorPickupCode;
import it.unipv.ingsw.pickuppoint.service.exception.ErrorTrackingCode;

@Controller
public class CustomerController {
	@Autowired
	UserService userService;
	@Autowired
	OrderDetailsService orderDetailsService;
	@Autowired
	HubService hubService;

	/**
	 * Questo metodo viene invocato quando il client effettua una richiesta GET a
	 * /add. Aggiunge un prodotto nella lista dei prodotti del Customer tramite
	 * inserimento del tracking code; Viene aggiunto il riferimento di chiave
	 * esterna (CustomerID) all'interno dell'entità genitore OrderDetails
	 * 
	 * @param tracking code dell'ordine
	 * @return reindirizzamento alla pagina html della vista degli ordini
	 */
	@RequestMapping(value = "/add")
	public String addOrder(@RequestParam(name = "tracking") int tracking, Model model) {
		try {
			hubService.addOrder(tracking);
		} catch (ErrorTrackingCode e) {
			userService.addListOrders(model);
			model.addAttribute("error", e.getMessage());
			return "/profile";
		}
		return "redirect:" + "/Orders";
	}

	/**
	 * Questo metodo viene invocato quando il client effettua una richiesta POST a
	 * /withdraw/{id}; Permette al Customer di ritirare l'ordine; Viene settato
	 * DeliveryStatus = WITHDRAW e WithdrawalDate
	 * 
	 * @param id ordine da ritirare
	 * @return reindirizzamento alla pagina per il recupero e la visualizzazione
	 *         degli ordini del Customer attraverso una richesta GET da parte del
	 *         client
	 * 
	 */
	@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
	public String showEditProductForm(@RequestParam(name = "pickupCode") String pickupCode, Model model)
			throws ErrorPickupCode {
		try {
			hubService.withdraw(pickupCode);
		} catch (ErrorPickupCode e) {
			userService.addListOrders(model);
			model.addAttribute("error", e.getMessage());
			return "/viewOrders";
		}
		return "redirect:" + "/Orders";
	}
}
