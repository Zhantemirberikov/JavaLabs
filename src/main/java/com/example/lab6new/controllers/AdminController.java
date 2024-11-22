package com.example.lab6new.controllers;

import com.example.lab6new.models.Task;
import com.example.lab6new.models.User;
import com.example.lab6new.repositories.TaskRepository;
import com.example.lab6new.repositories.UserRepository;
import com.example.lab6new.repositories.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public AdminController(UserRepository userRepository, TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    // Просмотр всех пользователей
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-user-list";
    }

    // Просмотр всех задач
    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "admin-task-list";
    }

    // Просмотр всех категорий
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin-category-list";
    }

    // Удаление пользователя
    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}

