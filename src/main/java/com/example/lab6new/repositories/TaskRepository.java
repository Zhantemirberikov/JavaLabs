package com.example.lab6new.repositories;

import com.example.lab6new.models.Task;
import com.example.lab6new.models.User;
import com.example.lab6new.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUser(User user, Pageable pageable);


    Page<Task> findByUserAndTitleContaining(User user, String keyword, Pageable pageable);


    List<Task> findByUserAndCategory(User user, Category category);
}
