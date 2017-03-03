package com.inspoweb.controllers;

import com.inspoDataBase.entity.Reminder;
import com.inspoDataBase.entity.User;
import com.inspoDataBase.jpaUsageDataBase.service.ReminderService;
import com.inspoDataBase.jpaUsageDataBase.service.UserService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author mmikilchenko on 27.02.2017.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
    private UserService userService;
    private ReminderService reminderService;

    @Autowired
    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Autowired
    public UserController(UserService userService, ReminderService reminderService) {
        this.userService = userService;
        this.reminderService = reminderService;
    }

    //registration
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "registerForm";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistration(@Valid User user, BindingResult resul) {
        userService.addUser(user);

        if (resul.hasErrors()) {
            return "registerForm";
        }

        return "redirect:/user/" + user.getUserId();
    }

    //login
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());

        return "loginForm";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(@Valid User user, BindingResult resul) {
        User existedUser = userService.findUserByUsernameAndPassword(user.getUserName(), user.getPassword());
        if (existedUser == null || resul.hasErrors()) {
            return "loginForm";
        }

  /*      return Optional.ofNullable("redirect:/user/" + existedUser.getUserId())
                .orElse("loginForm");*/
        return "redirect:/user/" + existedUser.getUserId();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showUserProfile(@PathVariable Integer id, Model model) {
        User user = userService.findUserByUserId(id);
        List<Reminder> userReminders = reminderService.findReminderByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("reminderList", userReminders);
        model.addAttribute("reminder", new Reminder());
        return "profile";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String addReminderOnUserProfile(@PathVariable Integer id, @Valid Reminder reminder, BindingResult resul) {

        User user = userService.findUserByUserId(id);
        if (!resul.hasErrors()) {
            reminderService.addReminder(reminder, user);
        }else{

            alert.setTitle("Message Here...");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText("I have a great message for you!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });

        }
        return "redirect:/user/{id}";

    }


}
