package ratustele.pacpac.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ratustele.pacpac.models.AuthenticationRequest;
import ratustele.pacpac.models.AuthenticationResponse;
import ratustele.pacpac.repositories.EntityRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final EntityRepository repository;
    private final JwtService jwtService;

    /**
     * Method that authenticates user and creates a JWT for him.
     * @param request The request body: a valid email and password.
     * @return Returns an error or an exception.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
