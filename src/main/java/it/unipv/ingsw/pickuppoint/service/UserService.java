package it.unipv.ingsw.pickuppoint.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import it.unipv.ingsw.pickuppoint.data.RoleRepo;
import it.unipv.ingsw.pickuppoint.data.UserRepo;
import it.unipv.ingsw.pickuppoint.model.User;
import it.unipv.ingsw.pickuppoint.model.entity.Role;
import it.unipv.ingsw.pickuppoint.service.exception.CustomerAlreadyExistException;

@Service
public class UserService {
	@Autowired
	UserRepo userRepo;
	@Autowired
	RoleRepo roleRepo;
	@Autowired

	BCryptPasswordEncoder passwordEncoder;
	
	
	public List<User> getUserByRole(String name) {
		return userRepo.findByRole_name(name);
	}

	/**
	 * @return utente autenticato
	 */
	public User getAuthenticatedUser() {
		return ((UserAuthorization) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}
	
	/**
	 * Questo metodo effettua la registrazione del Customer
	 * 
	 * @param customer da registrare
	 * @throws CustomerAlreadyExistException
	 */
	public void register(User customer) throws CustomerAlreadyExistException {
		if (checkIfUserExist(customer.getEmail())) {
			throw new CustomerAlreadyExistException("Customer already exists for this email");
		}
		customer.setEnabled(true);
		customer.setRole(getCustomerRole());
		encodePassword(customer);
		userRepo.save(customer);
	}

	/**
	 * Restituisce il Customer con chiave primaria id
	 * 
	 * @param id
	 * @return Customer
	 */
	public User getCustomer(Long id) {
		return userRepo.findByUserId(id);
	}

	/**
	 * Questo metodo restitusce il ruole associato al Customer, utile quando il
	 * customer viene registrato per settare il ruolo
	 * 
	 * @return Ruolo Customer
	 */
	private Role getCustomerRole() {
		Role role = roleRepo.findByName("CUSTOMER");
		return role;
	}

	/**
	 * Questo metodo cripta la password del Customer e la inserisce nella propria
	 * istanza
	 * 
	 * @param customer
	 */
	private void encodePassword(User customer) {
		passwordEncoder = new BCryptPasswordEncoder();
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
	}

	/**
	 * Verifica se all'interno dell'entita User è presente un record contente
	 * l'email passata
	 * 
	 * @param email
	 * @return true se esiste, altrimenti false
	 */
	private boolean checkIfUserExist(String email) {
		if (userRepo.findByEmail(email) != null) {
			return true;
		}
		return false;
	}
}
	
	
