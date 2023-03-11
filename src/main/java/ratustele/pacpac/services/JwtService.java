package ratustele.pacpac.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "7234753778214125442A472D4B6150645367566B59703373367638792F423F45";

    /**
     * Method that retrieves the username from a Claim.
     * @param token JWT token.
     * @return Returns an user.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Method that retrieves a Claim from a JWT.
     * @param token JWT token.
     * @param claimsResolver Function.
     * @return Returns a claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Method that generates a JWT
     * @param userDetails The details of the user for whom the JWT must be created.
     * @return Returns a token.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Method that generates a JWT
     * @param extraClaims Extra date for the JWT.
     * @param userDetails The details of the user for whom the JWT must be created.
     * @return Returns a token.
     */
    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Method that verifies if a JWT is valid.
     * @param token JWT token.
     * @param userDetails The details of the user in question.
     * @return Returns a boolean value.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * A method that verifies if a JWT is expired.
     * @param token A JWT.
     * @return Returns a boolean value.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Method that retrieves the expiration date for a JWT.
     * @param token A JWT.
     * @return Returns a date.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Method that retrieves all Claims form a JWT.
     * @param token A JWT.
     * @return Returns Claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
