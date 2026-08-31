package com.jtspringproject.JtSpringProject.controller;

import com.jtspringproject.JtSpringProject.models.PageResult;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.services.userService;
import com.jtspringproject.JtSpringProject.services.productService;

@Controller
public class UserController {

	private final userService userService;
	private final productService productService;

	@Autowired
	public UserController(userService userService, productService productService) {
		this.userService = userService;
		this.productService = productService;
	}

	@GetMapping("/register")
	public String registerUser() {
		return "register";
	}

	@GetMapping("/buy")
	public String buy() {
		return "buy";
	}

	@GetMapping("/login")
	public ModelAndView userLogin(@RequestParam(required = false) String error) {
		ModelAndView mv = new ModelAndView("userLogin");
		if ("true".equals(error)) {
			mv.addObject("msg", "Please enter correct email and password");
		}
		return mv;
	}

	@GetMapping("/")
	public ModelAndView indexPage(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "name,asc") String sort) {
		ModelAndView mView = new ModelAndView("index");
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		mView.addObject("username", username);
		PageResult<Product> result = this.productService.getProducts(page, size, sort);

		if (result.isEmpty() && result.getTotalElements() == 0) {
			mView.addObject("msg", "No products are available");
		}
		addPaginationAttributes(mView, result);
		mView.addObject("products", result.getContent());
		return mView;
	}

	@GetMapping("/user/products")
	public ModelAndView getProducts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "name,asc") String sort) {
		ModelAndView mView = new ModelAndView("uproduct");
		PageResult<Product> result = this.productService.getProducts(page, size, sort);

		if (result.isEmpty() && result.getTotalElements() == 0) {
			mView.addObject("msg", "No products are available");
		}
		addPaginationAttributes(mView, result);
		mView.addObject("products", result.getContent());
		return mView;
	}

	@PostMapping("newuserregister")
	public ModelAndView registerNewUser(@ModelAttribute User user) {
		boolean exists = this.userService.checkUserExists(user.getUsername());

		if (!exists) {
			user.setRole("ROLE_NORMAL");
			this.userService.addUser(user);
			return new ModelAndView("userLogin");
		} else {
			ModelAndView mView = new ModelAndView("register");
			mView.addObject("msg", user.getUsername() + " is taken. Please choose a different username.");
			return mView;
		}
	}

	@GetMapping("/profileDisplay")
	public String profileDisplay(Model model) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByUsername(username);

		if (user != null) {
			model.addAttribute("userid", user.getId());
			model.addAttribute("username", user.getUsername());
			model.addAttribute("email", user.getEmail());
			model.addAttribute("password", "");
			model.addAttribute("address", user.getAddress());
		} else {
			model.addAttribute("msg", "User not found");
		}

		return "updateProfile";
	}

	@PostMapping("/updateuser")
	public String updateUserProfile(@RequestParam("userid") int userid,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam("address") String address) {
		User updatedUser = this.userService.updateUserProfile(userid, username, email, password, address);
		if (updatedUser != null) {
			refreshAuthenticatedPrincipal(username);
		}
		return "redirect:/";
	}

	private void addPaginationAttributes(ModelAndView mView, PageResult<?> result) {
		mView.addObject("currentPage", result.getCurrentPage());
		mView.addObject("totalPages", result.getTotalPages());
		mView.addObject("totalElements", result.getTotalElements());
		mView.addObject("pageSize", result.getPageSize());
		mView.addObject("sortField", result.getSortField());
		mView.addObject("sortDirection", result.getSortDirection());
		mView.addObject("hasNext", result.isHasNext());
		mView.addObject("hasPrevious", result.isHasPrevious());
		mView.addObject("startItem", result.getStartItem());
		mView.addObject("endItem", result.getEndItem());

		int pageStart = Math.max(0, result.getCurrentPage() - 2);
		int pageEnd = Math.min(result.getTotalPages() - 1, pageStart + 4);
		if (pageEnd - pageStart < 4) {
			pageStart = Math.max(0, pageEnd - 4);
		}
		mView.addObject("pageStart", pageStart);
		mView.addObject("pageEnd", pageEnd);
	}

	private void refreshAuthenticatedPrincipal(String username) {
		Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
				username,
				currentAuthentication.getCredentials(),
				currentAuthentication.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
	}

}