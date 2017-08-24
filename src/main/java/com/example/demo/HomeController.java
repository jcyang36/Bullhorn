package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    private UserService userService;
    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping(value="/register", method= RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }



    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String processRegistrationPage(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model
    ){ model.addAttribute("user", user);


        if(result.hasErrors()){
            return "registration";
        }
        else{
            userService.saveUser(user);
            model.addAttribute("message","User Account Successfully Created");
        }
        return "redirect:/";
    }
    @RequestMapping("/")
    public String listMessages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String messageForm(Model model) {
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Message message, BindingResult result, @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return "messageform";
        }

        try {
            Map uploadResult=cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype","auto"));
            message.setImage(uploadResult.get("url").toString());
         //  String filename= cloudc.createUrl(uploadResult.get("url").toString(),30,40,"scale");
           // message.setImage(filename);
            Calendar calendar = Calendar.getInstance();
            java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
            message.setPosteddate(ourJavaDateObject);
            messageRepository.save(message);

        } catch(IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }

        return "redirect:/";

    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message",messageRepository.findOne(id));
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message",messageRepository.findOne(id));
        return "messageform";
    }
    @RequestMapping("/delete/{id}")
    public String delCourse(@PathVariable("id") long id, Model model){
        model.addAttribute("message",messageRepository.findOne(id));
        messageRepository.delete(id);
        return "redirect:/";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }

}
