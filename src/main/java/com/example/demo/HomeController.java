package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;
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

    @GetMapping("/addimage")
    public String messageFormImage(Model model) {
        model.addAttribute("message", new Message());
        return "messageformimage";
    }
    @GetMapping("/addtext")
    public String messageFormText(Model model) {
        model.addAttribute("message", new Message());
        return "messageformtext";
    }
   /* @RequestMapping(method = RequestMethod.POST, value = "/object")
    ResponseEntity<Object> create(@RequestParam("is_file") boolean isFile, HttpServletRequest request) {
        if (isFile) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartRequest.getFile("file");
        } else {
            System.out.println("no file");
        }
        return null;
    }*/


   /* ProcessFormImage  */
   @PostMapping("/processformimage")
    public String processFormImage(@ModelAttribute Message message, @RequestParam("file") MultipartFile file, Principal principal) {
        if (file.isEmpty()) {
            return "redirect:/addimage";
        }

       try {
           Map uploadResult=cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype","auto"));
           message.setImage(uploadResult.get("url").toString());
           messageRepository.save(message);

       } catch(IOException e){
           e.printStackTrace();
           return "redirect:/processformimage";
       }

        Calendar calendar = Calendar.getInstance();
        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
        String username = principal.getName();
        User user_current = userRepository.findByUsername(username);
        message.setUser(user_current);
        message.setSentby(message.getUser().getUsername());
        message.setPosteddate(ourJavaDateObject);
        messageRepository.save(message);

        return "redirect:/";

    }
/*ProcessFormImage*/
/* ProcessFormText*/
    @PostMapping("/processformtext")
    public String processFormText(@Valid Message message, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "messageformtext";
        }


        Calendar calendar = Calendar.getInstance();
        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
        String username = principal.getName();
        User user_current = userRepository.findByUsername(username);
        message.setUser(user_current);
        message.setSentby(message.getUser().getUsername());
        message.setPosteddate(ourJavaDateObject);
        messageRepository.save(message);

        return "redirect:/";

    }

    /* ProcessFormText*/
    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message",messageRepository.findOne(id));
        return "show";
    }
    @RequestMapping("/edittextmessage/{id}")
    public String editTextMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message",messageRepository.findOne(id));
        return "messageformtext";
    }

    @RequestMapping("/editimagemessage/{id}")
    public String editImageMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message",messageRepository.findOne(id));
        return "messageformimage";
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
