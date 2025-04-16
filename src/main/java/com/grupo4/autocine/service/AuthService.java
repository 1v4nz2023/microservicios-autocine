package com.grupo4.autocine.service;

import com.grupo4.autocine.dto.LoginDTO;
import com.grupo4.autocine.dto.LoginResponseDTO;
import com.grupo4.autocine.exception.AuthenticationException;
import com.grupo4.autocine.model.PasswordResetToken;
import com.grupo4.autocine.model.Usuario;
import com.grupo4.autocine.repository.PasswordResetTokenRepository;
import com.grupo4.autocine.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    public LoginResponseDTO login(LoginDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Usuario y/o contraseña incorrecta"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())) {
            throw new AuthenticationException("Usuario y/o contraseña incorrecta");
        }

        LoginResponseDTO response = convertToLoginResponseDTO(usuario);
        response.setToken(jwtService.generateToken(response));
        return response;
    }

    private LoginResponseDTO convertToLoginResponseDTO(Usuario usuario) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        return dto;
    }

    @Transactional
    public void sendPasswordResetEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        // Generate a reset token
        String token = UUID.randomUUID().toString();
        
        // Create and save the token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpirationTime(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
        tokenRepository.save(resetToken);

        // Clean up old tokens
        tokenRepository.deleteExpiredAndUsedTokens(LocalDateTime.now());

        // Send the email
        emailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException("Invalid or expired reset token"));

        // Validate token
        if (resetToken.isUsed()) {
            throw new AuthenticationException("Reset token has already been used");
        }

        if (resetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("Reset token has expired");
        }

        // Update password
        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
} 