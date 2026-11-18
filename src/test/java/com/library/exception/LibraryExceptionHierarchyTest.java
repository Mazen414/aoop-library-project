package com.library.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryExceptionHierarchyTest {

    @Test
    void shouldDeclareBaseLibraryExceptionAsAbstractUncheckedException() {
        // Arrange
        Class<LibraryException> baseClass = LibraryException.class;

        // Act
        boolean isAbstract = Modifier.isAbstract(baseClass.getModifiers());
        boolean extendsRuntimeException = RuntimeException.class.isAssignableFrom(baseClass);

        // Assert
        assertTrue(isAbstract);
        assertTrue(extendsRuntimeException);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "title must not be blank";

        // Act
        InvalidLibraryArgumentException exception = assertThrows(
                InvalidLibraryArgumentException.class,
                () -> {
                    throw new InvalidLibraryArgumentException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowItemNotFoundExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Item not found: ITEM-99";

        // Act
        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> {
                    throw new ItemNotFoundException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowDuplicateItemExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Duplicate item ID: ITEM-1";

        // Act
        DuplicateItemException exception = assertThrows(
                DuplicateItemException.class,
                () -> {
                    throw new DuplicateItemException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowItemNotAvailableExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Item is currently borrowed: ITEM-1";

        // Act
        ItemNotAvailableException exception = assertThrows(
                ItemNotAvailableException.class,
                () -> {
                    throw new ItemNotAvailableException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowPatronNotFoundExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Patron not found: PAT-99";

        // Act
        PatronNotFoundException exception = assertThrows(
                PatronNotFoundException.class,
                () -> {
                    throw new PatronNotFoundException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowDuplicatePatronExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Duplicate patron ID: PAT-1";

        // Act
        DuplicatePatronException exception = assertThrows(
                DuplicatePatronException.class,
                () -> {
                    throw new DuplicatePatronException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowBorrowingLimitExceededExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Student borrowing limit of 3 reached";

        // Act
        BorrowingLimitExceededException exception = assertThrows(
                BorrowingLimitExceededException.class,
                () -> {
                    throw new BorrowingLimitExceededException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowLoanNotFoundExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Loan not found: LOAN-99";

        // Act
        LoanNotFoundException exception = assertThrows(
                LoanNotFoundException.class,
                () -> {
                    throw new LoanNotFoundException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowInvalidLoanOperationExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Return date cannot be before borrow date";

        // Act
        InvalidLoanOperationException exception = assertThrows(
                InvalidLoanOperationException.class,
                () -> {
                    throw new InvalidLoanOperationException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionAsLibraryExceptionWithMessage() {
        // Arrange
        String message = "Payment amount must be positive";

        // Act
        InvalidFinePaymentException exception = assertThrows(
                InvalidFinePaymentException.class,
                () -> {
                    throw new InvalidFinePaymentException(message);
                }
        );

        // Assert
        assertTrue(LibraryException.class.isAssignableFrom(exception.getClass()));
        assertEquals(message, exception.getMessage());
    }
}
