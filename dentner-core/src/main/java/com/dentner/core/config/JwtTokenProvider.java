package com.dentner.core.config;

import com.dentner.core.cmmn.exception.TokenValidationException;
import com.dentner.core.cmmn.dto.TokenDto;
import com.dentner.core.cmmn.vo.MemberVo;
import com.dentner.core.cmmn.vo.TokenVo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    public static final String BEARER_TYPE = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";
    public static final String BEARER_PREFIX = "Bearer ";

    private final Key key;

    @Getter
    @Value("${jwt.access-token-expiration-millis}")
    private long accessTokenExpirationMillis;

    @Getter
    @Value("${jwt.refresh-token-expiration-millis}")
    private long refreshTokenExpirationMillis;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    private Date getTokenExpiration(long expirationMillisecond) {
        Date date = new Date();

        return new Date(date.getTime() + expirationMillisecond);
    }
	
	/*
	public TokenDto generateTokenDto(CustomUserDetails customUserDetails) {
        Date accessTokenExpiresIn = getTokenExpiration(accessTokenExpirationMillis);
        Date refreshTokenExpiresIn = getTokenExpiration(refreshTokenExpirationMillis);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", customUserDetails.getRole());

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(customUserDetails.getEmail())
                .setExpiration(accessTokenExpiresIn)
                .setIssuedAt(Calendar.getInstance().getTime())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(customUserDetails.getEmail())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .authorizationType(AUTHORIZATION_HEADER)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }*/

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenDto generateToken(Authentication authentication) {
        Date accessTokenExpiresIn = getTokenExpiration(accessTokenExpirationMillis);
        Date refreshTokenExpiresIn = getTokenExpiration(refreshTokenExpirationMillis);

        TokenVo member = (TokenVo) authentication.getPrincipal();

        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getMemberNo()))
                .claim("memberNo", member.getMemberNo())
                .claim("memberSe", member.getMemberSe())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .setIssuedAt(Calendar.getInstance().getTime())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .authorizationType(AUTHORIZATION_HEADER)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDto generateChangeToken(MemberVo member) {
        // 토큰 만료 시간 설정
        Date accessTokenExpiresIn = getTokenExpiration(accessTokenExpirationMillis);
        Date refreshTokenExpiresIn = getTokenExpiration(refreshTokenExpirationMillis);

        // 권한 가져오기 - member 객체에서 직접 권한 정보를 가져오는 로직
        String authorities = "ROLE_USER";

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getMemberNo())) // 사용자 식별자
                .claim("memberNo", member.getMemberNo()) // 사용자 번호 정보 추가
                .claim("memberSe", member.getMemberSe()) // 사용자 유형 정보 추가
                .claim("auth", authorities) // 사용자 권한 정보 추가
                .setExpiration(accessTokenExpiresIn) // 토큰 만료 시간
                .setIssuedAt(Calendar.getInstance().getTime()) // 발행 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setIssuedAt(Calendar.getInstance().getTime()) // 발행 시간
                .setExpiration(refreshTokenExpiresIn) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘
                .compact();

        // TokenDto 반환
        return TokenDto.builder()
                .grantType(BEARER_TYPE) // 토큰 타입 설정
                .authorizationType(AUTHORIZATION_HEADER) // 인증 헤더 타입 설정
                .accessToken(accessToken) // 생성된 Access Token
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime()) // 만료 시간
                .refreshToken(refreshToken) // 생성된 Refresh Token
                .build();
    }


    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null || claims.get("memberNo") == null) {
            throw new BadCredentialsException("권한 정보가 없는 토큰입니다.");
        }

        // ID 
        int memberNo = claims.get("memberNo", Integer.class);
        String memberSe = claims.get("memberSe", String.class);
        //int customerNo = claims.get("customerNo", Integer.class);
        // 권한
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new) 
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        // UserDetails principal = new User(String.valueOf(memberId), "", authorities);
        CustomUserDetails principal = new CustomUserDetails(new User(String.valueOf(memberNo), "", authorities), memberSe);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token, HttpServletResponse response) {
        try {
            parseClaims(token);
        } catch (MalformedJwtException e) {
            e.printStackTrace();
            throw new TokenValidationException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            throw new TokenValidationException(HttpStatus.UNAUTHORIZED, "Expired JWT token");
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
            throw new TokenValidationException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new TokenValidationException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
        }
        return true;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch(ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // Request Header에 Access Token 정보를 추출하는 메서드
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Request Header에 Refresh Token 정보를 추출하는 메서드
    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESH_HEADER);
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }
}