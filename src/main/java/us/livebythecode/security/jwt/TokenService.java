package us.livebythecode.security.jwt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ApplicationScoped
public class TokenService {

    String issuer = "http://livebythecode.us";

    @ConfigProperty(name = "security.jwt.token.private-key")
    private String privateKeyString;

    @ConfigProperty(name = "security.jwt.token.expires-in")
    private String tokenExpirationSeconds;

    @ConfigProperty(name = "security.jwt.token.header")
    private String authHeader;

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

    private PrivateKey privateKey;

    @PostConstruct
    protected void init(){
        try {
            privateKey = decodePrivateKey(privateKeyString);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public String generateToken(String userName, List<String> roles){
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("groups",roles);
        return Jwts.builder()
            .setIssuer(issuer)
            .setSubject(userName)
            .setClaims(claims)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(getExpirationDate())
            .signWith(privateKey, signatureAlgorithm)
            .compact();
    }

    private Date getExpirationDate(){
        return new Date(System.currentTimeMillis() + Long.parseLong(tokenExpirationSeconds) * 1000);
    }

    private static PrivateKey decodePrivateKey(final String pemEncoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
         return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pemEncoded)));
    }

}