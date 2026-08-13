package com.jtspringproject.JtSpringProject.controller;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.services.cartService;
import com.jtspringproject.JtSpringProject.services.userService;
import com.jtspringproject.JtSpringProject.services.productService;


@Controller
public class UserController {

	@Autowired
	private userService userService;

	@Autowired
	private productService productService;

	@Autowired
	private cartService cartService;

	@GetMapping("/register")
	public String registerUser() {
		return "register";
	}

	@GetMapping("/buy")
	public String buy() {
		return "buy";
	}

	@GetMapping("/")
	public String userlogin(Model model) {
		return "userLogin";
	}

	@RequestMapping(value = "userloginvalidate", method = RequestMethod.POST)
	public ModelAndView userlogin(@RequestParam("username") String username, @RequestParam("password") String pass, Model model, HttpServletResponse res) {
		User u = this.userService.checkLogin(username, pass);
		if (u != null && u.getUsername() != null && u.getUsername().equals(username)) {
			res.addCookie(new Cookie("username", u.getUsername()));
			ModelAndView mView = new ModelAndView("index");
			mView.addObject("user", u);
			List<Product> products = this.productService.getProducts();
			if (products.isEmpty()) {
				mView.addObject("msg", "No products are available");
			} else {
				mView.addObject("products", products);
			}
			return mView;
		} else {
			ModelAndView mView = new ModelAndView("userLogin");
			mView.addObject("msg", "Please enter correct email and password");
			return mView;
		}
	}

	@GetMapping("/user/products")
	public ModelAndView getproduct() {
		ModelAndView mView = new ModelAndView("uproduct");
		List<Product> products = this.productService.getProducts();
		if (products.isEmpty()) {
			mView.addObject("msg", "No products are available");
		} else {
			mView.addObject("products", products);
		}
		return mView;
	}

	@RequestMapping(value = "newuserregister", method = RequestMethod.POST)
	public String newUseRegister(@ModelAttribute User user) {
		user.setRole("ROLE_NORMAL");
		this.userService.addUser(user);
		return "redirect:/";
	}

	@GetMapping("/cart")
	public ModelAndView getCart() {
		ModelAndView mView = new ModelAndView("cartproduct");
		List<Cart> carts = this.cartService.getCarts();
		mView.addObject("cartItems", carts);
		return mView;
	}

	@GetMapping("/test")
	public String Test(Model model) {
		model.addAttribute("author", "jay gajera");
		model.addAttribute("id", 40);
		List<String> friends = new ArrayList<String>();
		model.addAttribute("f", friends);
		friends.add("xyz");
		friends.add("abc");
		return "test";
	}

	@GetMapping("/test2")
	public ModelAndView Test2() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("name", "jay gajera 17");
		mv.addObject("id", 40);
		mv.setViewName("test2");
		List<Integer> list = new ArrayList<Integer>();
		list.add(10);
		list.add(25);
		mv.addObject("marks", list);
		return mv;
	}
}
