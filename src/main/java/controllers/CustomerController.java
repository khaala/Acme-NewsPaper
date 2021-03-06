/*
 * CustomerController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import domain.CreditCard;
import domain.Customer;
import domain.NewsPaper;
import forms.CustomerForm;
import forms.SubscribeForm;
import forms.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import services.CustomerService;

import javax.validation.Valid;
import java.util.Collection;

@Controller
@RequestMapping("/customer")
public class CustomerController extends AbstractController {

	//Services -------------------------------------------------------

	@Autowired
	private CustomerService customerService;


	// edition ---------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam int customerId){
		ModelAndView result;
		Customer customer;
		Assert.notNull(customerId);
		customer = customerService.findOne(customerId);

		result = createEditModelAndView(customer);

		return result;
	}


	@RequestMapping(value = "/edit", method = RequestMethod.POST,params = "save")
	public ModelAndView edit(@Valid Customer customer, BindingResult bindingResult){
		ModelAndView result;
		if(bindingResult.hasErrors())
			result = createEditModelAndView(customer);
		else{
			try{
				customerService.save(customer);
				result = new ModelAndView("redirect: list.do");

			}catch (Throwable oops){
				result = createEditModelAndView(customerService.create(),"customer.save.error");
			}
		}
		return result;
	}

	//Edition --------------------------------------------------------------------

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView edit() {

		ModelAndView result;
		result = new ModelAndView("customer/editForm");

		result.addObject("customerForm", new CustomerForm());

		return result;
	}

	// Save ------------------------------------------------------------------------

	@RequestMapping(value = "/register", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final CustomerForm customerForm, final BindingResult binding) {
		ModelAndView result;
		Customer customer;

		try {
			customer = this.customerService.reconstruct(customerForm, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView2(customerForm, "customer.save.error");
			else {
				result = new ModelAndView("redirect:/welcome/index.do");
				this.customerService.save(customer);

			}
		} catch (final Throwable oops) {
			result = this.createEditModelAndView2(customerForm, "customer.save.error");
		}

		return result;
	}





	// Ancillary methods


	private ModelAndView createEditModelAndView(final Customer customer) {

		return this.createEditModelAndView(customer, null);
	}

	private ModelAndView createEditModelAndView(final Customer customer, final String message) {

		final ModelAndView result = new ModelAndView("customer/edit");

		result.addObject("customer", customer);
		result.addObject("message", message);

		return result;
	}

	private ModelAndView createEditModelAndView2(CustomerForm customerForm, String messageCode) {

		ModelAndView res;
		res = new ModelAndView("customer/editForm");

		res.addObject("customerForm", customerForm);
//		res.addObject("requestURI", "customer/list.do");
//		res.addObject("cancelUri", "customer/list.do");
		res.addObject("message", messageCode);

		return res;
	}
}


