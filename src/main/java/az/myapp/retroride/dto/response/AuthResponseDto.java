package az.myapp.retroride.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;   // JWT token — sonrakı sorğularda "Authorization: Bearer TOKEN" göndərəcəklər
    private String name;
    private String email;
}
