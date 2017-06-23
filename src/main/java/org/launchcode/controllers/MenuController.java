package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msroc on 6/21/2017.
 */
@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    // Request path: /menu
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                         Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id){
        Menu menu = menuDao.findOne(id);
        model.addAttribute("menu", menu);
        model.addAttribute("title", menu.getName());
        return "/menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id){
        Menu menu = menuDao.findOne(id);
        ArrayList<Cheese> cheeses = (ArrayList<Cheese>) cheeseDao.findAll();
        AddMenuItemForm addItemForm = new AddMenuItemForm(menu, cheeses);
        model.addAttribute("menu", menu);
        model.addAttribute("form", addItemForm);
        model.addAttribute("title", "Add Item to menu: " + menu.getName());
        return "/menu/add-item";
    }

    @RequestMapping(value = "menu/add-item/{id}", method = RequestMethod.POST)
    public String processAddItem(@ModelAttribute @Valid AddMenuItemForm form,
                          Errors errors, @RequestParam int cheeseId, Model model, @PathVariable int id) {

        Menu menu = form.getMenu();

        if (errors.hasErrors()) {
            model.addAttribute("menu", menu);
            model.addAttribute("form", form);
            model.addAttribute("title", "Add Item to menu: " + menu.getName());
            return "menu/add-item";
        }

        Cheese cheese = cheeseDao.findOne(cheeseId);
        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }
}
