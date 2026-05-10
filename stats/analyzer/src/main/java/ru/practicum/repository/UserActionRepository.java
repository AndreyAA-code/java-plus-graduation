package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.UserAction;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}
