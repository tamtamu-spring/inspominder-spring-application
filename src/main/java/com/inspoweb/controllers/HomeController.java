package com.inspoweb.controllers;

import com.inspoDataBase.entity.User;
import com.inspoDataBase.jpaUsageDataBase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


/**
 * @author mmikilchenko on 01.03.2017.
 */
@Controller
@RequestMapping(value = {"/home", "/"})
public class HomeController {

    private UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showRegistrationForm() {

        return "home";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "registerForm";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistration(@Valid User user, BindingResult result) {
        if (userNameAlreadyExisted(user)) {
            result.addError(new FieldError("user", "userName", "User with this name is already existed."));
        }
        if (result.hasErrors()) {
            return "registerForm";
        }
        userService.addUser(user);

        return "redirect:/user/" + user.getUserName();
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginForm(Model model, String error, String logout) {
        model.addAttribute("user", new User());
        if (error != null)
            model.addAttribute("error", "Your username or password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    private boolean userNameAlreadyExisted(User user) {
        return userService.findUserByUsername(user.getUserName()) != null ? true : false;
    }

}