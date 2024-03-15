package canal.plus.subscriber.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SubscriberPersonalInfo(@NotBlank(message = "Firstname is mandatory")
                                        String firstname,

                                     @NotBlank(message = "Lastname is mandatory")
                                        String lastname,

                                     @Email
                                        @NotBlank(message = "Email is mandatory")
                                        String mail,

                                     @NotBlank(message = "Phone number is mandatory")
                                        @Size(min = 3, max = 15, message = "Phone number should have at least 3 or less than 17 digits")
                                        @Pattern(regexp = "[+]?\\d*",  message = "Only + and numeric digits are authorized for phone number")
                                        String phone) {
}
