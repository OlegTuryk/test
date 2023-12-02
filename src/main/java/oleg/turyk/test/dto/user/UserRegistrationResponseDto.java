package oleg.turyk.test.dto.user;

public record UserRegistrationResponseDto(Long id, String email,
                                          String firstName, String lastName) {
}
