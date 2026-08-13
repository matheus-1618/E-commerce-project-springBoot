package com.jtspringproject.JtSpringProject.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private userService userService;
	@Autowired
	private categoryService categoryService;
	@Autowired
	private productService productService;
	@Autowired
	private DataSource dataSource;

	@RequestMapping(value = {"/", "/logout"})
	public String returnIndex(HttpSession session) {
		session.removeAttribute("adminAuthenticated");
		session.removeAttribute("adminUsername");
		return "userLogin";
	}

	@GetMapping("/index")
	public String index(Model model, HttpSession session) {
		String adminUsername = (String) session.getAttribute("adminUsername");
		if (adminUsername == null || adminUsername.isEmpty()) {
			return "userLogin";
		} else {
			model.addAttribute("username", adminUsername);
			return "index";
		}
	}

	@GetMapping("login")
	public String adminlogin() {
		return "adminlogin";
	}

	@GetMapping("Dashboard")
	public String adminHome(Model model, HttpSession session) {
		Boolean authenticated = (Boolean) session.getAttribute("adminAuthenticated");
		if (Boolean.TRUE.equals(authenticated)) {
			return "adminHome";
		} else {
			return "redirect:/admin/login";
		}
	}

	@GetMapping("/loginvalidate")
	public String adminlog(Model model) {
		return "adminlogin";
	}

	@RequestMapping(value = "loginvalidate", method = RequestMethod.POST)
	public ModelAndView adminlogin(@RequestParam("username") String username, @RequestParam("password") String pass, HttpSession session) {
		User user = this.userService.checkLogin(username, pass);

		if (user != null && user.getRole() != null && user.getRole().equals("ROLE_ADMIN")) {
			ModelAndView mv = new ModelAndView("adminHome");
			session.setAttribute("adminAuthenticated", Boolean.TRUE);
			session.setAttribute("adminUsername", username);
			mv.addObject("admin", user);
			return mv;
		} else {
			ModelAndView mv = new ModelAndView("adminlogin");
			mv.addObject("msg", "Please enter correct username and password");
			return mv;
		}
	}

	@GetMapping("categories")
	public ModelAndView getcategory(HttpSession session) {
		Boolean authenticated = (Boolean) session.getAttribute("adminAuthenticated");
		if (!Boolean.TRUE.equals(authenticated)) {
			return new ModelAndView("adminlogin");
		} else {
			ModelAndView mView = new ModelAndView("categories");
			List<Category> categories = this.categoryService.getCategories();
			mView.addObject("categories", categories);
			return mView;
		}
	}

	@RequestMapping(value = "categories", method = RequestMethod.POST)
	public String addCategory(@RequestParam("categoryname") String category_name) {
		Category category = this.categoryService.addCategory(category_name);
		return "redirect:categories";
	}

	@GetMapping("categories/delete")
	public ModelAndView removeCategoryDb(@RequestParam("id") int id) {
		this.categoryService.deleteCategory(id);
		ModelAndView mView = new ModelAndView("forward:/categories");
		return mView;
	}

	@GetMapping("categories/update")
	public String updateCategory(@RequestParam("categoryid") int id, @RequestParam("categoryname") String categoryname) {
		Category category = this.categoryService.updateCategory(id, categoryname);
		return "redirect:/admin/categories";
	}

	@GetMapping("products")
	public ModelAndView getproduct(HttpSession session) {
		Boolean authenticated = (Boolean) session.getAttribute("adminAuthenticated");
		if (!Boolean.TRUE.equals(authenticated)) {
			return new ModelAndView("adminlogin");
		} else {
			ModelAndView mView = new ModelAndView("products");
			List<Product> products = this.productService.getProducts();
			if (products.isEmpty()) {
				mView.addObject("msg", "No products are available");
			} else {
				mView.addObject("products", products);
			}
			return mView;
		}
	}

	@GetMapping("products/add")
	public ModelAndView addProduct() {
		ModelAndView mView = new ModelAndView("productsAdd");
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories", categories);
		return mView;
	}

	@RequestMapping(value = "products/add", method = RequestMethod.POST)
	public String addProduct(@RequestParam("name") String name, @RequestParam("categoryid") int categoryId, @RequestParam("price") int price, @RequestParam("weight") int weight, @RequestParam("quantity") int quantity, @RequestParam("description") String description, @RequestParam("productImage") String productImage) {
		Category category = this.categoryService.getCategory(categoryId);
		Product product = new Product();
		product.setId(categoryId);
		product.setName(name);
		product.setCategory(category);
		product.setDescription(description);
		product.setPrice(price);
		product.setImage(productImage);
		product.setWeight(weight);
		product.setQuantity(quantity);
		this.productService.addProduct(product);
		return "redirect:/admin/products";
	}

	@GetMapping("products/update/{id}")
	public ModelAndView updateproduct(@PathVariable("id") int id) {
		ModelAndView mView = new ModelAndView("productsUpdate");
		Product product = this.productService.getProduct(id);
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories", categories);
		mView.addObject("product", product);
		return mView;
	}

	@RequestMapping(value = "products/update/{id}", method = RequestMethod.POST)
	public String updateProduct(@PathVariable("id") int id, @RequestParam("name") String name, @RequestParam("categoryid") int categoryId, @RequestParam("price") int price, @RequestParam("weight") int weight, @RequestParam("quantity") int quantity, @RequestParam("description") String description, @RequestParam("productImage") String productImage) {
		return "redirect:/admin/products";
	}

	@GetMapping("products/delete")
	public String removeProduct(@RequestParam("id") int id) {
		this.productService.deleteProduct(id);
		return "redirect:/admin/products";
	}

	@PostMapping("products")
	public String postproduct() {
		return "redirect:/admin/categories";
	}

	@GetMapping("customers")
	public ModelAndView getCustomerDetail(HttpSession session) {
		Boolean authenticated = (Boolean) session.getAttribute("adminAuthenticated");
		if (!Boolean.TRUE.equals(authenticated)) {
			return new ModelAndView("adminlogin");
		} else {
			ModelAndView mView = new ModelAndView("displayCustomers");
			List<User> users = this.userService.getUsers();
			mView.addObject("customers", users);
			return mView;
		}
	}

	@GetMapping("profileDisplay")
	public String profileDisplay(Model model, HttpSession session) {
		String adminUsername = (String) session.getAttribute("adminUsername");
		if (adminUsername == null || adminUsername.isEmpty()) {
			return "redirect:/admin/login";
		}

		try (Connection con = dataSource.getConnection();
			 PreparedStatement pst = con.prepareStatement("SELECT uid, username, email, password, address FROM users WHERE username = ?")) {
			pst.setString(1, adminUsername);
			try (ResultSet rst = pst.executeQuery()) {
				if (rst.next()) {
					model.addAttribute("userid", rst.getInt("uid"));
					model.addAttribute("username", rst.getString("username"));
					model.addAttribute("email", rst.getString("email"));
					model.addAttribute("address", rst.getString("address"));
				}
			}
		} catch (SQLException e) {
			model.addAttribute("error", "Unable to load profile");
		}
		return "updateProfile";
	}

	@RequestMapping(value = "updateuser", method = RequestMethod.POST)
	public String updateUserProfile(@RequestParam("userid") int userid, @RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("address") String address, HttpSession session) {
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

		try (Connection con = dataSource.getConnection();
			 PreparedStatement pst = con.prepareStatement("UPDATE users SET username = ?, email = ?, password = ?, address = ? WHERE uid = ?")) {
			pst.setString(1, username);
			pst.setString(2, email);
			pst.setString(3, hashedPassword);
			pst.setString(4, address);
			pst.setInt(5, userid);
			pst.executeUpdate();
			session.setAttribute("adminUsername", username);
		} catch (SQLException e) {
			// Silently handle — user sees stale profile on redirect
		}
		return "redirect:/admin/index";
	}
}
