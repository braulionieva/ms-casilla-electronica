package pe.gob.mpfn.casilla.notifications.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.util.enums.AppParams;
import pe.gob.mpfn.casilla.notifications.util.enums.LoginType;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(
            AccountRecord userDetails
    ) {

        var sessionType = LoginType.PERSONA_NATURAL.getValue();
        if (AppParams.ES_ABOGADO.getValue().equals(userDetails.getAbogado())) {
            sessionType = LoginType.ABOGADO.getValue();
        }

        Map<String, Object> claims = Map.of(
                "userName", "%s %s %s".formatted(
                        userDetails.getNombre(),
                        userDetails.getApePat(),
                        Objects.toString(userDetails.getApeMat(), "")),
                "email", userDetails.getCorreo(),
                "tipo", userDetails.getTipoUsr(),
                "numeroDocumento", userDetails.getNumDoc(),
                "abogado", userDetails.getAbogado(),
                "session", sessionType,
                "defensor", userDetails.getDefensorPublico(),
                "casilla", userDetails.getCuentaId()
        );
        return buildToken(claims, jwtExpiration);
    }

    public String generateTokenOnChageProfile(
            AccountRecord userDetails, String session
    ) {
        Map<String, Object> claims = Map.of(
                "userName", "%s %s %s".formatted(
                        userDetails.getNombre(),
                        userDetails.getApePat(),
                        Objects.toString(userDetails.getApeMat(), "")),
                "email", userDetails.getCorreo(),
                "tipo", userDetails.getTipoUsr(),
                "numeroDocumento", userDetails.getNumDoc(),
                "abogado", userDetails.getAbogado(),
                "session", session,
                "defensor", userDetails.getDefensorPublico(),
                "casilla", userDetails.getCuentaId()
        );
        return buildToken(claims, jwtExpiration);
    }

    public String generateTokenWithExpiration(AccountRecord accountRecord, long expiration) {
        Map<String, Object> claims = Map.of(
                "userName", accountRecord.getNombre(),
                "email", accountRecord.getCorreo(),
                "tipo", accountRecord.getTipoUsr(),
                "numeroDocumento", accountRecord.getNumDoc()
        );
        return buildToken(claims, expiration);
    }


    public String buildToken(
            Map<String, Object> claims,
            long expiration
    ) {
        return Jwts
                .builder()
                .addClaims(claims)
                .setSubject(claims.get("numeroDocumento").toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiration)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
