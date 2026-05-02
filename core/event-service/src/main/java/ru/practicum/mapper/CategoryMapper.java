package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.event.category.CategoryDto;
import ru.practicum.models.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category entity);

    Category toEntity(CategoryDto dto);
}
