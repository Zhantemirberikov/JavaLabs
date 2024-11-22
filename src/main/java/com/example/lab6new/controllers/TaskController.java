package com.example.lab6new.controllers;

import com.example.lab6new.models.Task;
import com.example.lab6new.models.User;
import com.example.lab6new.models.Category;
import com.example.lab6new.repositories.TaskRepository;
import com.example.lab6new.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskController(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    // Отображение списка задач с пагинацией и поиском
    @GetMapping
    public String listTasks(@AuthenticationPrincipal User user,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String keyword,
                            Model model) {
        Pageable pageable = PageRequest.of(page, 10); // Пагинация: 10 задач на странице
        Page<Task> tasks;

        if (keyword == null || keyword.isBlank()) {
            tasks = taskRepository.findByUser(user, pageable); // Передача user и pageable
        } else {
            tasks = taskRepository.findByUserAndTitleContaining(user, keyword, pageable); // Поиск с пагинацией
        }

        model.addAttribute("tasks", tasks.getContent()); // Задачи текущей страницы
        model.addAttribute("page", tasks); // Для пагинации
        model.addAttribute("categories", categoryRepository.findAll()); // Категории для фильтрации
        return "task-list";
    }

    // Форма добавления новой задачи
    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryRepository.findAll());
        return "task-form";
    }

    // Добавление новой задачи
    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, @AuthenticationPrincipal User user, Model model) {
        if (task.getDueDate().isBefore(LocalDate.now())) {
            model.addAttribute("error", "Дата выполнения не может быть в прошлом");
            model.addAttribute("categories", categoryRepository.findAll());
            return "task-form";
        }
        task.setUser(user);

        if (task.getCategory() == null) {
            task.setCategory(categoryRepository.findById(1L).orElse(null));
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    // Форма редактирования задачи
    @GetMapping("/{id}/edit")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Неверный ID задачи: " + id));
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryRepository.findAll());
        return "task-form";
    }

    // Редактирование задачи
    @PostMapping("/{id}/edit")
    public String editTask(@PathVariable Long id, @ModelAttribute Task task, @AuthenticationPrincipal User user, Model model) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Неверный ID задачи: " + id));

        if (task.getDueDate().isBefore(LocalDate.now())) {
            model.addAttribute("error", "Дата выполнения не может быть в прошлом");
            model.addAttribute("categories", categoryRepository.findAll());
            return "task-form";
        }

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setUser(user);

        if (task.getCategory() != null) {
            existingTask.setCategory(task.getCategory());
        }

        taskRepository.save(existingTask);
        return "redirect:/tasks";
    }

    // Удаление задачи
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }

    // Фильтрация задач по категории
    @GetMapping("/filter")
    public String filterTasks(@RequestParam(required = false) Long categoryId,
                              @AuthenticationPrincipal User user, Model model) {
        List<Task> tasks;

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            tasks = taskRepository.findByUserAndCategory(user, category);
        } else {
            Pageable pageable = PageRequest.of(0, 10); // Пагинация по умолчанию для фильтрации
            tasks = taskRepository.findByUser(user, pageable).getContent();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryRepository.findAll());
        return "task-list";
    }
}



