package ru.practicum.main.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.main.exception.InvalidPageParametersException;

@UtilityClass
public class Page {
    public static Pageable paged(Integer from, Integer size) {
        Pageable page;
        if (from != null && size != null) {
            if (from < 0 || size < 0) {
                throw new InvalidPageParametersException("Неправильные параметры страницы.");
            }
            page = PageRequest.of(from > 0 ? from / size : 0, size);
        } else {
            page = PageRequest.of(0, 10);
        }
        return page;
    }

    public static Pageable paged(Integer from, Integer size, String sort) {
        if (sort.equals("EVENT_DATE")) {
            sort = "eventDate";
        } else {
            sort = "views";
        }
        Pageable page;
        if (from != null && size != null) {
            if (from < 0 || size < 0) {
                throw new InvalidPageParametersException("Неправильные параметры страницы.");
            }
            page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, sort));
        } else {
            page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, sort));
        }
        return page;
    }
}