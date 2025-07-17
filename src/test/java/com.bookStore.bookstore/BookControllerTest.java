package com.bookStore.bookstore.controllers;

import com.bookStore.bookstore.model.Book;
import com.bookStore.bookstore.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerTest {

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookController bookController;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
	}

	@Test
	@WithMockUser(roles = "EMPLOYEE")
	public void increaseQuantity_ShouldIncreaseBookQuantity_WhenEmployeeRole() throws Exception {
		Book book = new Book();
		book.setId(1L);
		book.setQuantity(10);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		mockMvc.perform(post("/books/1/increase")
						.param("amount", "5"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/books/1"));

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(any(Book.class));
	}

	@Test
	@WithMockUser(roles = "EMPLOYEE")
	public void increaseQuantity_ShouldNotIncrease_WhenNegativeAmount() throws Exception {
		Book book = new Book();
		book.setId(1L);
		book.setQuantity(10);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		mockMvc.perform(post("/books/1/increase")
						.param("amount", "-5"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/books/1"));

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(argThat(b -> b.getQuantity() == 10));
	}

	@Test
	@WithMockUser(roles = "EMPLOYEE")
	public void decreaseQuantity_ShouldDecreaseBookQuantity_WhenEmployeeRole() throws Exception {
		Book book = new Book();
		book.setId(1L);
		book.setQuantity(10);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		mockMvc.perform(post("/books/1/decrease")
						.param("amount", "3"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/books/1"));

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(argThat(b -> b.getQuantity() == 7));
	}

	@Test
	@WithMockUser(roles = "EMPLOYEE")
	public void decreaseQuantity_ShouldNotGoBelowZero() throws Exception {
		Book book = new Book();
		book.setId(1L);
		book.setQuantity(5);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		mockMvc.perform(post("/books/1/decrease")
						.param("amount", "10"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/books/1"));

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(argThat(b -> b.getQuantity() == 0));
	}

	@Test
	@WithMockUser(roles = "EMPLOYEE")
	public void increaseQuantity_ShouldHandleNonExistingBook() throws Exception {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		mockMvc.perform(post("/books/999/increase")
						.param("amount", "5"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/books/999"));

		verify(bookRepository).findById(999L);
		verify(bookRepository, never()).save(any());
	}
}
